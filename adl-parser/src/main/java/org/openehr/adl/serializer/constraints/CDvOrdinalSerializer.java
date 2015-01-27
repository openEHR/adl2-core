package org.openehr.adl.serializer.constraints;

import org.openehr.adl.serializer.ArchetypeSerializer;
import org.openehr.jaxb.am.CDvOrdinal;
import org.openehr.jaxb.rm.DvOrdinal;

import java.util.List;

/**
 * Created by bna on 27.01.2015.
 * ELEMENT[at0015] occurrences matches {0..1} matches {	-- Ordinal
 * value matches {
 * 0|[local::at0038], 	-- No pain
 * 1|[local::at0039], 	-- Slight pain
 * 2|[local::at0040], 	-- Mild pain
 * 5|[local::at0041], 	-- Moderate pain
 * 9|[local::at0042], 	-- Severe pain
 * 10|[local::at0043]  	-- Most severe pain imaginable
 * }
 * }
 */
public class CDvOrdinalSerializer extends ConstraintSerializer<CDvOrdinal> {
    public CDvOrdinalSerializer(ArchetypeSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(CDvOrdinal cobj) {
        System.out.println("Serialize CdvOrdinal");
        List<DvOrdinal> ordinalList = cobj.getList();
        int i = 0;
        for (DvOrdinal o : ordinalList) {
            builder.append(o.getValue()).append("|")
                    .append("[")
                    .append(o.getSymbol().getDefiningCode().getTerminologyId().getValue())
                    .append("::")
                    .append(o.getSymbol().getDefiningCode().getCodeString())
                    .append("]");
            if (i < ordinalList.size()) {
                builder.append(",");
            }
            builder.newline();
        }
    }
}
