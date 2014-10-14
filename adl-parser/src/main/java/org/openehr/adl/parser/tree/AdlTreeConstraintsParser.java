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


import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.openehr.adl.am.OperatorKind;
import org.openehr.adl.antlr.AdlParser;
import org.openehr.adl.parser.RuntimeRecognitionException;
import org.openehr.adl.rm.RmModel;
import org.openehr.adl.rm.RmPath;
import org.openehr.adl.rm.RmTypes;
import org.openehr.adl.util.AdlUtils;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.DvQuantity;
import org.openehr.jaxb.rm.IntervalOfInteger;
import org.openehr.jaxb.rm.IntervalOfReal;
import org.openehr.jaxb.rm.MultiplicityInterval;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.openehr.adl.am.AmObjectFactory.*;
import static org.openehr.adl.rm.RmObjectFactory.newArchetypeId;
import static org.openehr.adl.rm.RmObjectFactory.newMultiplicityInterval;
import static org.openehr.adl.util.AdlUtils.makeClone;

/**
 * @author markopi
 */
class AdlTreeConstraintsParser extends AbstractAdlTreeParser {
    private final AdlTreeDAdlParser dadl;
    private final AdlTreePrimitiveConstraintsParser primitives;

    AdlTreeConstraintsParser(CommonTokenStream tokenStream, CommonTree adlTree,
            AdlTreeParserState state, AdlTreeDAdlParser dadl, RmModel rmModel) {
        super(tokenStream, adlTree, state, rmModel);
        this.dadl = dadl;
        primitives = new AdlTreePrimitiveConstraintsParser(tokenStream, adlTree, state, dadl, rmModel);
    }


    CComplexObject parseTypeConstraint(Tree tTypeDefinition) {
        CComplexObject result = new CComplexObject();
        int index = 0;
        if (tTypeDefinition.getChild(index).getType() == AdlParser.BEFORE ||
            tTypeDefinition.getChild(index).getType() == AdlParser.AFTER) {
            Tree tLocation = tTypeDefinition.getChild(index);
            result.setSiblingOrder(newSiblingOrder(
                    tLocation.getType() == AdlParser.BEFORE,
                    tLocation.getChild(0).getText()));
            index++;
        }


        String rmType = collectText(tTypeDefinition.getChild(index++));
        result.setRmTypeName(rmType);

        String nodeId = null;
        if (tTypeDefinition.getChild(index).getType() == AdlParser.AT_CODE_VALUE) {
            nodeId = tTypeDefinition.getChild(index++).getText();
        }
        result.setNodeId(nodeId);

        if (tTypeDefinition.getChild(index).getType() == AdlParser.OCCURRENCES) {
            result.setOccurrences(parseOccurrences(tTypeDefinition.getChild(index++).getChild(0)));
        } else {
            result.setOccurrences(newMultiplicityInterval(1, 1));
        }

        parseTypeValueConstraint(result, tTypeDefinition.getChild(index));

        return result;
    }

    private MultiplicityInterval parseOccurrences(Tree tInterval) {
        MultiplicityInterval result = parseMultiplicityInterval(tInterval);
        if (result.getLower() == null) {
            result.setLower(0);
            result.setLowerUnbounded(false);
            result.setLowerIncluded(true);
        }
        return result;
    }

    private void parseTypeValueConstraint(CComplexObject target, Tree tValueConstraint) {
        switch (tValueConstraint.getType()) {
            case AdlParser.AST_ATTRIBUTE_CONSTRAINT_LIST:
                parseAttributeConstraintList(target, tValueConstraint);
                break;
            case AdlParser.AST_CONSTRAINT_ANY:
                break;
            case AdlParser.AST_NULL:
                break;
            default:
                throw new RuntimeRecognitionException("Unexpected token type", tValueConstraint);
        }
    }

    private void parseAttributeConstraintList(CComplexObject target, Tree tAttributeList) {
        if (tAttributeList.getType() == AdlParser.AST_CONSTRAINT_ANY) {
            return;
        }
        assertTokenType(tAttributeList, AdlParser.AST_ATTRIBUTE_CONSTRAINT_LIST);
        for (Tree tAttribute : children(tAttributeList)) {
            switch (tAttribute.getType()) {
                case AdlParser.AST_ATTRIBUTE_CONSTRAINT:
                    target.getAttributes().add(parseAttributeConstraint(tAttribute));
                    break;
                case AdlParser.AST_ATTRIBUTE_SECOND_ORDER_CONSTRAINT:
                    target.getAttributeTuples().add(parseAttributeSecondOrderConstraint(tAttribute));
                    break;

                default:
                    throw new RuntimeRecognitionException("Unexpected token type", tAttribute);
            }
        }
    }

    private CAttributeTuple parseAttributeSecondOrderConstraint(Tree tAttribute) {
        assertTokenType(tAttribute, AdlParser.AST_ATTRIBUTE_SECOND_ORDER_CONSTRAINT);
        CAttributeTuple result = new CAttributeTuple();

        int index = 0;
        List<String> attributeNames = parseAttributeNames(child(tAttribute, index++, AdlParser.AST_LIST));

        final MultiplicityInterval existence;
        if (tAttribute.getChild(index).getType() == AdlParser.EXISTENCE) {
            existence = parseMultiplicityInterval(tAttribute.getChild(index++).getChild(0));
        } else {
            existence = newMultiplicityInterval(1, 1);
        }

        for (String attributeName : attributeNames) {
            CAttribute attribute = new CAttribute();
            attribute.setRmAttributeName(attributeName);
            attribute.setExistence(makeClone(existence));

            result.getMembers().add(attribute);
        }


        Tree tConstraints = child(tAttribute, index, AdlParser.AST_LIST);

        for (Tree tSingleTuple : children(tConstraints)) {
            require(tSingleTuple.getChildCount() == result.getMembers().size(), tSingleTuple,
                    "Unexpected object tuple length. Expected %d, actual: 5d",
                    result.getMembers().size(), tSingleTuple.getChildCount());

            CObjectTuple tuple = new CObjectTuple();
            for (Tree tSingleElement : children(tSingleTuple)) {
                tuple.getMembers().add(primitives.parsePrimitiveValueConstraint(tSingleElement));
            }
            result.getChildren().add(tuple);
        }


        return result;
    }

    private List<String> parseAttributeNames(Tree tAttributeList) {
        List<String> result = new ArrayList<>();
        for (Tree tAttributeName : children(tAttributeList)) {
            result.add(collectText(tAttributeName));
        }
        return result;
    }

    private CAttribute parseAttributeConstraint(Tree tAttribute) {
        int index = 0;
        Tree attributeNode = tAttribute.getChild(index++);
        final String attributeNameOrPath = checkNotNull(collectText(attributeNode));


        final MultiplicityInterval existence;
        if (isType(tAttribute.getChild(index), AdlParser.EXISTENCE)) {
            existence = parseMultiplicityInterval(tAttribute.getChild(index++).getChild(0));
        } else {
            existence = newMultiplicityInterval(1, 1);
        }

        final Cardinality cardinality;
        if (isType(tAttribute.getChild(index), AdlParser.CARDINALITY)) {
            cardinality = parseCardinality(tAttribute.getChild(index++));
        } else {
            cardinality = null;
        }

        final CAttribute result = new CAttribute();
        result.setCardinality(cardinality);

        if (attributeNameOrPath.startsWith("/")) {
            assertAdlV15("path based attributes", attributeNode);
            String actualAttributeName = RmPath.valueOf(attributeNameOrPath).getAttribute();
            result.setRmAttributeName(actualAttributeName);
            result.setDifferentialPath(attributeNameOrPath);
        } else {
            result.setRmAttributeName(attributeNameOrPath);
        }

        result.setExistence(existence);

        if (isType(tAttribute.getChild(index), AdlParser.MATCHES)) {
            Tree tMatches = tAttribute.getChild(index++);
            result.setMatchNegated(tMatches.getChild(0).getType() == AdlParser.FALSE);
        }

        while (index < tAttribute.getChildCount()) {
            Tree tConstraint = tAttribute.getChild(index++);
            parsedValueConstraint(result, tConstraint);
        }
        return result;

    }

    private boolean isEmptyInterval(MultiplicityInterval interval) {
        return Integer.valueOf(0).equals(interval.getLower()) && Integer.valueOf(0).equals(interval.getUpper());
    }

    private void parsedValueConstraint(CAttribute result, Tree tConstraint) {


        switch (tConstraint.getType()) {
            case AdlParser.AST_TYPE_CONSTRAINT:
                result.getChildren().add(parseTypeConstraint(tConstraint));
                break;
            case AdlParser.AST_CONSTRAINT_ANY:
                // no constraints
                break;
            case AdlParser.AST_USE_NODE_CONSTRAINT:
                result.getChildren().add(parseUseNodeConstraint(tConstraint));
                break;
            case AdlParser.AST_PRIMITIVE_VALUE_CONSTRAINT:
                result.getChildren().add(primitives.parsePrimitiveValueConstraint(tConstraint));
                break;
            case AdlParser.AST_ARCHETYPE_SLOT_CONSTRAINT:
                result.getChildren().add(parseArchetypeSlotConstraint(tConstraint));
                break;
            case AdlParser.USE_ARCHETYPE:
                result.getChildren().add(parseArchetypeReferenceConstraint(tConstraint));
                break;
            case AdlParser.USE_TEMPLATE:
                result.getChildren().add(parseArchetypeReferenceConstraint(tConstraint));
                break;
            case AdlParser.AST_CODE_PHRASE_CONSTRAINT:
                result.getChildren().add(primitives.parseCodeConstraint(tConstraint));
                break;
            case AdlParser.AST_ADL_VALUE_CONSTRAINT:
                result.getChildren().add(parseAdlValueConstraint(tConstraint));
                break;
            case AdlParser.AST_ORDINAL_CONSTRAINT:
                result.getChildren().add(primitives.parseCDvOrdinalConstraint(tConstraint));
                break;
            case AdlParser.AST_CONSTRAINT_REF:
                result.getChildren().add(parseConstraintRef(tConstraint));
                break;
            default:
                throw new RuntimeRecognitionException(tConstraint);
        }
    }

    private CArchetypeRoot parseArchetypeReferenceConstraint(Tree tArchetypeReference) {
        assertTokenType(tArchetypeReference, AdlParser.USE_ARCHETYPE, AdlParser.USE_TEMPLATE);
        CArchetypeRoot result = new CArchetypeRoot();

        int index = 0;
        result.setRmTypeName(collectText(tArchetypeReference.getChild(index++)));
        if (isType(tArchetypeReference.getChild(index), AdlParser.AT_CODE_VALUE)) {
            result.setNodeId(tArchetypeReference.getChild(index++).getText());
        }
        result.setArchetypeId(newArchetypeId(checkNotNull(collectText(tArchetypeReference.getChild(index++)))));

        if (isType(tArchetypeReference.getChild(index), AdlParser.OCCURRENCES)) {
            result.setOccurrences(parseOccurrences(tArchetypeReference.getChild(index++).getChild(0)));
        }
        parseAttributeConstraintList(result, tArchetypeReference.getChild(index));

        return result;
    }


    private ConstraintRef parseConstraintRef(Tree tConstraint) {
        assertTokenType(tConstraint, AdlParser.AST_CONSTRAINT_REF);

        ConstraintRef result = new ConstraintRef();
        result.setRmTypeName(AdlUtils.getRmTypeName(ConstraintRef.class));
        result.setReference(collectText(tConstraint.getChild(0)));
        return result;
    }


    private CObject parseAdlValueConstraint(Tree tConstraint) {
        assertTokenType(tConstraint, AdlParser.AST_ADL_VALUE_CONSTRAINT);

        String amType = checkNotNull(collectText(tConstraint.getChild(0)));
        Tree tAdlValue = tConstraint.getChild(1);
        CObject result;
        switch (amType) {
            case "C_DV_QUANTITY":
                result = parseCDvQuantityConstraint(tAdlValue);
                break;
            default:
                try {
                    Class<? extends CObject> cclass = AdlUtils.getAmClass(amType);
                    result = cclass.newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    throw new AdlTreeParserException("Error instantiating am class: " + amType, tokenOf(tAdlValue), e);
                }
        }
        result.setRmTypeName(amType.substring(2)); // skip leading "C_";
        return result;
    }

    private CDvQuantity parseCDvQuantityConstraint(Tree tConstraint) {
        CDvQuantity result = new CDvQuantity();
        result.setRmTypeName(rmModel.getRmTypeName(DvQuantity.class));

        if (tConstraint.getType() == AdlParser.AST_NULL) return result;


        DAdlObject object = dadl.parseAdlObject(tConstraint);

        Tree tProperty = object.tryGet("property");
        if (tProperty != null) {
            result.setProperty(parseCodePhraseListSingleItem(tProperty));
        }
        Tree tList = object.tryGet("list");
        if (tList != null) {
            Map<String, Tree> list = dadl.parseAdlMap(tList);
            for (Tree tItem : list.values()) {
                DAdlObject objItem = dadl.parseAdlObject(tItem);
                CQuantityItem item = new CQuantityItem();

                if (objItem.contains("magnitude")) {
                    item.setMagnitude(parseIntervalOfReal(objItem.get("magnitude")));
                }
                if (objItem.contains("precision")) {
                    item.setPrecision(parseIntervalOfInteger(objItem.get("precision")));
                }
                item.setUnits(collectString(objItem.get("units")));
                result.getList().add(item);
            }
        }
        Tree tAssumed = object.tryGet("assumed_value");
        if (tAssumed != null) {
            DAdlObject objItem = dadl.parseAdlObject(tAssumed);
            DvQuantity item = new DvQuantity();
            item.setMagnitude(parseFloat(objItem.get("magnitude")));
            item.setPrecision(parseInteger(objItem.get("precision")));
            item.setUnits(collectString(objItem.get("units")));
            result.setAssumedValue(item);
        }

        return result;
    }


    private Cardinality parseCardinality(Tree tCardinality) {
        Cardinality cardinality;
        cardinality = new Cardinality();
        cardinality.setInterval(parseMultiplicityInterval(tCardinality.getChild(0)));
        int index = 1;
        while (index < tCardinality.getChildCount()) {
            switch (tCardinality.getChild(index).getType()) {
                case AdlParser.ORDERED:
                    cardinality.setIsOrdered(true);
                    break;
                case AdlParser.UNORDERED:
                    cardinality.setIsOrdered(false);
                    break;
                case AdlParser.UNIQUE:
                    cardinality.setIsUnique(true);
                    break;
                default:
                    assertTokenType(tCardinality.getChild(index), AdlParser.ORDERED, AdlParser.UNORDERED, AdlParser.UNIQUE);
            }
            index++;
        }
        return cardinality;
    }

    private ArchetypeSlot parseArchetypeSlotConstraint(Tree tArchetypeSlot) {
        ArchetypeSlot result = new ArchetypeSlot();
        int index = 0;
        result.setRmTypeName(collectText(tArchetypeSlot.getChild(index++)));
        if (tArchetypeSlot.getChild(index).getType() == AdlParser.AT_CODE_VALUE) {
            result.setNodeId(tArchetypeSlot.getChild(index++).getText());
        }
        if (isType(tArchetypeSlot.getChild(index), AdlParser.OCCURRENCES)) {
            result.setOccurrences(parseOccurrences(tArchetypeSlot.getChild(index++).getChild(0)));
        }

        if (isType(tArchetypeSlot.getChild(index), AdlParser.CLOSED)) {
            result.setIsClosed(true);
            index++;
        }


        Tree tArchetypeSlotConstraint = tArchetypeSlot.getChild(index);
        if (tArchetypeSlotConstraint != null) { // if null, then archetype slot is closed
            for (Tree tAssertionList : children(tArchetypeSlotConstraint)) {
                switch (tAssertionList.getType()) {
                    case AdlParser.INCLUDE:
                        parseArchetypeSlotValueConstraint(result.getIncludes(), tAssertionList);
                        break;
                    case AdlParser.EXCLUDE:
                        parseArchetypeSlotValueConstraint(result.getExcludes(), tAssertionList);
                        break;
                    default:
                        throw new RuntimeRecognitionException("Unexpected archetype slot restriction", tAssertionList);
                }
            }
        }

        return result;
    }

    private boolean isType(@Nullable Tree tree, int tokenType) {
        return tree != null && tree.getType() == tokenType;
    }


    private void parseArchetypeSlotValueConstraint(List<Assertion> target, Tree tAssertionList) {
        if (tAssertionList == null) return;
        for (Tree tAssertion : children(tAssertionList)) {
            assertTokenType(tAssertion, AdlParser.AST_ARCHETYPE_SLOT_SINGLE_CONSTRAINT);
            String rmPath = checkNotNull(collectText(tAssertion.getChild(0)));
            //String regexp = collectRegularExpression(tAssertion.getChild(1));

            Assertion assertion = new Assertion();
            assertion.setStringExpression(collectText(tAssertion));
            CPrimitiveObject cPrimitiveObject = primitives.parsePrimitiveValueConstraint(tAssertion.getChild(1));
            ExprBinaryOperator expr = newExprBinaryOperator(
                    RmTypes.ReferenceType.CONSTRAINT.toString(),
                    OperatorKind.OP_MATCHES,
                    false,
                    newExprLeaf(RmTypes.STRING, RmTypes.ReferenceType.ATTRIBUTE, rmPath),
                    newExprLeaf(cPrimitiveObject.getRmTypeName(), RmTypes.ReferenceType.CONSTRAINT,
                            cPrimitiveObject));

            assertion.setExpression(expr);
            target.add(assertion);
        }
    }


    private CObject parseUseNodeConstraint(Tree tUseNode) {
        ArchetypeInternalRef result = new ArchetypeInternalRef();

        assertTokenType(tUseNode, AdlParser.AST_USE_NODE_CONSTRAINT);
        assertTokenType(tUseNode.getChild(0), AdlParser.USE_NODE);

        int index = 1;
        String rmType = collectText(tUseNode.getChild(index++));
        result.setRmTypeName(rmType);

        if (tUseNode.getChild(index).getType() == AdlParser.AT_CODE_VALUE) {
            String nodeId = tUseNode.getChild(index++).getText();
            result.setNodeId(nodeId);

        }

        if (tUseNode.getChild(index).getType() == AdlParser.OCCURRENCES) {
            result.setOccurrences(parseOccurrences(tUseNode.getChild(index++).getChild(0)));
        }

        String targetPath = collectText(tUseNode.getChild(index));
        result.setTargetPath(targetPath);

        return result;
    }

    private IntervalOfInteger parseIntervalOfInteger(Tree tInterval) {
        IntervalOfInteger result = new IntervalOfInteger();
        fillIntervalOfInteger(result, tInterval);

        return result;
    }


    private MultiplicityInterval parseMultiplicityInterval(Tree tInterval) {
        MultiplicityInterval result = new MultiplicityInterval();
        fillIntervalOfInteger(result, tInterval);

        return result;
    }

    private void fillIntervalOfInteger(IntervalOfInteger target, Tree tInterval) {
        assertTokenType(tInterval, AdlParser.AST_NUMBER_INTERVAL_CONSTRAINT);
        target.setLower(parseNullableInteger(tInterval.getChild(0)));
        target.setUpper(parseNullableInteger(tInterval.getChild(1)));
        target.setLowerUnbounded(tInterval.getChild(0).getType() == AdlParser.AST_NULL);
        target.setUpperUnbounded(tInterval.getChild(1).getType() == AdlParser.AST_NULL);
        target.setLowerIncluded(tInterval.getChild(2).getType() == AdlParser.TRUE);
        target.setUpperIncluded(tInterval.getChild(3).getType() == AdlParser.TRUE);
    }

    private IntervalOfReal parseIntervalOfReal(Tree tInterval) {
        assertTokenType(tInterval, AdlParser.AST_NUMBER_INTERVAL_CONSTRAINT);

        IntervalOfReal result = new IntervalOfReal();
        result.setLower(parseNullableFloat(tInterval.getChild(0)));
        result.setUpper(parseNullableFloat(tInterval.getChild(1)));
        result.setLowerUnbounded(tInterval.getChild(0).getType() == AdlParser.AST_NULL);
        result.setUpperUnbounded(tInterval.getChild(1).getType() == AdlParser.AST_NULL);
        result.setLowerIncluded(tInterval.getChild(2).getType() == AdlParser.TRUE);
        result.setUpperIncluded(tInterval.getChild(3).getType() == AdlParser.TRUE);

        return result;
    }

}
