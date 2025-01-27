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
package org.pdfsam.tools.extract;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pdfsam.core.support.params.ConversionUtils;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.output.FileOrDirectoryTaskOutput;
import org.sejda.model.parameter.ExtractPagesParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.page.PagesSelection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Andrea Vacondio
 *
 */
public class ExtractParametersBuilderTest {

    @Test  //JUnit Test
    public void build(@TempDir Path folder) throws IOException {
        var victim = new ExtractParametersBuilder(); //victim is the test object

        //enable main function
        victim.compress(true); // enable compression (true) for the extracted PDF
        FileOrDirectoryTaskOutput output = mock(FileOrDirectoryTaskOutput.class);
        victim.output(output); //use Mockito to create a mock object to simulate the output file
        victim.existingOutput(ExistingOutputPolicy.OVERWRITE);
        victim.discardBookmarks(true); //enable the discarding of PDF bookmarks (true)
        victim.prefix("prefix"); //set the prefix for the output files
        victim.invertSelection(true); //enable inverted page selection
        victim.separateForEachRange(true); //generate a separate output file for each range of pages

        //create a temporary pdf
        PdfFileSource source = PdfFileSource.newInstanceNoPassword(Files.createTempFile(folder, null, ".pdf").toFile());
        victim.addSource(source);
        victim.addSource(PdfFileSource.newInstanceNoPassword(Files.createTempFile(folder, null, ".pdf").toFile()));

        //set the pdf
        victim.version(PdfVersion.VERSION_1_7);//set a version
        Set<PagesSelection> ranges = ConversionUtils.toPagesSelectionSet("2,5-20,33,45,last");//select the page number
        victim.pagesSelection(ranges);
        ExtractPagesParameters params = victim.build(); //build the extraction parameters

        //validate the parameters
        assertTrue(params.isCompress());
        assertTrue(params.discardOutline());
        assertEquals(ExistingOutputPolicy.OVERWRITE, params.getExistingOutputPolicy());
        assertEquals(PdfVersion.VERSION_1_7, params.getVersion());
        assertEquals(OptimizationPolicy.AUTO, params.getOptimizationPolicy());
        assertTrue(params.hasPageSelection());
        assertEquals(output, params.getOutput());
        assertTrue(params.isInvertSelection());
        assertTrue(params.isSeparateFileForEachRange());
        assertEquals("prefix", params.getOutputPrefix());
        assertEquals(2, params.getSourceList().size());
        assertEquals(source, params.getSourceList().get(0));
        System.out.println("Assertions passed. Test completed");
    }
}
