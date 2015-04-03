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

import org.antlr.runtime.tree.CommonTree;
import org.openehr.adl.antlr.AdlLexer;
import org.openehr.adl.antlr.AdlParser;
import org.openehr.adl.parser.tree.AdlTreeParser;
import org.openehr.adl.parser.tree.AdlTreeParserException;
import org.openehr.adl.rm.RmModel;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.openehr.jaxb.am.DifferentialArchetype;

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
     * @throws  org.openehr.adl.parser.AdlParserException if an error occurred while parsing
     */
    public DifferentialArchetype parse(String adl) {
        try {
            try {
                AdlLexer lexer = new ErrorThrowingAdlLexer(new ANTLRStringStream(adl));
                CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                AdlParser parser = new ErrorThrowingAdlParser(tokenStream);
                AdlParser.adl_return adlReturn = parser.adl();

                DifferentialArchetype archetype =  AdlTreeParser.build(tokenStream, (CommonTree) adlReturn.getTree());
                AdlParserPostprocessor.postprocess(archetype);
                return archetype;
            } catch (RecognitionException e) {
                throw new RuntimeRecognitionException(e);
            }
        } catch (RuntimeRecognitionException e) {
            throw new AdlParserException(adl, e);
        } catch (AdlTreeParserException e) {
            throw new AdlParserException(adl, e);
        }
    }


}
