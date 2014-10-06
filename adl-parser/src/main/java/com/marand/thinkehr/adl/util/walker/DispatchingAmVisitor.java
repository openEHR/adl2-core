/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.util.walker;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.openehr.am.AmObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author markopi
 */
@SuppressWarnings("unchecked")
public class DispatchingAmVisitor<C extends AmVisitContext> implements AmVisitor<AmObject, C> {
    private final Multimap<Class<? extends AmObject>, AmVisitor> conf = LinkedListMultimap.create();
    private final Map<Class<? extends AmObject>, List<AmVisitor>> classToVisitorMap = new ConcurrentHashMap<>();

    public <T extends AmObject> DispatchingAmVisitor<C> add(Class<? extends T> cls, AmVisitor<T, C> visitor) {
        conf.put(cls, visitor);
        return this;
    }

    @Override
    public final ArchetypeWalker.Action<? extends AmObject> preorderVisit(AmObject item, C context) {
        List<AmVisitor> visitors = findVisitors(item);
        ArchetypeWalker.Action action = ArchetypeWalker.Action.next();
        for (AmVisitor visitor : visitors) {
            ArchetypeWalker.Action visitAction = visitor.preorderVisit(item, context);
            if (visitAction == null) visitAction = ArchetypeWalker.Action.next();
            action = action.mergeWith(visitAction);
        }
        return action;
    }

    @Override
    public ArchetypeWalker.Action<? extends AmObject> postorderVisit(AmObject item, C context,
            ArchetypeWalker.Action<? extends AmObject> action) {
        List<AmVisitor> visitors = findVisitors(item);
        for (AmVisitor visitor : visitors) {
            ArchetypeWalker.Action visitAction = visitor.postorderVisit(item, context, action);
            action = action.mergeWith(visitAction);
        }
        return action;
    }


    private List<AmVisitor> findVisitors(AmObject item) {
        List<AmVisitor> visitors = classToVisitorMap.get(item.getClass());
        if (visitors == null) {
            List<AmVisitorItem> items = new ArrayList<>();
            int confIndex = 0;
            for (Map.Entry<Class<? extends AmObject>, Collection<AmVisitor>> entry : conf.asMap().entrySet()) {
                if (entry.getKey().isAssignableFrom(item.getClass())) {
                    int iterIndex = 0;
                    for (AmVisitor amVisitor : entry.getValue()) {
                        AmVisitorItem vi = new AmVisitorItem(entry.getKey(), confIndex, iterIndex, amVisitor);
                        iterIndex++;
                        items.add(vi);
                    }
                }
                confIndex++;
            }
            Collections.sort(items);

            List<AmVisitor> visitorList = new ArrayList<>();
            for (AmVisitorItem amVisitorItem : items) {
                visitorList.add(amVisitorItem.visitor);
            }
            visitors = visitorList;
            classToVisitorMap.put(item.getClass(), visitors);
        }
        return visitors;
    }


    private static class AmVisitorItem implements Comparable<AmVisitorItem> {
        final Class cls;
        final int confIndex;
        final int iterIndex;
        final AmVisitor visitor;

        private AmVisitorItem(Class cls, int confIndex, int iterIndex, AmVisitor visitor) {
            this.cls = cls;
            this.confIndex = confIndex;
            this.iterIndex = iterIndex;
            this.visitor = visitor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AmVisitorItem that = (AmVisitorItem) o;

            if (confIndex != that.confIndex) return false;
            if (iterIndex != that.iterIndex) return false;
            if (!cls.equals(that.cls)) return false;
            if (!visitor.equals(that.visitor)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = cls.hashCode();
            result = 31 * result + confIndex;
            result = 31 * result + iterIndex;
            result = 31 * result + visitor.hashCode();
            return result;
        }

        @SuppressWarnings("CompareToUsesNonFinalVariable")
        @Override
        public int compareTo(AmVisitorItem o) {
            if (confIndex < o.confIndex) return -1;
            if (confIndex > o.confIndex) return 1;
            if (iterIndex < o.iterIndex) return -1;
            if (iterIndex > o.iterIndex) return 1;
            if (o.cls != cls) {
                if (cls.isAssignableFrom(o.cls)) return -1;
                if (o.cls.isAssignableFrom(cls)) return 1;
            }

            return 0;
        }
    }
}
