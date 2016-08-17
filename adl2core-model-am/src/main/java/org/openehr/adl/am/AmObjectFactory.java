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

package org.openehr.adl.am;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang.ObjectUtils;
import org.openehr.adl.rm.RmTypes;
import org.openehr.adl.rm.RmUtils;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.*;
import org.openehr.rm.RmObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

import static org.openehr.adl.rm.RmObjectFactory.newStringDictionaryItem;
import static org.openehr.adl.rm.RmUtils.emptyIfNull;


/**
 * @author markopi
 */
@SuppressWarnings("unused")
public class AmObjectFactory {
    private static final String AM_PACKAGE_NAME = Archetype.class.getPackage().getName();
    private static final String RM_PACKAGE_NAME = Composition.class.getPackage().getName();

    public static CComplexObject newCComplexObject(String type, MultiplicityInterval occurrences, String nodeID,
            List<CAttribute> attributes) {
        CComplexObject result = new CComplexObject();
        result.setNodeId(nodeID);
        result.setOccurrences(occurrences);
        result.setRmTypeName(type);
        result.getAttributes().addAll(emptyIfNull(attributes));
        return result;
    }

    public static ConstraintRef newConstraintRef(String rmType, MultiplicityInterval occurrences, String nodeId,
            String reference) {
        ConstraintRef result = new ConstraintRef();
        result.setRmTypeName(rmType);
        result.setOccurrences(occurrences);
        result.setNodeId(nodeId);
        result.setReference(reference);
        return result;
    }

    public static ArchetypeInternalRef newArchetypeInternalRef(String rmType, MultiplicityInterval occurrences, String nodeId,
            String target) {
        ArchetypeInternalRef result = new ArchetypeInternalRef();
        result.setRmTypeName(rmType);
        result.setOccurrences(occurrences);
        result.setNodeId(nodeId);
        result.setTargetPath(target);
        return result;
    }

    public static ArchetypeSlot newArchetypeSlot(String rmType, MultiplicityInterval occurrences, String nodeID, Set<Assertion> includes,
            Set<Assertion> excludes) {
        ArchetypeSlot result = new ArchetypeSlot();
        result.setNodeId(nodeID);
        result.setOccurrences(occurrences);
        result.setRmTypeName(rmType);
        result.getIncludes().addAll(emptyIfNull(includes));
        result.getExcludes().addAll(emptyIfNull(excludes));
        return result;
    }


    public static CAttribute newCAttribute(String name) {
        return newCAttribute(name, null, null, null);
    }

    public static CAttribute newCAttribute(String name, MultiplicityInterval existence, Cardinality cardinality,
            List<CObject> children) {
        CAttribute result = new CAttribute();
        result.setRmAttributeName(name);
        result.setExistence(existence);
        result.setCardinality(cardinality);
        result.getChildren().addAll(emptyIfNull(children));
        return result;
    }


    public static CodeDefinitionSet newCodeDefinitionSet(String language, List<ArchetypeTerm> terms) {
        CodeDefinitionSet result = new CodeDefinitionSet();
        result.setLanguage(language);
        result.getItems().addAll(emptyIfNull(terms));
        return result;
    }

    public static TermBindingSet newTermBindingSet(String terminology, List<TermBindingItem> bindings) {
        TermBindingSet result = new TermBindingSet();
        result.setTerminology(terminology);
        result.getItems().addAll(emptyIfNull(bindings));
        return result;
    }

    public static TermBindingItem newTermBindingItem(String code, String value) {
        TermBindingItem result = new TermBindingItem();
        result.setCode(code);
        result.setValue(value);
        return result;
    }

    public static ArchetypeTerm newArchetypeTerm(String nodeId, String text) {
        return newArchetypeTerm(nodeId, text, null, null);
    }


    public static ArchetypeTerm newArchetypeTerm(String nodeId, String text, String description) {
        return newArchetypeTerm(nodeId, text, description, null);
    }

    public static ArchetypeTerm newArchetypeTerm(String nodeId, String text, String description, String comment) {
        ArchetypeTerm result = new ArchetypeTerm();
        result.setCode(nodeId);
        if (text!=null) {
            result.getItems().add(newStringDictionaryItem("text", text));
        }
        if (description!=null) {
            result.getItems().add(newStringDictionaryItem("description", description));
        }
        if (comment!=null) {
            result.getItems().add(newStringDictionaryItem("text", text));
        }
        return result;

    }

    public static ValueSetItem newValueSetItem(String id, List<String> members) {
        ValueSetItem result = new ValueSetItem();
        result.setId(id);
        result.getMembers().addAll(members);
        return result;
    }


    public static ConstraintBindingSet newConstraintBindingSet(String terminology, List<ConstraintBindingItem> bindings) {
        ConstraintBindingSet result = new ConstraintBindingSet();
        result.setTerminology(terminology);
        result.getItems().addAll(emptyIfNull(bindings));
        return result;
    }

    public static ConstraintBindingItem newConstraintBindingItem(String code, String value) {
        ConstraintBindingItem result = new ConstraintBindingItem();
        result.setCode(code);
        result.setValue(value);
        return result;
    }

    public static Assertion newAssertion(String tag, ExprItem expression, String stringExpression, List<AssertionVariable> variables) {
        Assertion result = new Assertion();
        result.setTag(tag);
        result.setExpression(expression);
        result.setStringExpression(stringExpression);
        result.getVariables().addAll(emptyIfNull(variables));
        return result;
    }

    public static Cardinality newCardinality(boolean ordered, boolean unique, MultiplicityInterval interval) {
        Cardinality result = new Cardinality();
        result.setIsOrdered(ordered);
        result.setIsUnique(unique);
        result.setInterval(interval);
        return result;
    }

    public static CDvOrdinal newCDvOrdinal(MultiplicityInterval occurrences, String nodeId, List<DvOrdinal> list, DvOrdinal defaultValue,
            DvOrdinal assumedValue) {
        CDvOrdinal result = new CDvOrdinal();
        result.setRmTypeName(RmUtils.getRmTypeName(DvOrdinal.class));
        result.setOccurrences(occurrences);
        result.setNodeId(nodeId);
        result.getList().addAll(emptyIfNull(list));
        result.setAssumedValue(assumedValue);

        return result;
    }

    public static CCodePhrase newCCodePhrase(MultiplicityInterval occurrences, String nodeId, TerminologyId terminologyId,
            List<String> codeList, CodePhrase defaultValue, CodePhrase assumedValue) {
        CCodePhrase result = new CCodePhrase();
        result.setOccurrences(occurrences);
        result.setRmTypeName(RmUtils.getRmTypeName(CodePhrase.class));
        result.setNodeId(nodeId);
        result.setTerminologyId(terminologyId);
        result.getCodeList().addAll(emptyIfNull(codeList));
        result.setAssumedValue(assumedValue);
        result.setDefaultValue(defaultValue);
        return result;
    }

    public static CDvQuantity newCDvQuantity(MultiplicityInterval occurrences, String nodeId, List<CQuantityItem> list, CodePhrase property,
            DvQuantity defaultValue, DvQuantity assumedValue) {
        CDvQuantity result = new CDvQuantity();
        result.setOccurrences(occurrences);
        result.setRmTypeName(RmUtils.getRmTypeName(DvQuantity.class));
        result.setNodeId(nodeId);
        result.getList().addAll(emptyIfNull(list));
        result.setProperty(property);
        result.setAssumedValue(assumedValue);
        result.setDefaultValue(defaultValue);
        return result;
    }

    public static CQuantityItem newCQuantityItem(IntervalOfReal magnitude, IntervalOfInteger precision, String units) {
        CQuantityItem result = new CQuantityItem();
        result.setUnits(units);
        result.setMagnitude(magnitude);
        result.setPrecision(precision);
        return result;
    }

    public static CInteger newCInteger(IntervalOfInteger interval, @Nullable Integer assumedValue) {
        CInteger result = new CInteger();
        result.setAssumedValue(assumedValue);
        if (interval!=null) {
            result.getConstraint().add(interval);
        }
        return result;
    }


    public static CReal newCReal(IntervalOfReal interval, @Nullable Float assumedValue) {
        CReal result = new CReal();
        result.setAssumedValue(assumedValue);
        if (interval!=null) {
            result.getConstraint().add(interval);
        }
        return result;

    }

    public static CDate newCDate(String pattern, IntervalOfDate interval, List<DvDate> list, String assumed) {
        CDate result = new CDate();
        result.setPatternConstraint(pattern);
        if (interval!=null) {
            result.getConstraint().add(interval);
        }
        result.setAssumedValue(assumed);
        return result;
    }

    public static CTime newCTime(String pattern, IntervalOfTime interval, List<DvTime> list, String assumed) {
        CTime result = new CTime();
        result.setPatternConstraint(pattern);
        if (interval!=null) {
            result.getConstraint().add(interval);
        }
        result.setAssumedValue(assumed);
        return result;
    }

    public static CDateTime newCDateTime(String pattern, IntervalOfDateTime interval, List<DvDateTime> list, String assumed) {
        CDateTime result = new CDateTime();
        result.setPatternConstraint(pattern);
        if (interval!=null) {
            result.getConstraint().add(interval);
        }
        result.setAssumedValue(assumed);
        return result;
    }

    public static CDuration newCDuration(String pattern, IntervalOfDuration interval, String assumed) {
        CDuration result = new CDuration();
        result.setPatternConstraint(pattern);
        if (interval!=null) {
            result.getConstraint().add(interval);
        }
        result.setAssumedValue(assumed);
        return result;
    }

    public static CString newCString(String pattern, List<String> list, String assumed) {
        CString result = new CString();
        result.setPattern(pattern);
        result.getConstraint().addAll(emptyIfNull(list));
        result.setAssumedValue(assumed);
        return result;
    }

    public static CBoolean newCBoolean(boolean trueValid, boolean falseValid, Boolean assumedValue) {
        CBoolean result = new CBoolean();
        if (trueValid) {
            result.getConstraint().add(true);
        }
        if (falseValid) {
            result.getConstraint().add(false);
        }
        result.setAssumedValue(assumedValue);
        return result;
    }

    public static CTerminologyCode newCTerminologyCode(String constraint, String assumedValue) {
        CTerminologyCode result = new CTerminologyCode();
        // CTerminologyCode should be modified to have a 'constraint' field instead of this.
        if (constraint!=null) {
            result.setConstraint(constraint);
        }
        result.setAssumedValue(assumedValue);
        return result;
    }



    public static ExprLeaf newExprPathConstant(String path) {
        return newExprConstant(RmTypes.RM, path);
    }

    public static ExprLeaf newExprConstant(String rmType, Object value) {
        return newExprLeaf(rmType, RmTypes.ReferenceType.CONSTANT, value);
    }

    public static ExprLeaf newExprConstraint(String rmType, Object value) {
        return newExprLeaf(rmType, RmTypes.ReferenceType.CONSTRAINT, value);
    }

    public static ExprLeaf newExprAttribute(String rmType, Object value) {
        return newExprLeaf(rmType, RmTypes.ReferenceType.ATTRIBUTE, value);
    }

    public static ExprLeaf newExprLeaf(String type, RmTypes.ReferenceType referenceType, Object value) {
        ExprLeaf result = new ExprLeaf();
        result.setType(type);
        result.setReferenceType(ObjectUtils.toString(referenceType));
        result.setItem(value);
        return result;
    }

    public static ExprUnaryOperator newExprUnaryOperator(String type, OperatorKind op, boolean precedenceOverridden, ExprItem operand) {
        ExprUnaryOperator result = new ExprUnaryOperator();
        result.setType(type);

        result.setOperator(op);
        result.setPrecedenceOverridden(precedenceOverridden);
        result.setOperand(operand);
        return result;
    }

    public static ExprBinaryOperator newExprBinaryOperator(String type, OperatorKind op, boolean precedenceOverridden, ExprItem left,
            ExprItem right) {
        ExprBinaryOperator result = new ExprBinaryOperator();
        result.setType(type);
        result.setOperator(op);
        result.setPrecedenceOverridden(precedenceOverridden);
        result.setLeftOperand(left);
        result.setRightOperand(right);
        return result;
    }


    public static SiblingOrder newSiblingOrder(boolean isBefore, String siblingNodeId) {
        SiblingOrder result = new SiblingOrder();
        result.setIsBefore(isBefore);
        result.setSiblingNodeId(siblingNodeId);
        return result;
    }


    public static Class<? extends RmObject> getRmClass(@Nonnull String rmTypeName) throws ClassNotFoundException {
        int genericsTypeIndex = rmTypeName.indexOf('<');
        String name = genericsTypeIndex == -1 ? rmTypeName : rmTypeName.substring(0, genericsTypeIndex);
        String className = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
        //noinspection unchecked
        return (Class<RmObject>) Class.forName(RM_PACKAGE_NAME + '.' + className);
    }

}
