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
import org.openehr.jaxb.rm.ResourceAnnotationNodeItems;
import org.openehr.jaxb.rm.ResourceAnnotationNodes;
import org.openehr.jaxb.rm.ResourceAnnotations;
import org.openehr.jaxb.rm.StringDictionaryItem;

import javax.annotation.Nullable;
import java.util.List;

import static org.openehr.adl.util.AdlUtils.makeClone;

/**
 * @author markopi
 */
public class AnnotationsMerger {
    private final Function<ResourceAnnotationNodes, String> ANNOTATION_SET_KEY_EXTRACTOR = new Function<ResourceAnnotationNodes, String>() {
        @Override
        public String apply(ResourceAnnotationNodes input) {
            return input.getLanguage();
        }
    };
    private final Function<ResourceAnnotationNodeItems, String> ANNOTATION_KEY_EXTRACTOR = new Function<ResourceAnnotationNodeItems, String>() {
        @Override
        public String apply(ResourceAnnotationNodeItems input) {
            return input.getPath();
        }
    };
    private final Function<StringDictionaryItem, String> STRING_DICTIONARY_ITEM_KEY_EXTRACTOR = new Function<StringDictionaryItem, String>() {
        @Override
        public String apply(StringDictionaryItem input) {
            return input.getId();
        }
    };

    public void merge(List<ResourceAnnotationNodes> parent, List<ResourceAnnotationNodes> target) {
        mergeAnnotationSets(target, parent);
    }

    private void mergeAnnotationSets(List<ResourceAnnotationNodes> target, List<ResourceAnnotationNodes> parent) {
        for (ResourceAnnotationNodes parentItem : parent) {
            ResourceAnnotationNodes existing = find(target, parentItem.getLanguage(), ANNOTATION_SET_KEY_EXTRACTOR);
            if (existing != null) {
                mergeAnnotations(existing.getItems(), parentItem.getItems());
            } else {
                target.add(makeClone(parentItem));
            }
        }
    }

    private void mergeAnnotations(List<ResourceAnnotationNodeItems> target, List<ResourceAnnotationNodeItems> parent) {
        for (ResourceAnnotationNodeItems parentItem : parent) {
            ResourceAnnotationNodeItems existing = find(target, parentItem.getPath(), ANNOTATION_KEY_EXTRACTOR);
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
