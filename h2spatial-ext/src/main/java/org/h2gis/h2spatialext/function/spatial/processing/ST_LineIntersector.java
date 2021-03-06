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
 * or contact directly: info_at_orbisgis.org
 */

package org.h2gis.h2spatialext.function.spatial.processing;

import com.vividsolutions.jts.algorithm.RobustLineIntersector;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.noding.IntersectionAdder;
import com.vividsolutions.jts.noding.MCIndexNoder;
import com.vividsolutions.jts.noding.NodedSegmentString;
import com.vividsolutions.jts.noding.SegmentString;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.h2gis.h2spatialapi.DeterministicScalarFunction;

/**
 * LineIntersector is used to split an input geometry (LineString or MultiLineString) by
 * a set of geometries. 
 * @author Erwan Bocher
 */
public class ST_LineIntersector extends  DeterministicScalarFunction{   
    
    
    
    private static final RobustLineIntersector ROBUST_INTERSECTOR = new RobustLineIntersector();
    
    public ST_LineIntersector() {
        addProperty(PROP_REMARKS, "Split an input geometry by another geometry. \n"
                + "This function uses a more robust intersection algorithm than the ST_Split function.\n"
                + "It computes the intersections between the line segments of the input geometries."
                + "A collection of LineString is returned.");
    }
    
     @Override
    public String getJavaStaticMethod() {
        return "lineIntersector";
    }
    
    /**
     * Split a lineal geometry by a another geometry
     * @param inputLines
     * @param clipper
     * @return 
     */
    public static Geometry lineIntersector(Geometry inputLines, Geometry clipper) throws IllegalArgumentException {
        if(inputLines.getDimension()==1){
        MCIndexNoder mCIndexNoder = new MCIndexNoder();
        mCIndexNoder.setSegmentIntersector(new IntersectionAdder(ROBUST_INTERSECTOR));        
        mCIndexNoder.computeNodes(getSegments(inputLines, clipper));
        Collection nodedSubstring = mCIndexNoder.getNodedSubstrings();
        GeometryFactory gf = inputLines.getFactory();
        ArrayList<LineString> linestrings = new ArrayList<LineString>(nodedSubstring.size());
        for (Iterator it = nodedSubstring.iterator(); it.hasNext();) {
            SegmentString segment = (SegmentString) it.next();
            //We keep only the segments of the input geometry
            if((Integer)segment.getData()==0){
                Coordinate[] cc = segment.getCoordinates();
                cc = CoordinateArrays.atLeastNCoordinatesOrNothing(2, cc);
                if (cc.length > 1) {
                    linestrings.add(gf.createLineString(cc));
                }
            }
        }
        if (linestrings.isEmpty()) {
            return inputLines;
        } else {
            return gf.createMultiLineString(linestrings.toArray(new LineString[linestrings.size()]));
        }}
        throw new IllegalArgumentException("Split a " + inputLines.getGeometryType() + " by a " + clipper.getGeometryType() + " is not supported.");
    }
    
    /***
     * Convert the input geometries as a list of segments and mark them with a flag
     * to identify input and output geometries.
     * @param inputLines
     * @param clipper
     * @return 
     */
    public static ArrayList<SegmentString> getSegments(Geometry inputLines, Geometry clipper) {
        ArrayList<SegmentString> segments = new ArrayList<SegmentString>();        
        addGeometryToSegments(inputLines, 0, segments);        
        addGeometryToSegments(clipper, 1, segments);
        return segments;
    }

    /**
     * Convert the a geometry as a list of segments and mark it with a flag
     * @param geometry
     * @param flag
     * @param segments 
     */
    public static void addGeometryToSegments(Geometry geometry, int flag, ArrayList<SegmentString> segments) {
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry component = geometry.getGeometryN(i);
            if (component instanceof Polygon) {
                add((Polygon) component, flag, segments);
            } else if (component instanceof LineString) {
                add((LineString) component, flag, segments);
            }
        }
    }

     /**
     * Convert a polygon as a list of segments and mark it with a flag
     * @param poly
     * @param flag
     * @param segments 
     */
    private static void add(Polygon poly, int flag, ArrayList<SegmentString> segments) {
        add(poly.getExteriorRing(), flag, segments);
        for (int j = 0; j < poly.getNumInteriorRing(); j++) {
            add(poly.getInteriorRingN(j), flag, segments);
        }
    }

    /**
     * Convert a linestring as a list of segments and mark it with a flag
     * @param line
     * @param flag
     * @param segments 
     */
    private static void add(LineString line, int flag, ArrayList<SegmentString> segments) {
        SegmentString ss = new NodedSegmentString(line.getCoordinates(),
                flag);
        segments.add(ss);
    }
}
