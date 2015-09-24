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

package org.openehr.adl.util;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.openehr.adl.parser.AdlDeserializer;
import org.openehr.adl.parser.BomSupportingReader;
import org.openehr.jaxb.am.DifferentialArchetype;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @author markopi
 */
public class TestAdlParser {

    private static String readClasspathResource(String classpathResource) {
        try {
            InputStream in = TestAdlParser.class.getClassLoader().getResourceAsStream(classpathResource);
            try (Reader reader = new BomSupportingReader(in, Charsets.UTF_8)) {
                return CharStreams.toString(reader);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DifferentialArchetype parseAdlFromString(String adl) {
        return new AdlDeserializer().parse(adl);
    }


    public static DifferentialArchetype parseAdl(String classpathResource) {
        String adl = readClasspathResource(classpathResource);
        return new AdlDeserializer().parse(adl);
    }

}
