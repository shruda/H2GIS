/**
 * h2spatial is a library that brings spatial support to the H2 Java database.
 *
 * h2spatial is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * or contact directly: info_at_ orbisgis.org
 */

package org.h2gis.h2spatialext.function.spatial.edit;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import java.util.ArrayList;
import org.h2gis.h2spatialapi.DeterministicScalarFunction;

/**
 *
 * @author Erwan Bocher
 */
public class ST_CollectExtract extends DeterministicScalarFunction{
    
    
    
    public ST_CollectExtract() {
        addProperty(PROP_REMARKS, "Given a (multi)geometry, returns a (multi)geometry consisting only of elements of the specified dimension.\n"
                + "Dimension numbers are 1 == POINT, 2 == LINESTRING, 3 == POLYGON");
    }   

    @Override
    public String getJavaStaticMethod() {
        return "collectExtract";
    }
    
    /**
     * Given a (multi)geometry, returns a (multi)geometry consisting only of
     * elements of the specified type. Sub-geometries that are not the specified
     * type are ignored. If there are no sub-geometries of the right type, an
     * EMPTY geometry will be returned. Only points, lines and polygons are
     * supported.
     * 
     * @param geometry
     * @param dimension
     * @return 
     */
    public static Geometry collectExtract(Geometry geometry, int dimension) throws ParseException {
        if ((dimension < 1) || (dimension > 3)) {
            throw new IllegalArgumentException(
                    "Dimension out of range (1..3)");
        }
        if (geometry != null) {
            if (dimension == 1) {
                ArrayList<Point> points = new ArrayList<Point>();
                getPunctualGeometry(points, geometry);
                if (points.isEmpty()) {
                    return geometry.getFactory().buildGeometry(points);
                } else if (points.size() == 1) {
                    return points.get(0);
                } else {
                    return geometry.getFactory().createMultiPoint(points.toArray(new Point[points.size()]));
                }
            } else if (dimension == 2) {
                ArrayList<LineString> lines = new ArrayList<LineString>();
                getLinealGeometry(lines, geometry);
                if (lines.isEmpty()) {
                     return geometry.getFactory().buildGeometry(lines);
                } else if (lines.size() == 1) {
                    return lines.get(0);
                } else {
                    return geometry.getFactory().createMultiLineString(lines.toArray(new LineString[lines.size()]));
                }
            } else if (dimension == 3) {
                ArrayList<Polygon> polygones = new ArrayList<Polygon>();
                getArealGeometry(polygones, geometry);
                if (polygones.isEmpty()) {
                     return geometry.getFactory().buildGeometry(polygones);
                } else if (polygones.size() == 1) {
                    return polygones.get(0);
                } else {
                    return geometry.getFactory().createMultiPolygon(polygones.toArray(new Polygon[polygones.size()]));
                }
            }

        }
        return null;
    }
    
    /**
     * Filter point from a geometry
     * @param points
     * @param geometry 
     */
    private static void getPunctualGeometry(ArrayList<Point> points, Geometry geometry) {
         for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry subGeom = geometry.getGeometryN(i);
            if(subGeom instanceof Point){
                points.add((Point) subGeom);
            }
            else if (subGeom instanceof GeometryCollection){
                getPunctualGeometry(points, subGeom);
            }
        }
    }
    
     /**
     * Filter line from a geometry
     * @param lines
     * @param geometry 
     */
    private static void getLinealGeometry(ArrayList<LineString> lines, Geometry geometry) {
         for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry subGeom = geometry.getGeometryN(i);
            if(subGeom instanceof LineString){
                lines.add((LineString) subGeom);
            }
            else if (subGeom instanceof GeometryCollection){
                getLinealGeometry(lines, subGeom);
            }
        }
    }
    
     /**
     * Filter polygon from a geometry
     * @param polygones
     * @param geometry 
     */
    private static void getArealGeometry(ArrayList<Polygon> polygones, Geometry geometry) {
         for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry subGeom = geometry.getGeometryN(i);
            if(subGeom instanceof Polygon){
                polygones.add((Polygon) subGeom);
            }
            else if (subGeom instanceof GeometryCollection){
                getArealGeometry(polygones, subGeom);
            }
        }
    }
}
