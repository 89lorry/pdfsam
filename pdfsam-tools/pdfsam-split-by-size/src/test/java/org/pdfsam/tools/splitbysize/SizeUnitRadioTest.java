package org.pdfsam.tools.splitbysize;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class SizeUnitRadioTest {


    @Test
    public void onSaveState() {
        SizeUnitRadioLogic logic = new SizeUnitRadioLogic(SizeUnit.KILOBYTE);
        Map<String, String> data = new HashMap<>();
        logic.saveStateTo(data, true);
        assertTrue(Boolean.parseBoolean(data.get(SizeUnit.KILOBYTE.toString())));
    }

    @Test
    public void onSaveStateNotSelected() {
        SizeUnitRadioLogic logic = new SizeUnitRadioLogic(SizeUnit.MEGABYTE);
        Map<String, String> data = new HashMap<>();
        logic.saveStateTo(data, false);
        assertFalse(data.containsKey(SizeUnit.MEGABYTE.toString()));
    }

    @Test
    public void onRestoreState() {
        SizeUnitRadioLogic logic = new SizeUnitRadioLogic(SizeUnit.MEGABYTE);
        Map<String, String> data = new HashMap<>();
        data.put(SizeUnit.MEGABYTE.toString(), Boolean.TRUE.toString());
        assertTrue(logic.restoreStateFrom(data));
    }
}
