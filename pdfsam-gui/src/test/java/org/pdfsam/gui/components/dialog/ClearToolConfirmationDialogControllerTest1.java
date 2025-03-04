package org.pdfsam.gui.components.dialog;

import jakarta.inject.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.pdfsam.core.context.ApplicationContext;
import org.pdfsam.core.context.ApplicationPersistentSettings;
import org.pdfsam.core.context.BooleanPersistentProperty;
import org.pdfsam.eventstudio.StaticStudio;
import org.pdfsam.model.tool.ClearToolRequest;
import org.pdfsam.eventstudio.EventStudio;
import org.pdfsam.eventstudio.DefaultEventStudio;
import org.pdfsam.gui.components.dialog.ClearToolConfirmationDialog;
import org.pdfsam.gui.components.dialog.ClearToolConfirmationDialogController;

import javafx.stage.Stage;
import java.util.Optional;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;
import static org.pdfsam.core.context.ApplicationContext.app;

public class ClearToolConfirmationDialogControllerTest1 {

    private ClearToolConfirmationDialogController controller;
    private Provider<ClearToolConfirmationDialog> mockDialogProvider;
    private ClearToolConfirmationDialog mockDialog;

    @BeforeEach
    public void setUp() {
        // Mock Dialog
        mockDialogProvider = mock(Provider.class);
        mockDialog = mock(ClearToolConfirmationDialog.class);
        when(mockDialogProvider.get()).thenReturn(mockDialog);
        when(mockDialog.clearEverything(anyBoolean())).thenReturn(mockDialog);
        when(mockDialog.response()).thenReturn(true); // Simulating user clicking "Yes"

        // Initialize Controller
        controller = new ClearToolConfirmationDialogController(mockDialogProvider);
    }

    @Test
    public void testEventIsBroadcastWhenUserConfirms() {
        try (MockedStatic<StaticStudio> mockStaticStudio = mockStatic(StaticStudio.class);
             MockedStatic<ApplicationContext> mockAppContext = mockStatic(ApplicationContext.class)) {
            // Mock EventStudio
            DefaultEventStudio mockStudio = mock(DefaultEventStudio.class);
            mockStaticStudio.when(StaticStudio::eventStudio).thenReturn(mockStudio);

            // Mock ApplicationContext
            ApplicationContext mockApp = mock(ApplicationContext.class);
            ApplicationPersistentSettings mockSettings = mock(ApplicationPersistentSettings.class);
            mockAppContext.when(ApplicationContext::app).thenReturn(mockApp);
            when(mockApp.persistentSettings()).thenReturn(mockSettings);
            when(mockSettings.get(BooleanPersistentProperty.CLEAR_CONFIRMATION)).thenReturn(true);

            // Given a ClearToolRequest
            ClearToolRequest request = new ClearToolRequest("module", true, true);

            // When the request method is called
            controller.request(request);

            // Verify that the dialog was shown and "Yes" was selected
            verify(mockDialog).clearEverything(true);
            verify(mockDialog).response();

            // Verify that an event was broadcast
            verify(mockStudio, times(1)).broadcast(request, request.toolBinding());
        }
    }

    @Test
    public void testNoEventBroadcastWhenUserClicksNo() {
        try (MockedStatic<StaticStudio> mockStaticStudio = mockStatic(StaticStudio.class)) {
            DefaultEventStudio mockStudio = mock(DefaultEventStudio.class); // Use DefaultEventStudio
            mockStaticStudio.when(StaticStudio::eventStudio).thenReturn(mockStudio);

            // Simulate user clicking "No"
            when(mockDialog.response()).thenReturn(false);

            // Given a ClearToolRequest
            ClearToolRequest request = new ClearToolRequest("module", true, true);

            // When the request method is called
            controller.request(request);

            // Verify that the event was NOT broadcast
            verify(mockStudio, never()).broadcast(any(), any());
        }
    }
}