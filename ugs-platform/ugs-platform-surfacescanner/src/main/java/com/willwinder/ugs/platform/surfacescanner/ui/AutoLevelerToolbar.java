/*
    Copyright 2023 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.ugs.platform.surfacescanner.ui;

import com.willwinder.ugs.nbp.core.ui.ToolBar;
import com.willwinder.ugs.platform.surfacescanner.SurfaceScanner;
import com.willwinder.ugs.platform.surfacescanner.actions.*;

import javax.swing.*;

/**
 * A toolbar with all actions for the auto leveler
 */
public class AutoLevelerToolbar extends ToolBar {

    public AutoLevelerToolbar(SurfaceScanner surfaceScanner) {
        setFloatable(false);
        add(new JButton(new OpenScannedSurfaceAction(surfaceScanner)));
        add(new JButton(new SaveScannedSurfaceAction(surfaceScanner)));
        add(new JButton(new ClearScannedSurfaceAction(surfaceScanner)));
        add(Box.createGlue());
        add(new JButton(new GenerateTestDataAction(surfaceScanner)));
        add(new JButton(new OpenSettingsAction()));
    }
}
