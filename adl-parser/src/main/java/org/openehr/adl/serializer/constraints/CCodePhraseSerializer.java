package org.openehr.adl.serializer.constraints;

import org.openehr.adl.serializer.ArchetypeSerializer;
import org.openehr.jaxb.am.CCodePhrase;

/**
 * <pre>
 *     DV_CODED_TEXT matches {
 * defining_code matches {
 * [local::
 * at0007, 	-- Lying
 * at0008, 	-- Reclining
 * at0009, 	-- Sitting
 * at0010]	-- Standing
 * }
 * }
 * </pre>
 */
public class CCodePhraseSerializer extends ConstraintSerializer<CCodePhrase> {
    public CCodePhraseSerializer(ArchetypeSerializer archetypeSerializer) {
        super(archetypeSerializer);
    }

    @Override
    public void serialize(CCodePhrase cobj) {
        builder.newIndentedline()
                .append("[" + cobj.getTerminologyId().getValue()).append("::")
                .newline();
        int n = 0;
        for (String code : cobj.getCodeList()) {
            n++;
            if (n >= cobj.getCodeList().size()) {
                builder.append(code);
            } else {
                builder.append(code).append(",").newline();
            }
        }
        builder.append("]").unindent();
    }

}
