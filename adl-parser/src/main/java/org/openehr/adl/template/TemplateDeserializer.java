package org.openehr.adl.template;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.CharStreams;
import org.openehr.adl.parser.AdlDeserializer;
import org.openehr.adl.parser.BomSupportingReader;
import org.openehr.adl.util.AdlUtils;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.Template;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author markopi
 */
public class TemplateDeserializer {
    public static Template deserialize(InputStream adltStream) throws IOException {
        try (Reader r = new BomSupportingReader(adltStream, Charsets.UTF_8)) {
            String adltContents = CharStreams.toString(r);
            return deserialize(adltContents);
        }

    }

    public static Template deserialize(String adltContent) {
        Iterable<String> adls = Splitter.on(Pattern.compile("(\r|\n)+ *\\-{2,} *(\r|\n)+")).split(adltContent);

        List<Archetype> result = new ArrayList<>();
        AdlDeserializer deserializer = new AdlDeserializer();
        for (String adl : adls) {
            result.add(deserializer.parse(adl));
        }
        return AdlUtils.buildTemplate(result);
    }
}