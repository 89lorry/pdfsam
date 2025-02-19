package org.pdfsam.service.pdf;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pdfsam.model.pdf.PdfDescriptorLoadingStatus;
import org.pdfsam.model.pdf.PdfDocumentDescriptor;
import org.pdfsam.model.tool.RequiredPdfData;
import org.sejda.io.BufferedSeekableSource;
import org.sejda.io.FileChannelSeekableSource;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.encryption.InvalidPasswordException;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static org.mockito.Mockito.*;

class DefaultPdfLoadServiceTest {

    private DefaultPdfLoadService service;

    @Mock private PdfDocumentDescriptor descriptor;
    @Mock private PDDocument mockDocument;
    @Mock private File mockFile;
    @Mock private PdfLoader<PDDocument> mockLoader;

    private BiConsumer<PDDocument, PdfDocumentDescriptor> finisher;

    @BeforeAll
    static void initJavaFX() {
        if (!Platform.isFxApplicationThread()) {
            new Thread(() -> Platform.startup(() -> {})).start();
        }
    }

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        when(mockLoader.key()).thenReturn(RequiredPdfData.DEFAULT);
        service = new DefaultPdfLoadService(List.of(mockLoader));

        // Access private static FINISHER using reflection
        Field finisherField = DefaultPdfLoadService.class.getDeclaredField("FINISHER");
        finisherField.setAccessible(true);
        finisher = (BiConsumer<PDDocument, PdfDocumentDescriptor>) finisherField.get(null);

    }

    @Test
    void testFinisherWithPassword() {
        when(descriptor.hasPassword()).thenReturn(true);

        // Invoke the private FINISHER function using reflection
        finisher.accept(mockDocument, descriptor);

        // Verify expected status update
        Platform.runLater(() -> verify(descriptor, timeout(1000)).moveStatusTo(PdfDescriptorLoadingStatus.LOADED_WITH_USER_PWD_DECRYPTION));
    }

    @Test
    void testFinisherWithoutPassword() {
        when(descriptor.hasPassword()).thenReturn(false);

        // Invoke the private FINISHER function using reflection
        finisher.accept(mockDocument, descriptor);

        // Verify expected status update
        Platform.runLater(() -> verify(descriptor, timeout(1000)).moveStatusTo(PdfDescriptorLoadingStatus.LOADED));
    }

    @Test
    void testSkipInvalidDocument() {
        when(descriptor.hasReferences()).thenReturn(false);
        service.load(Collections.singleton(descriptor), RequiredPdfData.DEFAULT);
        verify(descriptor, never()).moveStatusTo(any());
    }

    @Test
    void testHandleEncryptedPdf() throws Exception {
        when(descriptor.hasReferences()).thenReturn(true);
        when(descriptor.getFile()).thenReturn(mockFile);
        when(descriptor.getPassword()).thenReturn("wrong-password");

        BufferedSeekableSource mockSource = mock(BufferedSeekableSource.class);

        // Use Mockito's `mock()` to create a mock exception
        InvalidPasswordException mockException = mock(InvalidPasswordException.class);

        try (var mockedParser = mockStatic(PDFParser.class)) {
            mockedParser.when(() -> PDFParser.parse(any(BufferedSeekableSource.class), anyString()))
                    .thenThrow(mockException);

            service.load(Collections.singleton(descriptor), RequiredPdfData.DEFAULT);
        }

        // Verify that the status is moved to ENCRYPTED
        Platform.runLater(() -> verify(descriptor, timeout(1000)).moveStatusTo(PdfDescriptorLoadingStatus.ENCRYPTED));
    }


    @Test
    void testHandleGeneralException() throws Exception {
        when(descriptor.hasReferences()).thenReturn(true);
        when(descriptor.getFile()).thenReturn(mockFile);
        when(descriptor.getPassword()).thenReturn(null);

        BufferedSeekableSource mockSource = mock(BufferedSeekableSource.class);

        // Use Mockito's `mock()` to create a mock exception
        InvalidPasswordException mockException = mock(InvalidPasswordException.class);

        // Mock the static method `PDFParser.parse`
        try (var mockedParser = mockStatic(PDFParser.class)) {
            mockedParser.when(() -> PDFParser.parse(any(BufferedSeekableSource.class), anyString()))
                    .thenThrow(new RuntimeException("Unexpected error"));

            service.load(Collections.singleton(descriptor), RequiredPdfData.DEFAULT);
        }

        // Ensure descriptor status moves to WITH_ERRORS
        Platform.runLater(() -> verify(descriptor, timeout(1000)).moveStatusTo(PdfDescriptorLoadingStatus.WITH_ERRORS));
    }

    @Test
    void testFxMoveStatusTo() {
        when(descriptor.hasReferences()).thenReturn(true);
        when(descriptor.getFile()).thenReturn(mockFile);

        service.load(Collections.singleton(descriptor), RequiredPdfData.DEFAULT);

        // Ensure that moveStatusTo() is eventually called inside Platform.runLater
        Awaitility.await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(descriptor, atLeastOnce()).moveStatusTo(any(PdfDescriptorLoadingStatus.class));
        });
    }

}
