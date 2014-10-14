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

package org.openehr.adl.flattener;

import com.google.common.base.Function;
import org.openehr.jaxb.rm.Annotation;
import org.openehr.jaxb.rm.AnnotationSet;
import org.openehr.jaxb.rm.StringDictionaryItem;

import javax.annotation.Nullable;
import java.util.List;

import static org.openehr.adl.util.AdlUtils.makeClone;

/**
 * @author markopi
 */
public class AnnotationsMerger {
    private final Function<AnnotationSet, String> ANNOTATION_SET_KEY_EXTRACTOR = new Function<AnnotationSet, String>() {
        @Override
        public String apply(AnnotationSet input) {
            return input.getLanguage();
        }
    };
    private final Function<Annotation, String> ANNOTATION_KEY_EXTRACTOR = new Function<Annotation, String>() {
        @Override
        public String apply(Annotation input) {
            return input.getPath();
        }
    };
    private final Function<StringDictionaryItem, String> STRING_DICTIONARY_ITEM_KEY_EXTRACTOR = new Function<StringDictionaryItem, String>() {
        @Override
        public String apply(StringDictionaryItem input) {
            return input.getId();
        }
    };

    public void merge(List<AnnotationSet> parent, List<AnnotationSet> target) {
        mergeAnnotationSets(target, parent);
    }

    private void mergeAnnotationSets(List<AnnotationSet> target, List<AnnotationSet> parent) {
        for (AnnotationSet parentItem : parent) {
            AnnotationSet existing = find(target, parentItem.getLanguage(), ANNOTATION_SET_KEY_EXTRACTOR);
            if (existing != null) {
                mergeAnnotations(existing.getItems(), parentItem.getItems());
            } else {
                target.add(makeClone(parentItem));
            }
        }
    }

    private void mergeAnnotations(List<Annotation> target, List<Annotation> parent) {
        for (Annotation parentItem : parent) {
            Annotation existing = find(target, parentItem.getPath(), ANNOTATION_KEY_EXTRACTOR);
            if (existing != null) {
                mergeStringDictionaryItems(existing.getItems(), parentItem.getItems());
            } else {
                target.add(makeClone(parentItem));
            }
        }
    }

    private void mergeStringDictionaryItems(List<StringDictionaryItem> target, List<StringDictionaryItem> parent) {
        for (StringDictionaryItem parentItem : parent) {
            StringDictionaryItem existing = find(target, parentItem.getId(), STRING_DICTIONARY_ITEM_KEY_EXTRACTOR);
            if (existing == null) {
                target.add(makeClone(parentItem));
            }
        }
    }


    @Nullable
    private <T> T find(List<T> items, String key, Function<T, String> keyExtractor) {
        for (T item : items) {
            if (key.equals(keyExtractor.apply(item))) {
                return item;
            }
        }
        return null;
    }


}
