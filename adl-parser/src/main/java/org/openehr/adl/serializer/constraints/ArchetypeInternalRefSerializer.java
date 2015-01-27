package org.openehr.adl.serializer.constraints;

import org.openehr.adl.serializer.ArchetypeSerializer;
import org.openehr.adl.serializer.constraints.ConstraintSerializer;
import org.openehr.jaxb.am.CObject;

/**
 * Created by bna on 27.01.2015.
 */
public class ArchetypeInternalRefSerializer extends ConstraintSerializer {
    public ArchetypeInternalRefSerializer(ArchetypeSerializer archetypeSerializer) {
        super(archetypeSerializer);
    }

    @Override
    public void serialize(CObject cobj) {
        System.out.println("Not implemented serialize ArchetypeInternalRef --> nodeId=" + cobj.getNodeId());
    }
}
