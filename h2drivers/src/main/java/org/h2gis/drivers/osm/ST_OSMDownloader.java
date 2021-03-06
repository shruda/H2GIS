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

package org.h2gis.drivers.osm;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.h2gis.h2spatialapi.DeterministicScalarFunction;

/**
 * This function is used to download data from the osm api using a bounding box.
 *
 * @author Erwan Bocher
 */
public class ST_OSMDownloader extends DeterministicScalarFunction {

    private static final String OSM_API_URL = "http://api.openstreetmap.org/api/0.6/";

    public ST_OSMDownloader() {
        addProperty(PROP_REMARKS, "Extract an OSM XML file from the OSM api server using a the bounding box of a given geometry.\n"
                + "A path must be set to specified where the OSM file will be stored.");
    }

    @Override
    public String getJavaStaticMethod() {
        return "downloadData";
    }

    /**
     * 
     * @param area The geometry used to compute the area set to the OSM server
     * @param fileName The path to save the osm file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void downloadData(Geometry area, String fileName) throws FileNotFoundException, IOException {
        File file = new File(fileName);
        if (file.exists()) {
            throw new FileNotFoundException("The following file already exists:\n" + fileName);
        }
        if (area != null) {
            downloadOSMFile(file, area.getEnvelopeInternal());
        }
    }   
    
     

    /**
     * Download OSM file from the official server
     *
     * @param file
     * @param geometryEnvelope
     * @throws IOException
     */
    public static void downloadOSMFile(File file, Envelope geometryEnvelope) throws IOException {
        InputStream in = createOsmUrl(geometryEnvelope).openStream();
        OutputStream out = new FileOutputStream(file);
        try {
            byte[] data = new byte[4096];
            while (true) {
                int numBytes = in.read(data);
                if (numBytes == -1) {
                    break;
                }
                out.write(data, 0, numBytes);
            }
        } finally {
            out.close();
            in.close();
        }
    }

    /**
     * Build the OSM URL based on a given envelope
     *
     * @param geometryEnvelope
     * @return
     */
    private static URL createOsmUrl(Envelope geometryEnvelope) {
        try {
            return new URL(OSM_API_URL + "map?bbox=" + geometryEnvelope.getMinX() + ","
                    + geometryEnvelope.getMinY()
                    + "," + geometryEnvelope.getMaxX() + "," + geometryEnvelope.getMaxY());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }
}
