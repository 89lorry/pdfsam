package org.pdfsam.tools.splitbysize;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pdfsam.model.tool.ToolDescriptor;

import static org.junit.jupiter.api.Assertions.*;

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
}
