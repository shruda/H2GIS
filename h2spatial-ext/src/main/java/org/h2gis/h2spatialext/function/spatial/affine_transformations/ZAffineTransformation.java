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
package org.h2gis.h2spatialext.function.spatial.affine_transformations;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;

/**
 * Applies a 3D affine transformation to a geometry. 
 * That means a rotation, scale and translate in a single step.
 * 
 * @author Erwan Bocher
 */
public class ZAffineTransformation implements Cloneable, CoordinateSequenceFilter {

    // affine matrix entries
    // (bottom row is always [ 0 0 1 ])
    private final double m00;
    private final double m01;
    private final double m02;
    private final double m10;
    private final double m11;
    private final double m12;
    private final double m20;
    private final double m21;
    private final double m22;
    private final double m03;
    private final double m13;
    private final double m23;

    /**
     * Constructs a new transformation whose matrix has the specified values.
     *
     * @param m00 the entry for the [0, 0] element in the transformation matrix
     * @param m01 the entry for the [0, 1] element in the transformation matrix
     * @param m02 the entry for the [0, 2] element in the transformation matrix
     * @param m03 the entry for the [0, 3] element in the transformation matrix
     * @param m10 the entry for the [1, 0] element in the transformation matrix
     * @param m11 the entry for the [1, 1] element in the transformation matrix
     * @param m12 the entry for the [1, 2] element in the transformation matrix
     * @param m13 the entry for the [1, 3] element in the transformation matrix
     * @param m20 the entry for the [2, 0] element in the transformation matrix
     * @param m21 the entry for the [2, 1] element in the transformation matrix
     * @param m22 the entry for the [2, 2] element in the transformation matrix
     * @param m23 the entry for the [2, 3] element in the transformation matrix
     * 
     */
    public ZAffineTransformation(double m00, double m01, double m02,double m03, double m10, double m11, double m12,double m13,
            double m20, double m21, double m22, double m23) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03=m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13=m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23=m23;
    }
    
    /**
     * Populate the matrix element of the linear transformation used to
     * rotate, scale, translate or shear in 3D a geometry.
     * 
     * @param dx
     * @param dy
     * @param dz 
     */
    public ZAffineTransformation(double dx, double dy, double dz) {
        this.m00 = 1.0;  this.m01 = 0.0; this.m02 = 0.0; this.m03 = dx;
        this.m10 = 0.0;  this.m11 = 1.0; this.m12 = 0.0;this.m13=dy;
        this.m20=0.0; this.m21=0.0; this.m22 =1; this.m23=dz;
    }

    @Override
    public void filter(CoordinateSequence seq, int i) {
        double x = seq.getOrdinate(i, 0);
        double y = seq.getOrdinate(i, 1);
        double z = seq.getOrdinate(i, 2);
        double xp = m00 * x + m01 * y + m02 * z + m03;
        double yp = m10 * x + m11 * y + m12 * z + m13;
        double zp = m20 * x + m21 * y + m22 * z + m23;
        seq.setOrdinate(i, 2, zp);
        seq.setOrdinate(i, 0, xp);
        seq.setOrdinate(i, 1, yp);
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean isGeometryChanged() {
        return true;
    }
}
