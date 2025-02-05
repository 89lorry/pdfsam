/*
 * This file is part of the PDF Split And Merge source code
 * Created on 14/giu/2013
 * Copyright 2017 by Sober Lemur S.r.l. (info@soberlemur.com).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pdfsam.model.pdf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.pdf.PdfVersion;

import java.io.File;
import java.util.HashMap;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Andrea Vacondio
 */
public class PdfDocumentDescriptorTest {

    private PdfDocumentDescriptor victim;
    private PdfDocumentDescriptor victimNoPwd;
    private File file;

    @BeforeEach
    public void setUp() {
        file = mock(File.class); //Creates mock file objects to simulate a real PDF file.
        when(file.getName()).thenReturn("myName");
        when(file.isFile()).thenReturn(true);
        victim = PdfDocumentDescriptor.newDescriptor(file, "pwd"); //A descriptor with a password
        victimNoPwd = PdfDocumentDescriptor.newDescriptorNoPassword(file); //without password
    }

    @Test
    public void illegal() {
        //handle the illegal argument
        //Prevents creating a descriptor for a null file
        assertThrows(IllegalArgumentException.class, () -> PdfDocumentDescriptor.newDescriptorNoPassword(null));
    }

    @Test
    public void initialState() {
        assertTrue(victim.hasReferences());
        assertEquals(PdfDescriptorLoadingStatus.INITIAL, victim.loadingStatus().getValue());    //Checks that the descriptor starts in the INITIAL state.
        assertEquals("pwd", victim.getPassword());  //Confirms that the correct password and file name are stored.
        assertEquals("myName", victim.getFileName());
        assertNull(victimNoPwd.getPassword());  //Ensures that victimNoPwd correctly stores null for its password.
    }

    @Test
    public void invalidate() {
        victim.retain().retain();
        assertTrue(victim.hasReferences());     //Simulates increasing and decreasing reference counts.
        victim.releaseAll();
        assertFalse(victim.hasReferences());    //Ensures that calling releaseAll() removes all references and properly resets the state.
    }

    @Test
    public void retainAndRelease() {
        assertFalse(victim.retain().retain().release());
    }
    //Calls retain() multiple times to increase the reference count.
    //Calls release() and expects a false return value if references are still held

    @Test
    public void noVersionString() {
        assertEquals("", victim.getVersionString());
    }
    //Ensures that, by default, the version string is empty.

    @Test
    public void getVersionString() {
        victim.setVersion(PdfVersion.VERSION_1_5); //Sets a PDF version (1.5) and ensures that the version string is no longer empty.
        assertFalse(isBlank(victim.getVersionString()));
    }

    @Test
    public void moveValidStatus() {
        //State Transition Tests
        assertEquals(PdfDescriptorLoadingStatus.INITIAL, victim.loadingStatus().getValue());
        victim.moveStatusTo(PdfDescriptorLoadingStatus.REQUESTED);
        assertEquals(PdfDescriptorLoadingStatus.REQUESTED, victim.loadingStatus().getValue());
    }

    @Test
    public void moveInvalidStatus() {
        //Checks that skipping REQUESTED and moving directly to LOADING causes an IllegalStateException.
        assertEquals(PdfDescriptorLoadingStatus.INITIAL, victim.loadingStatus().getValue());
        assertThrows(IllegalStateException.class, () -> victim.moveStatusTo(PdfDescriptorLoadingStatus.LOADING));
    }

    @Test
    public void toPdfSource() {
        PdfFileSource source = victim.toPdfFileSource();
        assertEquals(file, source.getSource()); //Ensures that the descriptor correctly converts into a PdfFileSource.
        assertEquals("pwd", source.getPassword()); //Checks that the file reference and password are correctly set.
    }

    @Test
    public void FailToPdfSource() {
        //If the file is invalid, toPdfFileSource() should throw a ConversionException.
        //Ensures that conversion does not proceed with bad input.
        when(file.isFile()).thenReturn(Boolean.FALSE);
        assertThrows(ConversionException.class, () -> victim.toPdfFileSource());
    }

    @Test
    public void informationDictionary() {
        //Stores a key-value pair in the information dictionary.
        //Ensures that getInformation("key") correctly retrieves the stored value
        HashMap<String, String> values = new HashMap<>();
        values.put("key", "value");
        victim.setInformationDictionary(values);
        assertEquals("value", victim.getInformation("key"));
    }

    @Test
    public void putInformation() {
        //Ensures that calling putInformation() correctly updates the information dictionary.
        victim.putInformation("key", "value");
        assertEquals("value", victim.getInformation("key"));
    }

}
