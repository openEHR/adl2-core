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

package org.openehr.adl.antlr4.generated;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.openehr.adl.parser.BomSupportingReader;
import org.openehr.adl.util.TestUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @author markopi
 */
public class AdlParserTest {
    private static adlParser createAdlParser(String text) throws IOException {
        char[] chars = text.toCharArray();
        CharStream charStream = new ANTLRInputStream(chars, chars.length);
        Lexer lexer = new adlLexer(charStream);
        return new adlParser(new BufferedTokenStream(lexer));
    }

    protected static adlParser createAdlParser(InputStream inputStream) throws IOException {
        Reader reader = new BomSupportingReader(inputStream, Charsets.UTF_8);

        return createAdlParser(CharStreams.toString(reader));
    }


    @Test
    public void parseAdl() throws IOException {
        adlParser parser = createAdlParser(TestUtils.getResource("adl15/openEHR-EHR-EVALUATION.alert.v1.adls"));

        parser.adl();

    }


}
