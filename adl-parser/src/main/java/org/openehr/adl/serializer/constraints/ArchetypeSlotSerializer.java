package org.openehr.adl.serializer.constraints;

import org.openehr.adl.serializer.ArchetypeSerializer;
import org.openehr.adl.serializer.constraints.ConstraintSerializer;
import org.openehr.jaxb.am.ArchetypeSlot;

/**
 * Created by bna on 27.01.2015.
 */
public class ArchetypeSlotSerializer extends ConstraintSerializer<ArchetypeSlot> {

    public ArchetypeSlotSerializer(ArchetypeSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(ArchetypeSlot cobj) {
System.err.println("Not implemented serialize ArchetypeSlot" + cobj.getNodeId());
    }
}
