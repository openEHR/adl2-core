/*
 * ADL2-core
 * Copyright (c) 2013-2014 Marand d.o.o. (www.marand.com)
 *
 * This file is part of ADL2-core.
 *
 * ADL2-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openehr.adl.serializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by bna on 01.02.2015.
 */
public abstract class CSerializer {


    private boolean debug = false;

    public CSerializer() {
        debug = false;
    }

    public CSerializer(boolean debug) {
        this.debug = debug;
    }

    /**
     * Write the ADL to target/{file) to inspect manually and try open with Archetype Editor
     *
     * @param adl      the string representation of Archetype
     * @param filename the filename
     */
    protected void write(String adl, String filename) {
        if (debug) {
            try {
                System.out.println("adlResult");
                System.out.println(adl);
                createTargetIfNotExist();
                FileWriter writer = new FileWriter(new File("target/" + filename));

                writer.write(adl);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createTargetIfNotExist() {

        try {
            Files.createDirectories(Paths.get("target"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
