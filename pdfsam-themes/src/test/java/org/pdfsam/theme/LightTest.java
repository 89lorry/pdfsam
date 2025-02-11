package org.pdfsam.theme;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LightTest {

    private Light lightTheme;

    @BeforeEach
    void setUp() {
        lightTheme = new Light();
    }

    @Test
    void testId() {
        assertEquals("AS876FDS7RB3", lightTheme.id());
    }

    @Test
    void testStylesheets() {
        List<String> stylesheets = lightTheme.stylesheets();
        assertNotNull(stylesheets);
        assertTrue(stylesheets.contains("/themes/light/colors.css"));
        assertTrue(stylesheets.contains("/themes/light/theme.css"));
        assertTrue(stylesheets.contains("/themes/light/tooltip.css"));
        assertTrue(stylesheets.contains("/themes/light/progress.css"));
        assertTrue(stylesheets.contains("/themes/light/list.css"));
        assertTrue(stylesheets.contains("/themes/light/news.css"));
        assertTrue(stylesheets.contains("/themes/light/dialogs.css"));
        assertTrue(stylesheets.contains("/themes/light/combo.css"));
        assertTrue(stylesheets.contains("/themes/light/scrollbars.css"));
        assertTrue(stylesheets.contains("/themes/light/notifications.css"));
        assertTrue(stylesheets.contains("/themes/light/dashboard.css"));
        assertTrue(stylesheets.contains("/themes/light/menu.css"));
        assertTrue(stylesheets.contains("/themes/light/table.css"));
        assertTrue(stylesheets.contains("/themes/light/sidebar.css"));
        assertTrue(stylesheets.contains("/themes/light/logs.css"));
        assertTrue(stylesheets.contains("/themes/light/theme.last.css"));
    }

    @Test
    void testTransparentIncapableStylesheets() {
        List<String> stylesheets = lightTheme.transparentIncapableStylesheets();
        assertNotNull(stylesheets);
        assertEquals(1, stylesheets.size());
        assertTrue(stylesheets.contains("/themes/light/transparent-incapable.css"));
    }


    @Test
    void testIsDark() {
        assertFalse(lightTheme.isDark());
    }
}
