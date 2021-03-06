/**
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
package org.h2gis.h2spatialapi;

import java.beans.PropertyChangeListener;

/**
 * Progression information.
 * @author Nicolas Fortin
 */
public interface ProgressVisitor {

    public static String PROPERTY_CANCELED = "CANCELED";

    /***
     * Create a sub process. When this sub process finish it will count as a single step in this process.
     * @param stepCount Number of step into the sub-process.
     * @return
     */
    ProgressVisitor subProcess(int stepCount);

    /**
     * Same as {@link ProgressVisitor#setStep(int)} with currentStep++
     */
    void endStep();

    /**
     * @param idStep Set the current step, must be in [0-stepCount]
     */
    void setStep(int idStep);

    /**
     * @return The step count of this progress
     */
    int getStepCount();

    /**
     * Same thing as call {@link ProgressVisitor#setStep(int)} with step count.
     */
    void endOfProgress();

    /**
     * @return This step progression [O-1], take account sub process progression.
     */
    double getProgression();

    /**
     * @return True if the process has been canceled
     */
    boolean isCanceled();

    /**
     * Call this method to cancel the operation
     */
    void cancel();

    /**
     * Listen for this visitor properties
     * @param property Property name one of {@link #PROPERTY_CANCELED}
     * @param listener Listener instance
     */
    void addPropertyChangeListener(String property, PropertyChangeListener listener);

    /**
     *
     * @param listener Listener instance
     */
    void removePropertyChangeListener(PropertyChangeListener listener);
}
