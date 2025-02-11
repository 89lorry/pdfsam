package org.pdfsam.theme;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LightMaroonTest {

    private LightMaroon lightMaroonTheme;

    @BeforeEach
    void setUp() {
        lightMaroonTheme = new LightMaroon();
    }

    @Test
    void testId() {
        assertEquals("KLDJHGH3N21A1Z", lightMaroonTheme.id());
    }

    @Test
    void testStylesheets() {
        List<String> stylesheets = lightMaroonTheme.stylesheets();
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
        assertTrue(stylesheets.contains("/themes/lightmaroon/colors.css"));
    }


    @Test
    void testIsDark() {
        assertFalse(lightMaroonTheme.isDark());
    }
}
