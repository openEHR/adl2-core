
/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.validator;


import org.openehr.am.AmObject;
import org.openehr.adl.am.mixin.AmMixins;
import org.openehr.adl.rm.RmModel;
import org.openehr.adl.rm.RmModelException;
import org.openehr.adl.rm.RmTypeAttribute;
import org.openehr.adl.am.AmQuery;
import org.openehr.adl.rm.RmPath;
import org.openehr.adl.util.walker.*;
import org.openehr.jaxb.am.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Validator for archetype parsed from ADL files
 * <p/>
 * It checks
 * .Existence of class and atribute
 * .Use node by object path in cADL
 * .Decomposed date with dADL
 * .Term definition and binding checking
 *
 * @author Rong Chen
 * @version 1.0
 */
public class ArchetypeValidator {
    private final RmModel rmModel;
    private final FlatArchetype archetype;
    private final List<AqlValidationError> errors = new ArrayList<>();

    /**
     * Constructs an ArchetypeValidator
     *
     * @param archetype The Archetype to be validated
     */
    public ArchetypeValidator(RmModel rmModel, FlatArchetype archetype) {
        this.rmModel = rmModel;
        this.archetype = archetype;
    }

    /**
     * Check if information model entity referenced by archetype
     * has right name or type
     *
     * @return true if valid
     */
    void checkRmModelConformance() {
        final AmVisitor<AmObject, AmConstraintContext> visitor = AmVisitors.preorder(new ConformanceVisitor());
        ArchetypeWalker.walkConstraints(visitor, archetype, new AmConstraintContext());
    }


    // todo: check if archetypeSlot has cyclical reference

    // todo: check if archetypeInternalRef has valid target, cyclical reference

    public void validate() {
        checkInternalReferences();
        checkRmModelConformance();
    }


    public List<AqlValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    void checkInternalReferences() {
        checkInternalReferences(archetype.getDefinition(), RmPath.ROOT);
    }


    private void checkInternalReferences(CComplexObject ccobj, RmPath path) {
        for (CAttribute cattribute : ccobj.getAttributes()) {
            for (CObject cobj : cattribute.getChildren()) {
                if (cobj instanceof ArchetypeInternalRef) {
                    ArchetypeInternalRef ref = (ArchetypeInternalRef) cobj;
                    ArchetypeConstraint target = AmQuery.find(archetype, ref.getTargetPath());

                    if (target == null) {
                        // target unknown
                        error(AqlValidationError.Level.ERROR,
                                "Internal reference at %s refers to an unknown target: %s",
                                path.resolve(cattribute.getRmAttributeName(), cobj.getNodeId()).toString(),
                                ref.getTargetPath());
                    } else if ((!((CObject) target).getRmTypeName().equals(cobj.getRmTypeName()))) {
                        // bad rmType
                        error(AqlValidationError.Level.ERROR,
                                "Internal reference at %s has a different rmType than target %s: %s!=%s",
                                path.resolve(cattribute.getRmAttributeName(), cobj.getNodeId()).toString(),
                                ref.getTargetPath(),
                                cobj.getRmTypeName(),
                                ((CObject) target).getRmTypeName()
                             );
                    }
                }
                if (cobj instanceof CComplexObject) {
                    checkInternalReferences((CComplexObject) cobj,
                            path.resolve(cattribute.getRmAttributeName(), cobj.getNodeId()));
                }
            }
        }
    }

    private void error(AqlValidationError.Level level, String messagePattern, Object... arguments) {
        final String message = arguments.length > 0 ? String.format(messagePattern, arguments) : messagePattern;
        errors.add(new AqlValidationError(level, message));
    }


    private class ConformanceVisitor implements AmSinglePhaseVisitor<AmObject, AmConstraintContext> {

        @Override
        public ArchetypeWalker.Action<? extends CComplexObject> visit(AmObject item, AmConstraintContext context) {
            if (item instanceof CAttribute) {
                CAttribute attr = (CAttribute) item;
                final CComplexObject container = (CComplexObject) context.getAmParents().getLast();
                try {
                    RmTypeAttribute rmAttribute = rmModel.getRmAttribute(
                            container.getRmTypeName(), attr.getRmAttributeName());
                    if (!AmMixins.of(rmAttribute.getExistence()).contains(attr.getExistence())) {
                        error(AqlValidationError.Level.ERROR,
                                "Existence of attribute at %s/%s does not conform to RM model: %s is not contained in %s",
                                context.getRmPath(), attr.getRmAttributeName(),
                                AmMixins.of(attr.getExistence()), AmMixins.of(rmAttribute.getExistence())
                             );
                    }
                } catch (RmModelException e) {
                    error(AqlValidationError.Level.ERROR, e.getMessage());
                }

            }
            return ArchetypeWalker.Action.next();
        }
    }

}
