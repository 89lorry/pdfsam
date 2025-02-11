package org.pdfsam.theme;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DarkTealTest {

    private DarkTeal darkTealTheme;

    @BeforeEach
    void setUp() {
        darkTealTheme = new DarkTeal();
    }

    @Test
    void testId() {
        assertEquals("K3DD49ASD30A1P", darkTealTheme.id());
    }

    @Test
    void testStylesheets() {
        List<String> stylesheets = darkTealTheme.stylesheets();
        assertNotNull(stylesheets);
        assertTrue(stylesheets.contains("/themes/dark/colors.css"));
        assertTrue(stylesheets.contains("/themes/dark/theme.css"));
        assertTrue(stylesheets.contains("/themes/darkteal/colors.css"));
    }


    @Test
    void testIsDark() {
        assertTrue(darkTealTheme.isDark());
    }
}
