package org.pdfsam.service.task;

 import javafx.application.Platform;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pdfsam.model.lifecycle.ShutdownEvent;
import org.pdfsam.model.tool.TaskExecutionRequest;
import org.pdfsam.service.tool.UsageService;
import org.sejda.core.service.TaskExecutionService;
import org.sejda.model.parameter.base.TaskParameters;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import org.sejda.model.parameter.base.AbstractParameters;
import org.sejda.model.notification.event.TaskExecutionStartedEvent;
import org.sejda.model.notification.event.TaskExecutionCompletedEvent;
import org.sejda.model.task.NotifiableTaskMetadata;




class TaskExecutionControllerTest {

    @Mock private TaskExecutionService executionService;
    @Mock private UsageService usageService;
    @Mock private TaskExecutionRequest request;
    @Mock private TaskParameters taskParameters;
    @Mock private ExecutorService executorServiceMock;
    private TaskExecutionController controller;

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // JavaFX is already started
        }
    }
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new TaskExecutionController(executionService, usageService);

        Field executorField = TaskExecutionController.class.getDeclaredField("executor");
        executorField.setAccessible(true);
        executorField.set(controller, executorServiceMock);
    }


    @Test
    void testShutdown() {
        controller.onShutdown(new ShutdownEvent());
        verify(executorServiceMock).shutdownNow();
    }

    @Test
    public void request() {
        var toolId = "tool";
        AbstractParameters params = mock(AbstractParameters.class);
        controller.request(new TaskExecutionRequest(toolId, params));
        verify(usageService).incrementUsageFor(toolId);
        verify(executorServiceMock, timeout(1000).times(1)).execute(any(Runnable.class));
    }

    @Test
    public void onEventTaskEventBroadcaster() {
        var toolId = "tool";
        AbstractParameters params = mock(AbstractParameters.class);
        controller.request(new TaskExecutionRequest(toolId, params));
        TaskExecutionController.TaskEventBroadcaster<TaskExecutionStartedEvent> broadcaster = controller.new TaskEventBroadcaster<>();
        TaskExecutionStartedEvent event = new TaskExecutionStartedEvent(null);
        org.pdfsam.eventstudio.Listener<TaskExecutionStartedEvent> listener = mock(org.pdfsam.eventstudio.Listener.class);
        org.pdfsam.eventstudio.StaticStudio.eventStudio().add(TaskExecutionStartedEvent.class, listener);
        org.pdfsam.eventstudio.Listener<TaskExecutionStartedEvent> listenerTool = mock(org.pdfsam.eventstudio.Listener.class);
        org.pdfsam.eventstudio.StaticStudio.eventStudio().add(TaskExecutionStartedEvent.class, listenerTool, toolId);
        broadcaster.onEvent(event);
        verify(listener, timeout(1000).times(1)).onEvent(event);
        verify(listenerTool, timeout(1000).times(1)).onEvent(event);
    }

    @Test
    public void onTaskExecutionCompletedEvent() throws InterruptedException {
        TaskExecutionCompletedEvent event = mock(TaskExecutionCompletedEvent.class);
        NotifiableTaskMetadata metadata = mock(NotifiableTaskMetadata.class);
        File mockFile = mock(File.class);

        // Ensure getNotifiableTaskMetadata is called
        when(event.getNotifiableTaskMetadata()).thenReturn(metadata);
        when(metadata.skippedOutput()).thenReturn(List.of(mockFile));
        when(mockFile.getName()).thenReturn("skipped-file.pdf");

        TaskExecutionController.TaskEventBroadcaster<TaskExecutionCompletedEvent> broadcaster = controller.new TaskEventBroadcaster<>();

        Platform.runLater(() -> {
            broadcaster.onEvent(event);
            synchronized (this) {
                this.notify(); // Notify test thread
            }
        });

        synchronized (this) {
            this.wait(); // Ensure JavaFX execution before verification
        }

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(metadata, times(1)).skippedOutput();
            verify(mockFile, times(1)).getName();
        });
    }
}
