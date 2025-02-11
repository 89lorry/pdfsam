package org.pdfsam.service.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pdfsam.model.lifecycle.ShutdownEvent;
import org.pdfsam.model.tool.TaskExecutionRequest;
import org.pdfsam.service.tool.UsageService;
import org.sejda.core.service.TaskExecutionService;
import org.sejda.model.parameter.base.TaskParameters;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;

class TaskExecutionControllerTest {

    @Mock private TaskExecutionService executionService;
    @Mock private UsageService usageService;
    @Mock private TaskExecutionRequest request;
    @Mock private TaskParameters taskParameters;
    @Mock private ExecutorService executorServiceMock;

    private TaskExecutionController controller;

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


}
