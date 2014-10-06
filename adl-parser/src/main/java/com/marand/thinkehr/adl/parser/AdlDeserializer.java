/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.parser;

import com.marand.thinkehr.adl.antlr.AdlLexer;
import com.marand.thinkehr.adl.antlr.AdlParser;
import com.marand.thinkehr.adl.parser.tree.AdlTreeParser;
import com.marand.thinkehr.adl.parser.tree.AdlTreeParserException;
import com.marand.thinkehr.adl.rm.RmModel;
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
    private final RmModel rmModel;

    /**
     * Constructs an adl deserializer with a provided rm model.
     * @param rmModel rm model to use
     */
    public AdlDeserializer(RmModel rmModel) {
        this.rmModel = rmModel;
    }

    /**
     * Parses an adl source into a differential archetype.
     *
     * @param adl contents of an adl source file
     * @return parsed archetype
     * @throws  com.marand.thinkehr.adl.parser.AdlParserException if an error occurred while parsing
     */
    public DifferentialArchetype parse(String adl) {
        try {
            try {
                AdlLexer lexer = new ErrorThrowingAdlLexer(new ANTLRStringStream(adl));
                CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                AdlParser parser = new ErrorThrowingAdlParser(tokenStream);
                AdlParser.adl_return adlReturn = parser.adl();

                DifferentialArchetype archetype =  AdlTreeParser.build(tokenStream, adlReturn.getTree(), rmModel);
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
