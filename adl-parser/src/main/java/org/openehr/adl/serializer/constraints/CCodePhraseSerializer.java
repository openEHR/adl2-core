package org.openehr.adl.serializer.constraints;

import org.openehr.adl.serializer.ArchetypeSerializer;
import org.openehr.adl.serializer.constraints.ConstraintSerializer;
import org.openehr.jaxb.am.CCodePhrase;

/**
 * Created by bna on 27.01.2015.
 */
public class CCodePhraseSerializer extends ConstraintSerializer<CCodePhrase> {
    public CCodePhraseSerializer(ArchetypeSerializer archetypeSerializer) {
        super(archetypeSerializer);
    }

    @Override
    public void serialize(CCodePhrase cobj) {
        System.err.println(" CCodePhrase Not implemented at ALL");
        builder.newIndentedline();
    }
}
