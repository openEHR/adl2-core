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
import org.openehr.adl.util.AdlUtils;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.openehr.adl.am.AmObjectFactory.*;
import static org.openehr.adl.parser.tree.AdlTreeParserUtils.*;
import static org.openehr.adl.parser.tree.AdlTreePrimitiveConstraintsParser.parseNumberInterval;
import static org.openehr.adl.parser.tree.AdlTreePrimitiveConstraintsParser.parsePrimitiveValue;
import static org.openehr.adl.rm.RmObjectFactory.*;

/**
 * @author markopi
 */
abstract class AdlTreeConstraintParser {

    private static CObject parseTypeDefinition(adlParser.TypeConstraintContext context) {

        final CObject result;
        if (context.complexObjectConstraint() != null) {
            result = parseComplexObject(context.complexObjectConstraint());
        } else if (context.USE_NODE() != null) {
            result = parseArchetypeInternalRef(context);
        } else if (context.archetypeSlotConstraint() != null) {
            result = parseArchetypeSlot(context.archetypeSlotConstraint());
        } else if (context.odinValue() != null) {
            result = parseAdlValueConstraint(context.odinValue());
        } else if (context.archetypeReferenceConstraint() != null) {
            result = parseArchetypeReference(context.archetypeReferenceConstraint());
        } else {
            throw new NotImplementedException();
        }

        result.setSiblingOrder(parseSiblingOrder(context.orderConstraint()));
        return result;

    }

    private static CArchetypeRoot parseArchetypeReference(adlParser.ArchetypeReferenceConstraintContext context) {
        CArchetypeRoot result = new CArchetypeRoot();
        result.setRmTypeName(collectNonNullText(context.typeIdentifier()));
        result.setArchetypeRef(collectNonNullText(context.archetypeId()));
        result.setNodeId(result.getArchetypeRef());
        result.setSlotNodeId(collectText(context.AT_CODE_VALUE()));
        result.setOccurrences(parseOccurrences(context.occurrences()));
        return result;
    }

    private static CObject parseAdlValueConstraint(adlParser.OdinValueContext context) {
        String amType = collectText(context.typeIdentifier());
        if ("C_DV_QUANTITY".equals(amType) || amType==null) {
            return parseCDvQuantityConstraint(context);
        } else if ("C_DV_ORDINAL".equals(amType)) {
            return parseCDvOrdinalConstraint(context);
        } else {
            throw new AdlTreeParserException(context.start, "Bad adl value am type: %s", amType);
        }
    }

    private static CObject parseCDvOrdinalConstraint(adlParser.OdinValueContext adlValueContext) {
        CDvOrdinal result = new CDvOrdinal();
        result.setRmTypeName("DV_ORDINAL");
        return result;
    }

    private static CDvQuantity parseCDvQuantityConstraint(adlParser.OdinValueContext context) {
        CDvQuantity result = new CDvQuantity();
        result.setRmTypeName("DV_QUANTITY");
        if (context == null) return result;

        OdinObject dQuantity = OdinObject.parse(context.odinObjectValue());
        adlParser.OdinValueContext dProperty = dQuantity.tryGet("property");
        if (dProperty != null) {
            result.setProperty(parseCodePhraseListSingleItem(dProperty.odinCodePhraseValueList()));
        }
        adlParser.OdinValueContext dItem = dQuantity.tryGet("list");
        if (dItem != null && dItem.odinMapValue() != null) {
            for (adlParser.OdinMapValueEntryContext entryContext : dItem.odinMapValue().odinMapValueEntry()) {
                result.getList().add(parseCQuantityItem(entryContext.odinValue().odinObjectValue()));
            }
        }
        adlParser.OdinValueContext dAssumedValue = dQuantity.tryGet("assumed_value");
        if (dAssumedValue != null) {
            result.setAssumedValue(parseDvQuantity(dAssumedValue.odinObjectValue()));
        }

        return result;
    }

    private static DvQuantity parseDvQuantity(adlParser.OdinObjectValueContext context) {
        DvQuantity result = new DvQuantity();

        OdinObject dContext = OdinObject.parse(context);
        result.setUnits(dContext.tryGetString("units"));
        Double tMagnitude = dContext.tryGetDouble("magnitude");
        if (tMagnitude != null) result.setMagnitude(tMagnitude);
        result.setPrecision(dContext.tryGetInteger("precision"));
        return result;
    }

    private static CQuantityItem parseCQuantityItem(adlParser.OdinObjectValueContext context) {
        CQuantityItem result = new CQuantityItem();

        OdinObject dContext = OdinObject.parse(context);
        result.setUnits(collectString(dContext.get("units").openStringList()));
        adlParser.OdinValueContext pMagnitude = dContext.tryGet("magnitude");
        if (pMagnitude != null) {
            result.setMagnitude((IntervalOfReal) parseNumberInterval(pMagnitude.numberIntervalConstraint()));
        }
        adlParser.OdinValueContext pPrecision = dContext.tryGet("precision");
        if (pPrecision != null) {
            result.setPrecision((IntervalOfInteger) parseNumberInterval(pPrecision.numberIntervalConstraint()));
        }
        return result;
    }

    private static ArchetypeSlot parseArchetypeSlot(adlParser.ArchetypeSlotConstraintContext context) {
        ArchetypeSlot result = new ArchetypeSlot();
        result.setRmTypeName(collectText(context.typeIdentifierWithGenerics()));
        result.setNodeId(parseAtCode(context.atCode()));
        result.setOccurrences(parseOccurrences(context.occurrences()));
        if (context.archetypeSlotValueConstraint() != null) {
            adlParser.ArchetypeSlotValueConstraintContext cValue = context.archetypeSlotValueConstraint();
            result.getIncludes().addAll(parseAssertions(cValue.include));
            result.getExcludes().addAll(parseAssertions(cValue.exclude));
        }
        return result;
    }

    private static List<Assertion> parseAssertions(List<adlParser.ArchetypeSlotSingleConstraintContext> cAssertions) {
        List<Assertion> result = new ArrayList<>();
        for (adlParser.ArchetypeSlotSingleConstraintContext cAssertion : cAssertions) {
            result.add(parseAssertion(cAssertion));
        }
        return result;
    }

    private static Assertion parseAssertion(adlParser.ArchetypeSlotSingleConstraintContext cAssertion) {
        Assertion result = new Assertion();
        result.setStringExpression(collectTextWithSpaces(cAssertion));
        CPrimitiveObject cPrimitiveObject = parsePrimitiveValue(cAssertion.primitiveValueConstraint());
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

    private static ArchetypeInternalRef parseArchetypeInternalRef(adlParser.TypeConstraintContext context) {
        ArchetypeInternalRef result = new ArchetypeInternalRef();
        result.setRmTypeName(collectText(context.typeIdentifierWithGenerics()));
        result.setNodeId(parseAtCode(context.atCode()));
        result.setOccurrences(parseOccurrences(context.occurrences()));
        result.setTargetPath(collectText(context.rmPath()));
        return result;
    }

    public static CComplexObject parseComplexObject(adlParser.ComplexObjectConstraintContext context) {
        CComplexObject result = new CComplexObject();
        result.setRmTypeName(collectText(context.typeIdentifierWithGenerics()));
        result.setNodeId(parseAtCode(context.atCode()));
        result.setOccurrences(parseOccurrences(context.occurrences()));
        parseAttributeList(result, context.attributeListMatcher());
        return result;
    }

    private static void parseAttributeList(CComplexObject target, adlParser.AttributeListMatcherContext context) {
        if (context == null || context.attributeConstraint() == null) return;
        for (adlParser.AttributeConstraintContext cAttribute : context.attributeConstraint()) {
            if (cAttribute.attributeIdentifier() != null) {
                target.getAttributes().add(parseAttribute(cAttribute));
            } else if (cAttribute.tupleAttributeIdentifier() != null) {
                target.getAttributeTuples().add(parseAttributeTuple(cAttribute));
            } else {
                throw new AssertionError();
            }
        }
    }

    private static CAttributeTuple parseAttributeTuple(adlParser.AttributeConstraintContext context) {
        CAttributeTuple result = new CAttributeTuple();
        for (adlParser.AttributeIdentifierContext cAttribute : context.tupleAttributeIdentifier().attributeIdentifier()) {
            result.getMembers().add(newCAttribute(collectText(cAttribute.rmPath())));
        }
        for (adlParser.TupleChildConstraintContext cTuple : context.tupleChildConstraints().tupleChildConstraint()) {
            CObjectTuple tuple = new CObjectTuple();
            for (adlParser.PrimitiveValueConstraintContext cConstraint : cTuple.primitiveValueConstraint()) {
                tuple.getMembers().add(parsePrimitiveValue(cConstraint));
            }
            result.getChildren().add(tuple);
        }
        return result;
    }

    private static CAttribute parseAttribute(adlParser.AttributeConstraintContext context) {
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
                result.setRmAttributeName(rmPath.substring(rmPath.lastIndexOf("/") + 1));
            } else {
                result.setRmAttributeName(rmPath);
            }

            result.getChildren().addAll(parseMultiValue(context.multiValueConstraint()));
        } else {
            throw new AssertionError();
        }
        return result;
    }

    private static List<CObject> parseMultiValue(adlParser.MultiValueConstraintContext context) {
        if (context == null) return ImmutableList.of();

        List<CObject> result = new ArrayList<>();
        for (adlParser.ValueConstraintContext valueConstraintContext : context.valueConstraint()) {
            result.add(parseValueConstraint(valueConstraintContext));
        }
        return result;
    }

    private static CObject parseValueConstraint(adlParser.ValueConstraintContext context) {
        if (context.typeConstraint() != null) {
            return parseTypeDefinition(context.typeConstraint());
        }
        if (context.primitiveValueConstraint() != null) {
            return parsePrimitiveValue(context.primitiveValueConstraint());
        }
        if (context.codePhraseConstraint() != null) {
            return parseCodePhrase(context.codePhraseConstraint());
        }
        if (context.ordinalConstraint() != null) {
            return parseCDvOrdinalConstraint(context.ordinalConstraint());
        }

        throw new NotImplementedException();
    }

    private static CDvOrdinal parseCDvOrdinalConstraint(adlParser.OrdinalConstraintContext context) {
        CDvOrdinal result = new CDvOrdinal();
        result.setRmTypeName("DV_ORDINAL");
        for (adlParser.OrdinalItemContext cOrdinalItem : context.ordinalItemList().ordinalItem()) {
            result.getList().add(parseOrdinalItem(cOrdinalItem));
        }
        if (context.number() != null) {
            int number = parseInteger(context.number());
            for (DvOrdinal dvOrdinal : result.getList()) {
                if (dvOrdinal.getValue() == number) {
                    result.setAssumedValue(AdlUtils.makeClone(dvOrdinal));
                    break;
                }
            }
        }
        return result;
    }

    private static DvOrdinal parseOrdinalItem(adlParser.OrdinalItemContext context) {
        DvOrdinal result = new DvOrdinal();
        result.setValue(parseInteger(context.number()));
        CodePhrase code = parseCodePhrase(context.odinCodePhraseValue());
        result.setSymbol(newDvCodedText(code.getCodeString(), code));
        return result;
    }

    private static CodePhrase parseCodePhrase(adlParser.OdinCodePhraseValueContext context) {
        return newCodePhrase(newTerminologyId(collectText(context.tid)), collectText(context.code));
    }

    private static CTerminologyCode parseCodePhrase(adlParser.CodePhraseConstraintContext context) {
        CTerminologyCode result = new CTerminologyCode();
        result.setRmTypeName(RmTypes.TERMINOLOGY_CODE);

        result.setTerminologyId(collectText(context.tid));
        if (context.assumed!=null) {
            result.setAssumedValue(collectText(context.assumed));
        }
        for (adlParser.CodeIdentifierContext cCodeIdentifier : context.codeIdentifierList().codeIdentifier()) {
            result.getCodeList().add(collectText(cCodeIdentifier));
        }

        return result;
    }
//    private static CCodePhrase parseCodePhrase(adlParser.CodePhraseConstraintContext context) {
//        CCodePhrase result = new CCodePhrase();
//        result.setTerminologyId(newTerminologyId(collectText(context.tid)));
//        if (context.assumed!=null) {
//            result.setAssumedValue(newCodePhrase(newTerminologyId(collectText(context.tid)), collectText(context.assumed)));
//        }
//        for (adlParser.CodeIdentifierContext cCodeIdentifier : context.codeIdentifierList().codeIdentifier()) {
//            result.getCodeList().add(collectText(cCodeIdentifier));
//        }
//
//        return result;
//    }

    private static Cardinality parseCardinality(adlParser.CardinalityContext context) {
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


    private static MultiplicityInterval parseOccurrences(adlParser.OccurrencesContext context) {
        if (context == null) return null;
        return parseOccurrences(context.occurrenceRange());

    }

    private static MultiplicityInterval parseOccurrences(adlParser.OccurrenceRangeContext context) {
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
    private static SiblingOrder parseSiblingOrder(@Nullable adlParser.OrderConstraintContext context) {
        if (context == null) return null;
        SiblingOrder result = new SiblingOrder();
        result.setIsBefore(context.BEFORE() != null);
        result.setSiblingNodeId(parseAtCode(context.atCode()));
        return result;
    }

}
