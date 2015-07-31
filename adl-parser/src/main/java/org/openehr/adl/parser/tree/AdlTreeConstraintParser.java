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

package org.openehr.adl.parser.tree;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.NotImplementedException;
import org.openehr.adl.am.OperatorKind;
import org.openehr.adl.antlr4.generated.adlParser;
import org.openehr.adl.rm.RmTypes;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.MultiplicityInterval;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.openehr.adl.am.AmObjectFactory.newExprBinaryOperator;
import static org.openehr.adl.am.AmObjectFactory.newExprLeaf;
import static org.openehr.adl.rm.RmObjectFactory.newMultiplicityInterval;

/**
 * @author markopi
 */
public class AdlTreeConstraintParser extends AbstractAdlTreeParser {
    private final AdlTreePrimitiveConstraintsParser primitives = new AdlTreePrimitiveConstraintsParser();

    private CObject parseTypeDefinition(adlParser.TypeConstraintContext context) {

        final CObject result;
        if (context.complexObjectConstraint() != null) {
            result = parseComplexObject(context.complexObjectConstraint());
        } else if (context.USE_NODE() != null) {
            result = parseArchetypeInternalRef(context);
        } else if (context.archetypeSlotConstraint() != null) {
            result = parseArchetypeSlot(context.archetypeSlotConstraint());
        } else {
            throw new NotImplementedException();
        }

        result.setSiblingOrder(parseSiblingOrder(context.orderConstraint()));
        return result;

    }

    private ArchetypeSlot parseArchetypeSlot(adlParser.ArchetypeSlotConstraintContext context) {
        ArchetypeSlot result = new ArchetypeSlot();
        result.setRmTypeName(collectText(context.typeIdentifierWithGenerics()));
        result.setNodeId(parseAtCode(context.atCode()));
        result.setOccurrences(parseOccurrences(context.occurrences()));
        if (context.archetypeSlotValueConstraint()!=null) {
            adlParser.ArchetypeSlotValueConstraintContext cValue = context.archetypeSlotValueConstraint();
            result.getIncludes().addAll(parseAssertions(cValue.include));
            result.getExcludes().addAll(parseAssertions(cValue.exclude));
        }
        return result;
    }

    private List<Assertion> parseAssertions(List<adlParser.ArchetypeSlotSingleConstraintContext> cAssertions) {
        List<Assertion> result = new ArrayList<>();
        for (adlParser.ArchetypeSlotSingleConstraintContext cAssertion : cAssertions) {
            result.add(parseAssertion(cAssertion));
        }
        return result;
    }

    private Assertion parseAssertion(adlParser.ArchetypeSlotSingleConstraintContext cAssertion) {
        Assertion result = new Assertion();
        result.setStringExpression(collectText(cAssertion));
        CPrimitiveObject cPrimitiveObject = primitives.parsePrimitiveValue(cAssertion.primitiveValueConstraint());
        ExprBinaryOperator expr = newExprBinaryOperator(
                RmTypes.ReferenceType.CONSTRAINT.toString(),
                OperatorKind.OP_MATCHES,
                false,
                newExprLeaf(RmTypes.STRING, RmTypes.ReferenceType.ATTRIBUTE, collectText(cAssertion.rmPath())),
                newExprLeaf(cPrimitiveObject.getRmTypeName(), RmTypes.ReferenceType.CONSTRAINT,
                        cPrimitiveObject));
        result.setExpression(expr);

        return result;
    }

    private ArchetypeInternalRef parseArchetypeInternalRef(adlParser.TypeConstraintContext context) {
        ArchetypeInternalRef result = new ArchetypeInternalRef();
        result.setRmTypeName(collectText(context.typeIdentifierWithGenerics()));
        result.setNodeId(parseAtCode(context.atCode()));
        result.setOccurrences(parseOccurrences(context.occurrences()));
        result.setTargetPath(collectText(context.rmPath()));
        return result;
    }

    public CComplexObject parseComplexObject(adlParser.ComplexObjectConstraintContext context) {
        CComplexObject result = new CComplexObject();
        result.setRmTypeName(collectText(context.typeIdentifierWithGenerics()));
        result.setNodeId(parseAtCode(context.atCode()));
        result.setOccurrences(parseOccurrences(context.occurrences()));
        result.getAttributes().addAll(parseAttributeList(context.attributeListMatcher()));
        return result;
    }

    private List<CAttribute> parseAttributeList(adlParser.AttributeListMatcherContext context) {
        if (context == null || context.attributeConstraint() == null) return ImmutableList.of();
        List<CAttribute> result = new ArrayList<>();
        for (adlParser.AttributeConstraintContext cAttribute : context.attributeConstraint()) {
            result.add(parseAttribute(cAttribute));
        }
        return result;
    }

    private CAttribute parseAttribute(adlParser.AttributeConstraintContext context) {
        CAttribute result = new CAttribute();

        if (context.existence() != null) {
            result.setExistence(parseOccurrences(context.existence().occurrenceRange()));
        }
        result.setCardinality(parseCardinality(context.cardinality()));

        result.setMatchNegated(context.negatedMatches != null);

        if (context.attributeIdentifier() != null) {
            String rmPath = collectNonNullText(context.attributeIdentifier());
            if (rmPath.startsWith("/")) {
                result.setDifferentialPath(rmPath);
            } else {
                result.setRmAttributeName(rmPath);
            }

            result.getChildren().addAll(parseMultiValue(context.multiValueConstraint()));
        } else {
            throw new NotImplementedException("parse tuples");
            // todo parse tuples
        }
        return result;
    }

    private List<CObject> parseMultiValue(adlParser.MultiValueConstraintContext context) {
        if (context == null) return ImmutableList.of();

        List<CObject> result = new ArrayList<>();
        for (adlParser.ValueConstraintContext valueConstraintContext : context.valueConstraint()) {
            result.add(parseValueConstraint(valueConstraintContext));
        }
        return result;
    }

    private CObject parseValueConstraint(adlParser.ValueConstraintContext context) {
        if (context.typeConstraint() != null) {
            return parseTypeDefinition(context.typeConstraint());
        } else if (context.primitiveValueConstraint() != null) {
            return primitives.parsePrimitiveValue(context.primitiveValueConstraint());
        }

        throw new NotImplementedException();
    }

    private Cardinality parseCardinality(adlParser.CardinalityContext context) {
        if (context == null) return null;
        Cardinality result = new Cardinality();

        result.setInterval(parseOccurrences(context.occurrenceRange()));
        if (context.ORDERED() != null) {
            result.setIsOrdered(true);
        } else if (context.UNORDERED() != null) {
            result.setIsOrdered(false);
        }
        result.setIsUnique(context.UNIQUE() != null);
        return result;
    }


    private MultiplicityInterval parseOccurrences(adlParser.OccurrencesContext context) {
        if (context == null) return null;
        return parseOccurrences(context.occurrenceRange());

    }

    private MultiplicityInterval parseOccurrences(adlParser.OccurrenceRangeContext context) {
        if (context == null) return null;
        if (context.val != null) {
            int val = Integer.parseInt(context.val.getText());
            return newMultiplicityInterval(val, val);
        }
        Integer lower = context.lower != null ? Integer.parseInt(context.lower.getText()) : 0;
        Integer upper = context.upper != null ? Integer.parseInt(context.upper.getText()) : null;
        return newMultiplicityInterval(lower, upper);
    }

    @Nullable
    private SiblingOrder parseSiblingOrder(@Nullable adlParser.OrderConstraintContext context) {
        if (context == null) return null;
        SiblingOrder result = new SiblingOrder();
        result.setIsBefore(context.BEFORE() != null);
        result.setSiblingNodeId(parseAtCode(context.atCode()));
        return result;
    }

}
