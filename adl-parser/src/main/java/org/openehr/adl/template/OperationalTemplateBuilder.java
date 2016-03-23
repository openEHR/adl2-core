package org.openehr.adl.template;

import com.google.common.collect.Lists;
import org.openehr.adl.FlatArchetypeProvider;
import org.openehr.adl.am.AmQuery;
import org.openehr.adl.util.AdlUtils;
import org.openehr.jaxb.am.*;

import java.util.*;

/**
 * @author markopi
 */
public class OperationalTemplateBuilder {
    private final Template template;
    private final FlatArchetypeProvider flatArchetypeProvider;
    private final Set<String> presentTerminologies = new HashSet<>();
    private final OperationalTemplate result = new OperationalTemplate();


    private OperationalTemplateBuilder(FlatArchetypeProvider flatArchetypeProvider, Template template) {
        this.template = AdlUtils.makeClone(template);
        List<Archetype> overlays = Lists.newArrayList(template.getOverlays());
        overlays.add(template);

        this.flatArchetypeProvider = new FlatArchetypeProviderOverlay(flatArchetypeProvider, overlays);
    }


    private OperationalTemplate build() {
        AdlUtils.fillArchetypeFields(result, template);
        result.setIsDifferential(false);
        expandTemplate();

        return result;
    }


    private void expandTemplate() {
        Archetype a = flatArchetypeProvider.getFlatArchetype(result.getArchetypeId().getValue());
        presentTerminologies.add(a.getArchetypeId().getValue());

        ExpandContext context = new ExpandContext();
        context.archetypeStack.push(result);
        expandCObject(context, result.getDefinition());
        context.archetypeStack.pop();
    }

    private void expandCObject(ExpandContext context, CObject cobj) {
        if (cobj instanceof CArchetypeRoot) {
            CArchetypeRoot car = (CArchetypeRoot) cobj;
            Archetype a = flatArchetypeProvider.getFlatArchetype(((CArchetypeRoot) cobj).getArchetypeRef());
//            car.getAttributes().addAll(a.getDefinition().getAttributes());
            String aid = a.getArchetypeId().getValue();
            if (!presentTerminologies.contains(aid)) {
                result.getComponentTerminologies().add(newArchetypeTerminologyItem(aid, a.getTerminology()));
                presentTerminologies.add(aid);
            }
            context.archetypeStack.push(a);

            car.getAttributes().addAll(AdlUtils.makeClone((ArrayList<CAttribute>)
                    a.getDefinition().getAttributes()));
        }

        if (cobj instanceof CComplexObject) {
            expandCComplexObject(context, (CComplexObject) cobj);
        }


        if (cobj instanceof CArchetypeRoot) {
            context.archetypeStack.pop();
        }
    }

    private void expandCComplexObject(ExpandContext context, CComplexObject cobj) {
        for (Iterator<CAttribute> iterator = cobj.getAttributes().iterator(); iterator.hasNext(); ) {
            CAttribute attribute = iterator.next();
            if (isProhibited(attribute)) {
                iterator.remove();
            } else {
                expandCAttribute(context, attribute);
            }
        }
    }

    private void expandCAttribute(ExpandContext context, CAttribute attribute) {
        for (ListIterator<CObject> iterator = attribute.getChildren().listIterator(); iterator.hasNext(); ) {
            CObject cobj = iterator.next();
            if (isProhibited(cobj)) {
                iterator.remove();
            } else if (cobj instanceof ArchetypeSlot) {
                ArchetypeSlot as = (ArchetypeSlot) cobj;
                if (as.isIsClosed() != null && as.isIsClosed()) {
                    iterator.remove();
                } else {
                    expandCObject(context, cobj);
                }
            } else if (cobj instanceof ArchetypeInternalRef) {
                ArchetypeInternalRef iar = (ArchetypeInternalRef) cobj;
                CObject target = AmQuery.get(context.archetype().getDefinition(), iar.getTargetPath());
                CObject newCObject = AdlUtils.makeClone(target);
                iterator.set(newCObject);
                expandCObject(context, newCObject);
            } else {
                expandCObject(context, cobj);
            }
        }
    }

    private boolean isProhibited(CAttribute attribute) {
        return attribute.getExistence() != null
                && attribute.getExistence().getUpper() != null
                && attribute.getExistence().getUpper() == 0;
    }

    private boolean isProhibited(CObject cobj) {
        return cobj.getOccurrences() != null
                && cobj.getOccurrences().getUpper() != null
                && cobj.getOccurrences().getUpper() == 0;
    }

    private ArchetypeTerminologyItem newArchetypeTerminologyItem(String code, ArchetypeTerminology value) {
        ArchetypeTerminologyItem result = new ArchetypeTerminologyItem();
        result.setCode(code);
        result.setValue(value);
        return result;
    }

    public static OperationalTemplate build(FlatArchetypeProvider flatArchetypeProvider, Template template) {
        return new OperationalTemplateBuilder(flatArchetypeProvider, template).build();
    }


    private static class ExpandContext {
        final Deque<Archetype> archetypeStack = new ArrayDeque<>();

        Archetype archetype() {
            return archetypeStack.peek();
        }

    }

}
