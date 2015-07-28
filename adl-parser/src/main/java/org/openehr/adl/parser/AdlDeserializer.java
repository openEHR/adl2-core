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

package org.openehr.adl.parser;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import org.antlr.v4.runtime.*;
import org.openehr.adl.antlr4.generated.adlLexer;
import org.openehr.adl.antlr4.generated.adlParser;
import org.openehr.adl.parser.tree.AdlTreeParser;
import org.openehr.jaxb.am.DifferentialArchetype;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

/**
 * Parses a adl source file (.adls) and builds a differential archetype
 *
 * @author markopi
 */
public class AdlDeserializer {
    /**
     * Parses an adl source into a differential archetype.
     *
     * @param adl contents of an adl source file
     * @return parsed archetype
     * @throws org.openehr.adl.parser.AdlParserException if an error occurred while parsing
     */
    public DifferentialArchetype parse(String adl) {
        try {
            return parse(new StringReader(adl));
        } catch (IOException e) {
            // StringReader should never throw an IOException
            throw new AssertionError(e);
        }
    }

    public DifferentialArchetype parse(Reader reader) throws IOException {
        try {
            CharStream charStream = new ANTLRInputStream(reader);
            Lexer lexer = new adlLexer(charStream);
            adlParser parser = new adlParser(new BufferedTokenStream(lexer));
            AccumulatingErrorListener errorHandler = new AccumulatingErrorListener();
            parser.removeErrorListeners();
            parser.addErrorListener(errorHandler);
            adlParser.AdlContext context = parser.adl();
            if (!errorHandler.getErrors().isEmpty()) {
                throw new AdlParserException(Joiner.on("\n").join(errorHandler.getErrors()));
            }
            AdlTreeParser treeParser = new AdlTreeParser();
            return treeParser.parseAdl(context);

        } finally {
            reader.close();
        }
    }

    public DifferentialArchetype parse(InputStream inputStream) throws IOException {
        try (BomSupportingReader reader = new BomSupportingReader(inputStream, Charsets.UTF_8)) {
            return parse(reader);
        }
    }

}
