package org.openehr.adl.serializer.constraints;

import org.openehr.adl.serializer.ArchetypeSerializeUtils;
import org.openehr.adl.serializer.ArchetypeSerializer;
import org.openehr.jaxb.am.ArchetypeSlot;
import org.openehr.jaxb.am.Assertion;

/**
 * Created by bna on 27.01.2015.
 * <p/>
 * allow_archetype CLUSTER[at1030] occurrences matches {0..1} matches {	-- Exertion
 * include
 * archetype_id/value matches {/openEHR-EHR-CLUSTER\.level_of_exertion(-[a-zA-Z0-9_]+)*\.v1/}
 * }
 */
public class ArchetypeSlotSerializer extends ConstraintSerializer<ArchetypeSlot> {

    public ArchetypeSlotSerializer(ArchetypeSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(ArchetypeSlot cobj) {
        builder.newline()
                .append("allow_archetype")
                .append(" ")
                .append(cobj.getRmTypeName()).append("[").append(cobj.getNodeId()).append("]")

                .append(" occurrences matches {");
        ArchetypeSerializeUtils.buildOccurrences(builder, cobj.getOccurrences());
        builder
                .append("} matches { ")
                ;
        if (cobj.getIncludes() != null && cobj.getIncludes().size() > 0) {
            builder
                    .newIndentedline()
                    .append(" include ");
            for (Assertion a : cobj.getIncludes()) {
                builder
                        .newline()
                        .append(a.getStringExpression())
                        .append(" ")
                ;
            }
            builder.unindent().newline();
        }
        if (cobj.getExcludes() != null && cobj.getExcludes().size() > 0) {
            builder
                    .newIndentedline()
                    .append(" exclude ");
            for (Assertion a : cobj.getExcludes()) {
                builder
                        .newline()
                        .append(a.getStringExpression()).append(" ");
            }
        }

        builder.append("}");

    }
}
