/*
    Copyright 2020 Will Winder

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
package com.willwinder.universalgcodesender.gcode.processors;

import com.google.common.collect.ImmutableList;
import com.willwinder.universalgcodesender.gcode.GcodeParser;
import com.willwinder.universalgcodesender.gcode.GcodeState;
import com.willwinder.universalgcodesender.gcode.util.GcodeParserException;
import com.willwinder.universalgcodesender.model.Position;

import java.util.Collections;
import java.util.List;

import static com.willwinder.universalgcodesender.gcode.GcodePreprocessorUtils.normalizeCommand;

public class RunFromProcessor implements CommandProcessor {
    private int lineNumber;
    private GcodeParser parser;
    private Double clearanceHeight = 0.0;

    /**
     * Truncates gcode to the specified line, and rewrites the preamble with the GcodeState.
     *
     * @param lineNum line where the program should run from.
     */
    public RunFromProcessor(int lineNum) {
        lineNumber = lineNum;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public List<String> processCommand(String command, GcodeState state) throws GcodeParserException {
        // The processor is not activated
        if (lineNumber <= 0) {
            return Collections.singletonList(command);
        }

        if (state.commandNumber < lineNumber) {
            return skipLine(command);
        } else if (state.commandNumber == lineNumber && parser != null) {
            return getSkippedLinesState(command);
        }

        return ImmutableList.of(command);
    }

    private ImmutableList<String> getSkippedLinesState(String command) {
        Position pos = parser.getCurrentState().currentPoint;
        String moveToClearanceHeight = "G0Z" + clearanceHeight;
        String moveToXY = "G0X" + pos.x + "Y" + pos.y;
        String plunge = "G1Z" + pos.z;

        GcodeState s = parser.getCurrentState();
        String normalized = command;
        try {
            normalized = normalizeCommand(command, s);
        } catch (GcodeParserException e) {
            // If command couldn't be normalized, send as is
        }

        // Reset the parser to prevent the state to be re-added
        parser = null;

        return ImmutableList.of(
                // Initialize state
                s.machineStateCode(),

                // Move to start location
                moveToClearanceHeight,
                moveToXY,

                // Start spindle and set feed/speed before plunging into the work.
                s.toAccessoriesCode(),
                plunge,

                // Append normalized command
                normalized
        );
    }

    private ImmutableList<String> skipLine(String command) throws GcodeParserException {
        createParser();
        parser.addCommand(command);
        Position pos = parser.getCurrentState().currentPoint;
        clearanceHeight = Math.max(clearanceHeight, pos.z);
        return ImmutableList.of();
    }

    private void createParser() {
        if (parser == null) {
            parser = new GcodeParser();
        }
    }

    @Override
    public String getHelp() {
        return null;
    }
}
