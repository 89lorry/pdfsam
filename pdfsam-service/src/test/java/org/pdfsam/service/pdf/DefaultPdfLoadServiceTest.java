package org.pdfsam.service.pdf;
import java.lang.reflect.Method;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pdfsam.model.pdf.PdfDescriptorLoadingStatus;
import org.pdfsam.model.pdf.PdfDocumentDescriptor;
import org.pdfsam.model.tool.RequiredPdfData;
import org.sejda.io.BufferedSeekableSource;
import org.sejda.io.FileChannelSeekableSource;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import java.io.File;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.*;

class DefaultPdfLoadServiceTest {

    private DefaultPdfLoadService service;

    @Mock private PdfDocumentDescriptor descriptor;
    @Mock private PDDocument mockDocument;
    @Mock private File mockFile;
    @Mock private PdfLoader<PDDocument> mockLoader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockLoader.key()).thenReturn(RequiredPdfData.DEFAULT);
        service = new DefaultPdfLoadService(List.of(mockLoader));
    }



    @Test
    void testSkipInvalidDocument() {
        when(descriptor.hasReferences()).thenReturn(false);
        service.load(Collections.singleton(descriptor), RequiredPdfData.DEFAULT);
        verify(descriptor, never()).moveStatusTo(any());
    }

}
