package org.openehr.adl.serializer.constraints;

import org.openehr.adl.serializer.ArchetypeSerializer;
import org.openehr.jaxb.am.CDvQuantity;
import org.openehr.jaxb.am.CQuantityItem;
import org.openehr.jaxb.rm.DvQuantity;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 * <pre>
 *
 * </pre>
 * Created by bna on 27.01.2015.
 */
public class CDvQuantitySerializer extends ConstraintSerializer<CDvQuantity> {
    public CDvQuantitySerializer(ArchetypeSerializer archetypeSerializer) {
        super(archetypeSerializer);
    }

    @Override
    public void serialize(CDvQuantity cobj) {
        builder
                .newIndentedline()
                .append("C_DV_QUANTITY <")
                .newIndentedline()
                .append("property = <[")
                .append(cobj.getProperty().getTerminologyId().getValue())
                .append("::")
                .append(cobj.getProperty().getCodeString())
                .append("]>")
                .newline();

        builder.tryNewLine().append("list = <").newIndentedline();
        int n = 0;
        for (CQuantityItem item : cobj.getList()) {
            n++;
            builder.append("[\"" + n + "\"] = <")
                    .newIndentedline();
            if (item.getUnits() != null) {
                builder.append("units = <\"").append(item.getUnits()).append("\">");
                builder.newline();
            }
            if (item.getMagnitude() != null) {
                builder.append("magnitude").append(" = ")
                        .append("<|").append(firstNonNull(item.getMagnitude().getLower(), "0"))
                        .append("..").append(firstNonNull(item.getMagnitude().getUpper(), "*"))
                        .append("|>");
                builder.newline();
            }
            if (item.getPrecision() != null) {
                builder.append("precision = ")
                        .append("<|").append(firstNonNull(item.getPrecision().getUpper(), "0")).append("|>");

                builder.newline();
            }
            builder.unindent()
                    .append(">")
                    .newline()
            ;


        }
        builder
                .tryNewLine()
                .append(">")
                .unindent()
                .unindent();
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
        .newline()
            ;
        }
        builder.append(">");
        builder.unindent();
    }
}
