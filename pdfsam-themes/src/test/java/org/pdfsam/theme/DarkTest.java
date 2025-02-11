package org.pdfsam.theme;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DarkTest {

    private Dark darkTheme;

    @BeforeEach
    void setUp() {
        darkTheme = new Dark();
    }

    @Test
    void testId() {
        assertEquals("KDJ4FJ49D46H09JV1", darkTheme.id());
    }

    @Test
    void testStylesheets() {
        List<String> stylesheets = darkTheme.stylesheets();
        assertNotNull(stylesheets);
        assertTrue(stylesheets.contains("/themes/dark/colors.css"));
        assertTrue(stylesheets.contains("/themes/dark/theme.css"));
    }


    @Test
    void testIsDark() {
        assertTrue(darkTheme.isDark());
    }
}
