package org.openehr.adl.serializer.constraints;

import org.openehr.adl.serializer.ArchetypeSerializer;
import org.openehr.jaxb.am.CDvQuantity;
import org.openehr.jaxb.am.CQuantityItem;
import org.openehr.jaxb.rm.DvQuantity;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 * Created by bna on 27.01.2015.
 */
public class CDvQuantitySerializer extends ConstraintSerializer<CDvQuantity> {
    public CDvQuantitySerializer(ArchetypeSerializer archetypeSerializer) {
        super(archetypeSerializer);
    }

    /**
     * C_DV_QUANTITY <
     * property = <[openehr::125]>
     * list = <
     * ["1"] = <
     * units = <"mm[Hg]">
     * magnitude = <|0.0..<1000.0|>
     * precision = <|0|>
     * >
     * >
     * >
     *
     * @param cobj
     */
    @Override
    public void serialize(CDvQuantity cobj) {
        System.err.println("CDvQuantity  not implemented atr ALL!");
        builder.newIndentedline()
                .indent()

                .append("property = <[").append(cobj.getProperty().getTerminologyId().getValue()).append("::").append(cobj.getProperty().getCodeString()).append("]>")
                .newIndentedline();

        builder.tryNewLine().append("list = <")
                .newIndentedline();
        int n = 0;
        for (CQuantityItem item : cobj.getList()) {
            n++;
            builder.append("[\"" + n + "\"] = <")
                    .newIndentedline()
                    .append("units = <\"" + item.getUnits() + "\">")
                    .tryNewLine();
            if (item.getMagnitude() != null) {
                builder.append("magnitude").append(" = ")
                        .append("<|").append(firstNonNull(item.getMagnitude().getLower(), "0"))
                        .append("..<").append(firstNonNull(item.getMagnitude().getUpper(), "*"))
                        .append("|>");
                builder.newline();
            }
            if (item.getPrecision() != null) {
                builder.append("precision = ")
                        .append("<|").append(firstNonNull(item.getPrecision().getUpper(), "0")).append("|>")

                        .newline();
            }
            builder.unindent()
                    .append(">")
                    .newline()
            ;


        }
        builder
                .tryNewLine()
                .append(">");
        builder.unindent();

        /**
         *
         assumed_value = <
         magnitude = <4.0>
         units = <"km">
         precision = <2>
         >

         */
        DvQuantity assumedValue = cobj.getAssumedValue();
        if (assumedValue != null) {

            builder.newline()
                    .append("assumed_value = <")
                    .newIndentedline()
                    .append("magnitude = <").append(assumedValue.getMagnitude()).append(">")
                    .newline()
                    .append("units = <\"").append(assumedValue.getUnits()).append("\">")
                    .newline()
                    .append("precision = <").append(assumedValue.getPrecision()).append(">")
                    .newline()
                    .unindent()
                    .append(">")
                    .newline();
        }
    }
}
