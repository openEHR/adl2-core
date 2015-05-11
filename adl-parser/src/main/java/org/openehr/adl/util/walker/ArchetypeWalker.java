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

package org.openehr.adl.util.walker;

import org.openehr.am.AmObject;
import org.openehr.jaxb.am.*;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author markopi
 */
public class ArchetypeWalker<C extends AmVisitContext> {
    private final AmVisitor<AmObject, AmVisitContext> visitor;
    private final C context;

    private ArchetypeWalker(AmVisitor<AmObject, C> visitor, C context) {
        //noinspection unchecked
        this.visitor = (AmVisitor) visitor;
        this.context = context;
    }

    private Action<?> walk(AmObject cobj) {

        Action<?> action = visitor.preorderVisit(cobj, context);
        if (action == null) action = Action.next();

        final AmObject walkObj = extractWalkObject(cobj, action);
        if (walkObj != null) {
            // walk children
            UpdatingIterator it = getChildrenOf(walkObj);
            context.getAmParents().addLast(walkObj);
            while (it.hasNext()) {
                AmObject child = it.next();
                Action<?> childAction = walk(child);
                processAction(childAction, it);
            }
            context.getAmParents().removeLast();
        }

        Action<?> postAction = visitor.postorderVisit(walkObj != null ? walkObj : cobj, context, action);
        if (postAction == null) postAction = Action.next;

        action = action.mergeWith(postAction);
        return action;
    }

    @Nullable
    private AmObject extractWalkObject(AmObject amObject, Action<?> result) {
        if (result.act == Action.Act.next) {
            return amObject;
        } else if (result.act == Action.Act.nextSibling) {
            return null;
        } else if (result.act == Action.Act.remove) {
            return null;
        } else if (result.act == Action.Act.replace) {
            return result.getReplaceWith();
        } else throw new AssertionError(result);
    }


    @SuppressWarnings("unchecked")
    private UpdatingIterator getChildrenOf(AmObject parent) {

        if (parent instanceof CComplexObject) {
            return new UpdatingIterator(((CComplexObject) parent).getAttributes());
        } else if (parent instanceof CAttribute) {
            return new UpdatingIterator(((CAttribute) parent).getChildren());
        } else if (parent instanceof ArchetypeTerminology) {
            ArchetypeTerminology ontology = (ArchetypeTerminology) parent;
            return new UpdatingIterator(
                    ontology.getConstraintBindings(),
                    ontology.getConstraintDefinitions(),
                    ontology.getTermBindings(),
                    ontology.getTermDefinitions(),
                    ontology.getTerminologyExtracts());
        } else if (parent instanceof CodeDefinitionSet) {
            return new UpdatingIterator(((CodeDefinitionSet) parent).getItems());
        } else if (parent instanceof TermBindingSet) {
            return new UpdatingIterator(((TermBindingSet) parent).getItems());
        } else if (parent instanceof ConstraintBindingSet) {
            return new UpdatingIterator(((ConstraintBindingSet) parent).getItems());
        } else {
            return new UpdatingIterator(Collections.<AmObject>emptyList());
        }
    }

    private void processAction(Action<?> action, UpdatingIterator parentIterator) {
        if (action.act == Action.Act.next || action.act == Action.Act.nextSibling) {
            // do nothing
        } else if (action.act == Action.Act.remove) {
            parentIterator.remove();
        } else if (action.act == Action.Act.replace) {
            AmObject replacement = action.getReplaceWith();
            parentIterator.set(replacement);
        } else throw new AssertionError(action);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AmObject, C extends AmConstraintContext> void walkConstraints(AmVisitor<T, C> visitor, Archetype archetype,
            C context) {
        if (!(visitor instanceof ConstraintAmVisitor<?>)) {
            visitor = new ConstraintAmVisitor<>().add(AmObject.class, (AmVisitor) visitor);
        }

        ArchetypeWalker<C> walker = new ArchetypeWalker<>((AmVisitor<AmObject, C>) visitor, context);
        Action<?> action = walker.walk(archetype.getDefinition());
        if (action.act != Action.Act.next) {
            throw new AssertionError("Cannot remove or replace root constraint");
        }
    }

    public static <C extends AmVisitContext> void walkTerminology(AmVisitor<AmObject, C> visitor, ArchetypeTerminology terminology, C context) {
        ArchetypeWalker<C> walker = new ArchetypeWalker<>(visitor, context);
        Action<?> action = walker.walk(terminology);
        if (action.act != Action.Act.next) {
            throw new AssertionError("Cannot remove or replace root ontology object");
        }
    }

    @SuppressWarnings({"ClassReferencesSubclass", "unchecked"})
    public static final class Action<T extends AmObject> {
        private static final Action<AmObject> remove = new Action<>(Act.remove);
        private static final Action<AmObject> next = new Action<>(Act.next);
        private static final Action<AmObject> nextSibling = new Action<>(Act.nextSibling);

        private final Act act;
        private T replaceWith;

        private Action(Act act) {
            this.act = act;
        }

        public T getReplaceWith() {
            return replaceWith;
        }


        @Override
        public String toString() {
            return act.toString();
        }

        public Action<?> mergeWith(Action<?> other) {
            Action first, second;
            if (this.act.ordinal() <= other.act.ordinal()) {
                first = this;
                second = other;
            } else {
                first = other;
                second = this;
            }
            if (first.act == Act.next) return second;
            if (first.act == Act.nextSibling) return second;
            if (first.act == Act.remove && second.act == Act.remove) return first;

            throw new IllegalStateException(
                    String.format("Conflicting actions: %s and %s", this, other));
        }


        public static <T extends AmObject> Action<T> remove() {
            return (Action) remove;
        }

        public static <T extends AmObject> Action<T> replaceWith(T replacement) {
            Action<T> result = new Action<>(Act.replace);
            result.replaceWith = replacement;
            return result;
        }

        public static <T extends AmObject> Action<T> next() {
            return (Action) next;
        }

        public static <T extends AmObject> Action<T> nextSibling() {
            return (Action) nextSibling;
        }


        public static enum Act {
            /**
             * continue depth-first with the current item's child, if any, otherwise continue with current items's sibling
             * compatible with all.
             */
            next,
            /**
             * skip children of this node, and continue immediately with its siblings
             * compatible with all. overrides next
             */
            nextSibling,
            /**
             * removes current node and continue with its sibling.
             * compatible with next,nextSibling,remove. overrides next,nextSibling
             */
            remove,
            /**
             * replaces current node with provided node, and continue with the new nodes children
             * compatible with next,nextSibling. overrides next,nextSibling
             */
            replace
        }
    }

    private static class UpdatingIterator implements Iterator<AmObject> {
        private final Iterator<List<AmObject>> iterators;
        private ListIterator<AmObject> currentIterator;

        @SuppressWarnings("unchecked")
        @SafeVarargs
        public UpdatingIterator(List<? extends AmObject> first, List<? extends AmObject>... other) {
            List<List<AmObject>> lists = new ArrayList<>();
            lists.add((List) first);
            for (List<? extends AmObject> ts : other) {
                lists.add((List) ts);
            }
            this.iterators = lists.iterator();
            this.currentIterator = this.iterators.next().listIterator();
        }

        private void updateIterator() {
            while (!currentIterator.hasNext() && iterators.hasNext()) {
                currentIterator = iterators.next().listIterator();
            }
        }

        @Override
        public boolean hasNext() {
            updateIterator();
            return currentIterator.hasNext();
        }

        @Override
        public AmObject next() {
            updateIterator();
            return currentIterator.next();
        }

        @Override
        public void remove() {
            currentIterator.remove();
        }

        public void set(AmObject newValue) {
            currentIterator.set(newValue);

        }
    }

}
