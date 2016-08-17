package org.openehr.adl.serializer.constraints;

import org.openehr.adl.serializer.ArchetypeSerializer;
import org.openehr.jaxb.am.ArchetypeInternalRef;

/**
 * Created by bna on 27.01.2015.
 */
public class ArchetypeInternalRefSerializer extends ConstraintSerializer<ArchetypeInternalRef> {
    public ArchetypeInternalRefSerializer(ArchetypeSerializer archetypeSerializer) {
        super(archetypeSerializer);
    }

    @Override
    public void serialize(ArchetypeInternalRef cobj) {
        builder
                .newIndentedline()
                .append("use_node")
                .append(" ")
                .append(cobj.getRmTypeName())
                .append(" ")
                .append(cobj.getTargetPath())
                        //.lineComment("Should be comment here - not implemented")
                .unindent();
    }
}
