package org.pdfsam.tools.splitbysize;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.pdfsam.model.tool.ToolDescriptor;
import org.pdfsam.ui.components.io.BrowsableOutputDirectoryField;
import org.pdfsam.ui.components.io.PdfDestinationPane;
import org.pdfsam.ui.components.tool.Footer;
import org.pdfsam.ui.components.tool.OpenButton;
import org.pdfsam.ui.components.tool.RunButton;
import org.pdfsam.persistence.PreferencesRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SplitBySizeToolTest {

    private SplitBySizeTool tool;

    @BeforeEach
    void setUp() {
        tool = new SplitBySizeTool();
    }

    @Test
    void testId() {
        assertEquals("split.bysize", tool.id());
    }

    @Test
    void testGraphic() {
        Node graphic = tool.graphic();
        assertNotNull(graphic);
    }

    @Test
    void testDescriptor() {
        ToolDescriptor descriptor = tool.descriptor();
        assertNotNull(descriptor);
        assertTrue(
                descriptor.name().equals("Split by size") || descriptor.name().equals("按大小分割"),
                "Unexpected tool name: " + descriptor.name()
        );
    }


    @Test
    void testPanel() {
        try (MockedStatic<org.pdfsam.core.context.ApplicationContext> appContextMock = Mockito.mockStatic(org.pdfsam.core.context.ApplicationContext.class)) {
            SplitBySizeToolPanel mockPanel = mock(SplitBySizeToolPanel.class);
            org.pdfsam.core.context.ApplicationContext mockAppContext = mock(org.pdfsam.core.context.ApplicationContext.class);

            when(mockAppContext.instance(SplitBySizeToolPanel.class)).thenReturn(mockPanel);
            appContextMock.when(org.pdfsam.core.context.ApplicationContext::app).thenReturn(mockAppContext);

            Pane panel = tool.panel();
            assertNotNull(panel);
            assertEquals(mockPanel, panel);
        }
    }



}
