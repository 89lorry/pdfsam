package org.pdfsam.tools.splitbysize;

import javafx.scene.control.RadioButton;
import org.pdfsam.model.ui.workspace.RestorableView;

import java.util.Map;

import static org.sejda.commons.util.RequireUtils.requireNotNullArg;

/**
 * Radio button for {@link SizeUnit}, refactored for better testability.
 */
class SizeUnitRadio extends RadioButton implements RestorableView {
    private final SizeUnit unit;
    private final SizeUnitRadioLogic logic;

    public SizeUnitRadio(SizeUnit unit) {
        requireNotNullArg(unit, "Unit cannot be null");
        this.unit = unit;
        this.logic = new SizeUnitRadioLogic(unit);
        initializeUI();
    }

    /** Initialize UI elements */
    private void initializeUI() {
        this.setText(unit.friendlyName());
        this.setId("unit" + unit.symbol());
    }

    @Override
    public void saveStateTo(Map<String, String> data) {
        logic.saveStateTo(data, isSelected());
    }

    @Override
    public void restoreStateFrom(Map<String, String> data) {
        boolean isSelected = logic.restoreStateFrom(data);
        this.setSelected(isSelected);
    }

    public SizeUnit unit() {
        return unit;
    }
}

/**
 * Logic class for handling SizeUnitRadio state, decoupled from UI for testability.
 */
class SizeUnitRadioLogic {
    private final SizeUnit unit;

    public SizeUnitRadioLogic(SizeUnit unit) {
        this.unit = unit;
    }

    public void saveStateTo(Map<String, String> data, boolean isSelected) {
        if (isSelected) {
            data.put(unit.toString(), Boolean.TRUE.toString());
        }
    }

    public boolean restoreStateFrom(Map<String, String> data) {
        return Boolean.parseBoolean(data.getOrDefault(unit.toString(), "false"));
    }
}
