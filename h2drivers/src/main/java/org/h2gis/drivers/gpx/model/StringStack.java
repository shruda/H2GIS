/*
 * h2spatial is a library that brings spatial support to the H2 Java database.
 *
 * h2spatial is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 *
 * h2patial is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * h2spatial is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * h2spatial. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.h2gis.drivers.gpx.model;

/**
 * This class is a stack to keep in memory the structure of the GPX file.
 *
 * @author Erwan Bocher and Antonin Piasco
 */
public final class StringStack {

    private String[] stack;
    // Indicates the level of the stack
    private int stackTop = 0;

    /**
     * Instantiates a StringStack with chosen max size.
     *
     * @param capacity max size
     */
    public StringStack(int capacity) {
        stack = new String[capacity];
    }

    /**
     * Puts string in the stack.
     *
     * @param newText A new string to put in the stack
     * @return false if the stack is full
     */
    public boolean push(String newText) {
        if (stackTop < stack.length - 1) {
            stack[stackTop++] = newText;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes the last string of the stack.
     *
     * @return the last string of the stack, null if the stack is empty
     */
    public String pop() {
        if (stackTop == 0) {
            return (null);
        } else {
            return (stack[--stackTop]);
        }
    }
}