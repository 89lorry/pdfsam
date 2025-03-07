/*
 * This file is part of the PDF Split And Merge source code
 * Created on 10/set/2014
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
package org.pdfsam.tools.splitbysize;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.output.FileOrDirectoryTaskOutput;
import org.sejda.model.parameter.SplitBySizeParameters;
import org.sejda.model.pdf.PdfVersion;
import org.junit.jupiter.api.Assertions;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Andrea Vacondio
 */
public class SplitBySizeParametersBuilderTest {

    @Test
    public void build(@TempDir Path folder) throws IOException {
        //a new victim
        SplitBySizeParametersBuilder victim = new SplitBySizeParametersBuilder();

        //enable
        victim.compress(true);
        FileOrDirectoryTaskOutput output = mock(FileOrDirectoryTaskOutput.class);
        victim.output(output);
        victim.existingOutput(ExistingOutputPolicy.OVERWRITE);
        victim.size(120L);
        victim.prefix("prefix");
        victim.discardBookmarks(true);

        //create temp pdf
        PdfFileSource source = PdfFileSource.newInstanceNoPassword(Files.createTempFile(folder, null, ".pdf").toFile());
        victim.source(source);
        victim.version(PdfVersion.VERSION_1_7);

        //build
        SplitBySizeParameters params = victim.build();

        //assert
        assertTrue(params.isCompress());
        assertTrue(params.discardOutline());
        assertEquals(ExistingOutputPolicy.OVERWRITE, params.getExistingOutputPolicy());
        assertEquals(PdfVersion.VERSION_1_7, params.getVersion());
        assertEquals(120L, params.getSizeToSplitAt()); //120MB
        assertEquals("prefix", params.getOutputPrefix());
        assertEquals(output, params.getOutput());
        assertEquals(source, params.getSourceList().get(0));
    }

    @Test
    public void testSmallSize(@TempDir Path folder) {
        SplitBySizeParametersBuilder victim = new SplitBySizeParametersBuilder();
        victim.size(1L); // 1MB
        SplitBySizeParameters params = victim.build();
        assertEquals(1L, params.getSizeToSplitAt());
    }

    @Test
    public void testZeroSize(@TempDir Path folder) {
        SplitBySizeParametersBuilder victim = new SplitBySizeParametersBuilder();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            victim.size(0L); //0MB
        });
    }

    @Test
    public void testNegativeSize(@TempDir Path folder) {
        SplitBySizeParametersBuilder victim = new SplitBySizeParametersBuilder();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            victim.size(-1L); //-1MB
        });}

    @Test
    public void testSizeExceedsMaxLimit() {
        SplitBySizeParametersBuilder victim = new SplitBySizeParametersBuilder();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            victim.size(Long.MAX_VALUE + 1); // size exceeds long’s maximum value throw an exception
        });
    }

    @Test
    public void testInvalidSizeInput() {
        SplitBySizeParametersBuilder victim = new SplitBySizeParametersBuilder();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            victim.size(Long.parseLong("invalid"));
        });
    }
}



