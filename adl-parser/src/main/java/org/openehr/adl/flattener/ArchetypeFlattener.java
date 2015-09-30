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

package org.openehr.adl.flattener;

import org.openehr.adl.rm.RmModel;
import org.openehr.jaxb.am.Archetype;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static org.openehr.adl.util.AdlUtils.createFlatArchetypeClone;

/**
 * Flattens a specialized archetype with a flat parent
 *
 * @author markopi
 */
public class ArchetypeFlattener {
    private final ArchetypeMerger merger;
    private final RmModel rmModel;

    public ArchetypeFlattener(RmModel rmModel) {
        merger = new ArchetypeMerger(rmModel);
        this.rmModel = rmModel;
    }

    /**
     * Flattens a specialized source archetype
     *
     * @param flatParent   Parent archetype. Must already be flattened. Can be null if differentialArchetype is not specialized
     * @param differential Differential (source) archetype
     * @return Specialized archetype in flattened form
     */
    public Archetype flatten(@Nullable Archetype flatParent, Archetype differential) {
        checkArgument(flatParent == null || !flatParent.isIsDifferential(), "flatParent: Flat parent must be a flat archetype or null");
        checkArgument(differential.isIsDifferential(), "differential: Can only flatten a differential archetype");

        Archetype result = createFlatArchetypeClone(differential);

        if (differential.getParentArchetypeId() != null) {
            if (flatParent == null || !flatParent.getArchetypeId().getValue().startsWith(differential.getParentArchetypeId().getValue())) {
                throw new AdlFlattenerException(String.format("Wrong or missing parent archetype: expected %s, got %s",
                        differential.getParentArchetypeId().getValue(),
                        flatParent != null ? flatParent.getArchetypeId().getValue() : null));
            }
            merger.merge(flatParent, result);
        }

//        fillDefaultOccurrences(null, result.getDefinition());


        return result;
    }


//    private void fillDefaultOccurrences(CAttribute parentAttr, CObject cobj) {
//        if (cobj.getOccurrences() == null) {
//            if (parentAttr != null) {
//                if (parentAttr.getCardinality() != null) {
//                    cobj.setOccurrences(AdlUtils.makeClone(parentAttr.getCardinality().getInterval()));
//                } else {
//                    cobj.setOccurrences(AdlUtils.makeClone(parentAttr.getExistence()));
//                }
//            } else {
//                cobj.setOccurrences(newMultiplicityInterval(1, 1));
//            }
//        }
//
//        if (cobj instanceof CComplexObject) {
//            CComplexObject complexObj = (CComplexObject) cobj;
//            for (CAttribute attribute : complexObj.getAttributes()) {
//                fillDefaultExistence(complexObj, attribute);
//            }
//            for (CAttributeTuple cAttributeTuple : complexObj.getAttributeTuples()) {
//                for (CObjectTuple cObjectTuple : cAttributeTuple.getChildren()) {
//                    for (CPrimitiveObject cPrimitiveObject : cObjectTuple.getMembers()) {
//                        fillDefaultOccurrences(null, cPrimitiveObject);
//                    }
//                }
//            }
//        }
//    }
//
//    private void fillDefaultExistence(CComplexObject parentConstraint, CAttribute attribute) {
//        if (attribute.getExistence() == null || attribute.getCardinality() == null) {
//            RmTypeAttribute typeAttribute = null;
//            try {
//                typeAttribute = rmModel.getRmAttribute(parentConstraint.getRmTypeName(), attribute.getRmAttributeName());
//                if (attribute.getExistence() == null) {
//                    attribute.setExistence(AdlUtils.makeClone(typeAttribute.getExistence()));
//                }
//                if (attribute.getCardinality() == null) {
//                    attribute.setCardinality(AdlUtils.makeClone(typeAttribute.getCardinality()));
//                }
//            } catch (RmModelException e) {
//                attribute.setExistence(newMultiplicityInterval(0, 1));
//            }
//        }
//        for (CObject cObject : attribute.getChildren()) {
//            fillDefaultOccurrences(attribute, cObject);
//        }
//    }


}
