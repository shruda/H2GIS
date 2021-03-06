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
package org.h2gis.h2spatialext.function.spatial.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import com.vividsolutions.jts.operation.valid.TopologyValidationError;
import org.h2gis.h2spatialapi.DeterministicScalarFunction;

/**
 * Returns a valid_detail (valid,reason,location) as an array of objects.
 * If a geometry is valid or not and if not valid, a reason why and a location where.
 * 
 * @author Erwan Bocher
 */
public class ST_IsValidDetail extends DeterministicScalarFunction{

    private static final GeometryFactory GF = new GeometryFactory();    
    
    public ST_IsValidDetail() {
        addProperty(PROP_REMARKS, " Returns a valid_detail as an array of objects\n"
                + " [0] = isvalid,[1] = reason, [2] = error location"
                + "The second argument is optional. It can have the following values (0 or 1)\n"
                + "1 = It will validate inverted shells and exverted holes according the ESRI SDE model.\n"
                + "0 = It will based on the OGC geometry model.");
    }
    
    @Override
    public String getJavaStaticMethod() {
        return "isValidDetail";
    }
    
    /**
     * Returns a valid_detail as an array of objects
     * [0] = isvalid,[1] = reason, [2] = error location
     * 
     * @param geometry
     * @return 
     */
    public static Object[] isValidDetail(Geometry geometry) {
        return isValidDetail(geometry, 0);
    }
    
    /**
     * Returns a valid_detail as an array of objects
     * [0] = isvalid,[1] = reason, [2] = error location
     * 
     * isValid equals true if the geometry is valid.
     * reason correponds to an error message describing this error.
     * error returns the location of this error (on the {@link Geometry} 
     * containing the error. 
     * 
     * @param geometry
     * @param flag
     * @return 
     */
    public static Object[] isValidDetail(Geometry geometry, int flag) {        
        if (geometry != null) {            
            if (flag == 0) {
                return detail(geometry, false);
            } else if (flag == 1) {
                return detail(geometry, true);
            } else {
                throw new IllegalArgumentException("Supported arguments is 0 or 1.");
            }
        }
        return null;
    }
    
    /**
     *
     * @param geometry
     * @return
     */
    private static Object[] detail(Geometry geometry, boolean flag) {    
        Object[] details = new Object[3];
        IsValidOp validOP = new IsValidOp(geometry);
        validOP.setSelfTouchingRingFormingHoleValid(flag);
        TopologyValidationError error = validOP.getValidationError();
        if (error != null) {
            details[0] = false ;
            details[1] = error.getMessage();
            details[2] = GF.createPoint(error.getCoordinate());
        } else {
            details[0] = true ;
            details[1] = "Valid Geometry";
        }
        return details;
    }

}
