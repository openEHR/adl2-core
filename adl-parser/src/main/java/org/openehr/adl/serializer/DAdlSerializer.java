/*
 * ADL2-core
 * Copyright (c) 2013-2014 Marand d.o.o. (www.marand.com)
 *
 * This file is part of ADL2-core.
 *
 * ADL2-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openehr.adl.serializer;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.*;

import javax.annotation.Nonnull;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Marko Pipan
 */
public class DAdlSerializer {
    private final AdlStringBuilder builder;

    public DAdlSerializer(AdlStringBuilder builder) {
        this.builder = builder;
    }

    private static String getAttributeForField(@Nonnull String fieldName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
    }

    public void serialize(Object obj) {
        builder.append("<");
        serializePlain(obj);
        builder.append(">");
    }

    public void serializePlain(Object obj) {
        if (obj == null) {

        } else if (obj instanceof String) {
            builder.text((String) obj);
        } else if (obj instanceof List) {
            serializeListMap((List) obj);
        } else if (obj instanceof CodePhrase) {
            serializeCodePhrase((CodePhrase) obj);
        } else {
            serializeBean(obj);
        }
    }

    public void serializeBean(Object obj) {
        serializeBean(obj, ImmutableSet.<String>of());
    }

    /**
     * Serializes bean, without wrapping it with &lt;/&gt;
     *
     * @param obj                bean to serialize
     * @param attributesToIgnore attributes that are not written to dadl
     */
    public void serializeBean(Object obj, Set<String> attributesToIgnore) {
        builder.newIndentedline();
        try {
            BeanInfo info = Introspector.getBeanInfo(obj.getClass());
            List<NameValue> values = new ArrayList<>();

            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (pd.getName().equals("class")) continue;
                Object value = pd.getReadMethod().invoke(obj);
                if (value == null) continue;
                if (value instanceof List && ((List) value).isEmpty()) continue;
                String attribute = getAttributeForField(pd.getName());
                if (!attributesToIgnore.contains(attribute)) {
                    values.add(new NameValue(attribute, value, value instanceof List && isPlainType(((List) value).get(0))));
                }
            }

            for (int i = 0; i < values.size(); i++) {
                NameValue value = values.get(i);
                builder.append(value.name).append(" = ");
                if (value.plain) {
                    serializePlain(value.value);
                } else {
                    serialize(value.value);
                }
                if (i < values.size() - 1) {
                    builder.newline();
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        builder.unindent().newline();
    }

    private void serializeCodePhrase(CodePhrase cp) {
        builder.append("[").append(cp.getTerminologyId().getValue()).append("::").append(cp.getCodeString()).append("]");
    }

    private void serializeListMap(List list) {
        if (!list.isEmpty()) {
            Object o = list.get(0);
            if (isPlainType(o)) {
                builder.append("<");
                for (int i = 0; i < list.size(); i++) {
                    Object item = list.get(i);
                    serializePlain(item);
                    if (i < list.size() - 1) {
                        builder.append(", ");
                    }
                }
                builder.append(">");
                return;
            }
        }


        builder.newIndentedline();
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            serializeItem(item);
            if (i < list.size() - 1) {
                builder.newline();
            }
        }
        builder.unindent().newline();
    }

    private boolean isPlainType(Object o) {
        return o instanceof String || o instanceof CodePhrase;
    }

    private void serializeItem(Object item) {
     //   System.out.println("serializeItem:" + item);
        if (item instanceof TranslationDetails) {
            TranslationDetails td = (TranslationDetails) item;
            serializeKey(td.getLanguage().getCodeString());
            serialize(item);
        } else if (item instanceof StringDictionaryItem) {
            StringDictionaryItem sdi = (StringDictionaryItem) item;
            serializeKey(sdi.getId());
            serialize(sdi.getValue());
        } else if (item instanceof ResourceDescriptionItem) {
            ResourceDescriptionItem rdi = (ResourceDescriptionItem) item;
            serializeKey(rdi.getLanguage().getCodeString());
            serialize(rdi);
        } else if (item instanceof CodeDefinitionSet) {
            CodeDefinitionSet cds = (CodeDefinitionSet) item;
            serializeKey(cds.getLanguage());
            builder.append("<").newIndentedline().append("items =  ");
            serialize(cds.getItems());
            builder.unindent().newline().append(">");
            //builder.newline().append(">");
        } else if (item instanceof ArchetypeTerm) {
            ArchetypeTerm at = (ArchetypeTerm) item;
            serializeKey(at.getCode());
            List<StringDictionaryItem> items = at.getItems();
            builder.append("<").indent();
            for (StringDictionaryItem s : items) {
                builder.newline().append(s.getId()).append(" = ").dadl(s.getValue());
            }
            builder.newline().unindent().append(">");
        } else if (item instanceof ValueSetItem) {
            ValueSetItem vsi = (ValueSetItem) item;
            serializeKey(vsi.getId());
            serialize(item);
        } else if (item instanceof ResourceAnnotationNodes) {
            ResourceAnnotationNodes vsi = (ResourceAnnotationNodes) item;
            serializeKey(vsi.getLanguage());
            builder.append("<");
            serializeBean(vsi, ImmutableSet.of("language"));
            builder.append(">");
        } else if (item instanceof ResourceAnnotationNodeItems) {
            ResourceAnnotationNodeItems vsi = (ResourceAnnotationNodeItems) item;
            serializeKey(vsi.getPath());
            builder.append("<");
            serializeBean(vsi, ImmutableSet.of("path"));
            builder.append(">");
        } else if (item instanceof String) {
            serializePlain(item);
        } else if (item instanceof ConstraintBindingSet) {
            ConstraintBindingSet set = (ConstraintBindingSet) item;
            serializeKey(set.getTerminology());
            builder.append("<").newIndentedline().append("items = <");
            serializeListMap(set.getItems());
            builder.append(">");
            builder.unindent().newline().append(">");
        } else if (item instanceof ConstraintBindingItem) {
            ConstraintBindingItem c = (ConstraintBindingItem) item;
            serializeKey(c.getCode());
            //serializeItem(c.getValue());
            builder.append("<").append(c.getValue()).append(">");
        } else if (item instanceof TermBindingSet) {
            TermBindingSet t = (TermBindingSet) item;
            serializeKey(t.getTerminology());
            builder.append("<").newIndentedline().append("items = <");
            serializeListMap(t.getItems());
            builder.append(">");
            builder.unindent().newline().append(">");
        } else if (item instanceof TermBindingItem) {
            TermBindingItem t = (TermBindingItem) item;
            serializeKey(t.getCode());
            serialize(t.getValue());
        } else {
            throw new IllegalArgumentException(item.getClass().getName());
        }
    }

    private void serializeKey(String key) {
        builder.append("[").text(key).append("] = ");
    }

    private static class NameValue {
        final String name;
        final Object value;
        final boolean plain;

        private NameValue(String name, Object value, boolean plain) {
            this.name = name;
            this.value = value;
            this.plain = plain;
        }
    }
}
