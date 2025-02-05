package org.pdfsam.model.pdf;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PdfDescriptorLoadingStatusTest {

    //INITIAL
    @Test
    public void testInitialStateTransitions() {
        PdfDescriptorLoadingStatus status = PdfDescriptorLoadingStatus.INITIAL;
        assertTrue(status.canMoveTo(PdfDescriptorLoadingStatus.REQUESTED));
        assertTrue(status.canMoveTo(PdfDescriptorLoadingStatus.WITH_ERRORS));
        assertFalse(status.canMoveTo(PdfDescriptorLoadingStatus.LOADING));
    }

    //REQUESTED
    @Test
    public void testRequestedStateTransitions() {
        PdfDescriptorLoadingStatus status = PdfDescriptorLoadingStatus.REQUESTED;
        assertTrue(status.canMoveTo(PdfDescriptorLoadingStatus.LOADING));
        assertTrue(status.canMoveTo(PdfDescriptorLoadingStatus.WITH_ERRORS));
        assertFalse(status.canMoveTo(PdfDescriptorLoadingStatus.INITIAL));
    }

    //LOADING
    @Test
    public void testLoadingStateTransitions() {
        PdfDescriptorLoadingStatus status = PdfDescriptorLoadingStatus.LOADING;
        assertTrue(status.canMoveTo(PdfDescriptorLoadingStatus.LOADED));
        assertTrue(status.canMoveTo(PdfDescriptorLoadingStatus.LOADED_WITH_USER_PWD_DECRYPTION));
        assertTrue(status.canMoveTo(PdfDescriptorLoadingStatus.ENCRYPTED));
        assertTrue(status.canMoveTo(PdfDescriptorLoadingStatus.WITH_ERRORS));
        assertFalse(status.canMoveTo(PdfDescriptorLoadingStatus.INITIAL));
    }

    // ENCRYPTED
    @Test
    public void testEncryptedStateTransitions() {
        PdfDescriptorLoadingStatus status = PdfDescriptorLoadingStatus.ENCRYPTED;
        assertTrue(status.canMoveTo(PdfDescriptorLoadingStatus.REQUESTED));
        assertTrue(status.canMoveTo(PdfDescriptorLoadingStatus.WITH_ERRORS));
        assertFalse(status.canMoveTo(PdfDescriptorLoadingStatus.LOADING));
    }

    //  end status: LOADED, LOADED_WITH_USER_PWD_DECRYPTION, WITH_ERRORS）
    @Test
    public void testFinalStates() {
        PdfDescriptorLoadingStatus loadedStatus = PdfDescriptorLoadingStatus.LOADED;
        PdfDescriptorLoadingStatus decryptedStatus = PdfDescriptorLoadingStatus.LOADED_WITH_USER_PWD_DECRYPTION;
        PdfDescriptorLoadingStatus errorStatus = PdfDescriptorLoadingStatus.WITH_ERRORS;

        assertTrue(loadedStatus.isFinal());
        assertTrue(decryptedStatus.isFinal());
        assertTrue(errorStatus.isFinal());
    }

    //
    @Test(expected = IllegalStateException.class)
    public void testInvalidTransition() {
        PdfDescriptorLoadingStatus status = PdfDescriptorLoadingStatus.INITIAL;
        status.moveTo(PdfDescriptorLoadingStatus.LOADING);
    }
}
