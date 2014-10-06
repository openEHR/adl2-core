/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.rm;

import org.openehr.jaxb.rm.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.marand.thinkehr.adl.util.AdlUtils.doubleToFloat;
import static com.marand.thinkehr.adl.util.AdlUtils.emptyIfNull;

/**
 * @author markopi
 * @since 16.7.2013
 */
public class RmObjectFactory {
    public static HierObjectId newHierObjectId(String value) {
        HierObjectId result = new HierObjectId();
        result.setValue(value);
        return result;
    }

    public static ArchetypeId newArchetypeId(String value) {
        ArchetypeId result = new ArchetypeId();
        result.setValue(value);
        return result;
    }

    public static CodePhrase newCodePhrase(TerminologyId terminologyId, String code) {
        CodePhrase result = new CodePhrase();
        result.setTerminologyId(terminologyId);
        result.setCodeString(code);
        return result;
    }

    public static TerminologyId newTerminologyId(String value) {
        TerminologyId result = new TerminologyId();
        result.setValue(value);
        return result;
    }

    public static TranslationDetails newTranslationDetails(CodePhrase language, Map<String, String> author, String accrediation,
            Map<String, String> otherDetails) {
        TranslationDetails result = new TranslationDetails();
        result.setLanguage(language);
        addStringDictionaryItems(result.getAuthor(), author);
        addStringDictionaryItems(result.getOtherDetails(), otherDetails);
        result.setAccreditation(accrediation);
        return result;
    }

    private static void addStringDictionaryItems(List<StringDictionaryItem> target, Map<String, String> source) {
        if (source == null) return;
        for (Map.Entry<String, String> entry : source.entrySet()) {
            target.add(newStringDictionaryItem(entry.getKey(), entry.getValue()));
        }
    }

    public static StringDictionaryItem newStringDictionaryItem(String id, String value) {
        StringDictionaryItem result = new StringDictionaryItem();
        result.setId(id);
        result.setValue(value);
        return result;
    }

    public static ResourceDescription newResourceDescription(Map<String, String> originalAuthor, List<String> otherContributors,
            String lifecycleState, List<ResourceDescriptionItem> details, String resourcePackageURI, Map<String, String> otherDetails,
            AuthoredResource parent) {
        ResourceDescription result = new ResourceDescription();
        addStringDictionaryItems(result.getOriginalAuthor(), originalAuthor);
        result.getOtherContributors().addAll(emptyIfNull(otherContributors));
        result.setLifecycleState(lifecycleState);
        result.getDetails().addAll(emptyIfNull(details));
        result.setResourcePackageUri(resourcePackageURI);
        addStringDictionaryItems(result.getOtherDetails(), otherDetails);
        result.setParentResource(parent);
        return result;
    }


    public static ResourceDescriptionItem newResourceDescriptionItem(CodePhrase lang, String purpose, List<String> keywords,
            String use, String misuse, String copyright, Map<String, String> originalResourceURI, Map<String, String> otherDetails) {
        ResourceDescriptionItem result = new ResourceDescriptionItem();
        result.setLanguage(lang);
        result.setPurpose(purpose);
        result.getKeywords().addAll(emptyIfNull(keywords));
        result.setUse(use);
        result.setMisuse(misuse);
        result.setCopyright(copyright);
        addStringDictionaryItems(result.getOriginalResourceUri(), originalResourceURI);
        addStringDictionaryItems(result.getOtherDetails(), otherDetails);
        return result;
    }


    public static IntervalOfInteger newIntervalOfInteger(@Nullable Integer lower, @Nullable Integer upper, boolean lowerIncluded,
            boolean upperIncluded) {
        IntervalOfInteger result = new IntervalOfInteger();
        result.setLower(lower);
        result.setUpper(upper);
        result.setLowerUnbounded(lower == null);
        result.setUpperUnbounded(upper == null);
        result.setLowerIncluded(lowerIncluded);
        result.setUpperIncluded(upperIncluded);
        return result;
    }
    public static IntervalOfInteger newIntervalOfInteger(@Nullable Integer lower, @Nullable Integer upper) {
        return newIntervalOfInteger(lower, upper, lower != null, upper != null);
    }

    public static MultiplicityInterval newMultiplicityInterval(@Nullable Integer lower, @Nullable Integer upper, boolean lowerIncluded,
            boolean upperIncluded) {
        MultiplicityInterval result = new MultiplicityInterval();
        result.setLower(lower);
        result.setUpper(upper);
        result.setLowerUnbounded(lower == null);
        result.setUpperUnbounded(upper == null);
        result.setLowerIncluded(lowerIncluded);
        result.setUpperIncluded(upperIncluded);
        return result;
    }

    public static MultiplicityInterval newMultiplicityInterval(@Nullable Integer lower, @Nullable Integer upper) {
        return newMultiplicityInterval(lower, upper, lower != null, upper != null);
    }



    public static IntervalOfReal newIntervalOfReal(@Nullable Double lower, @Nullable Double upper) {
        return newIntervalOfReal(lower, upper, lower != null, upper != null);
    }

    public static IntervalOfReal newIntervalOfReal(@Nullable Double lower, @Nullable Double upper, boolean lowerIncluded,
            boolean upperIncluded) {
        return newIntervalOfReal(doubleToFloat(lower), doubleToFloat(upper), lowerIncluded, upperIncluded);
    }


    public static IntervalOfReal newIntervalOfReal(@Nullable Float lower, @Nullable Float upper, boolean lowerIncluded,
            boolean upperIncluded) {
        IntervalOfReal result = new IntervalOfReal();
        result.setLower(lower);
        result.setUpper(upper);
        result.setLowerUnbounded(lower == null);
        result.setUpperUnbounded(upper == null);
        result.setLowerIncluded(lowerIncluded);
        result.setUpperIncluded(upperIncluded);
        return result;
    }

    public static DvDate newDvDate(String value) {
        DvDate result = new DvDate();
        result.setValue(value);
        return result;
    }

    public static IntervalOfDate newIntervalOfDate(@Nullable String lower, @Nullable String upper) {
        return newIntervalOfDate(lower, upper, lower != null, upper != null);
    }

    public static IntervalOfDate newIntervalOfDate(@Nullable String lower, @Nullable String upper, boolean lowerIncluded,
            boolean upperIncluded) {
        IntervalOfDate result = new IntervalOfDate();
        result.setLower(lower);
        result.setUpper(upper);
        result.setLowerUnbounded(lower == null);
        result.setUpperUnbounded(upper == null);
        result.setLowerIncluded(lowerIncluded);
        result.setUpperIncluded(upperIncluded);
        return result;
    }

    public static IntervalOfDate newIntervalOfDate(@Nullable DvDate lower, @Nullable DvDate upper, boolean lowerIncluded,
            boolean upperIncluded) {
        return newIntervalOfDate(lower != null ? lower.getValue() : null, upper != null ? upper.getValue() : null, lowerIncluded,
                upperIncluded);
    }

    public static DvTime newDvTime(String value) {
        DvTime result = new DvTime();
        result.setValue(value);
        return result;
    }

    public static IntervalOfTime newIntervalOfTime(@Nullable String lower, @Nullable String upper) {
        return newIntervalOfTime(lower, upper, lower != null, upper != null);
    }

    public static IntervalOfTime newIntervalOfTime(@Nullable String lower, @Nullable String upper, boolean lowerIncluded,
            boolean upperIncluded) {
        IntervalOfTime result = new IntervalOfTime();
        result.setLower(lower);
        result.setUpper(upper);
        result.setLowerUnbounded(lower == null);
        result.setUpperUnbounded(upper == null);
        result.setLowerIncluded(lowerIncluded);
        result.setUpperIncluded(upperIncluded);
        return result;
    }

    public static IntervalOfTime newIntervalOfTime(@Nullable DvTime lower, @Nullable DvTime upper, boolean lowerIncluded,
            boolean upperIncluded) {
        return newIntervalOfTime(lower != null ? lower.getValue() : null, upper != null ? upper.getValue() : null, lowerIncluded,
                upperIncluded);
    }

    public static DvDateTime newDvDateTime(String value) {
        DvDateTime result = new DvDateTime();
        result.setValue(value);
        return result;
    }

    public static IntervalOfDateTime newIntervalOfDateTime(@Nullable String lower, @Nullable String upper) {
        return newIntervalOfDateTime(lower, upper, lower != null, upper != null);
    }

    public static IntervalOfDateTime newIntervalOfDateTime(@Nullable String lower, @Nullable String upper, boolean lowerIncluded,
            boolean upperIncluded) {
        IntervalOfDateTime result = new IntervalOfDateTime();
        result.setLower(lower);
        result.setUpper(upper);
        result.setLowerUnbounded(lower == null);
        result.setUpperUnbounded(upper == null);
        result.setLowerIncluded(lowerIncluded);
        result.setUpperIncluded(upperIncluded);
        return result;
    }

    public static IntervalOfDateTime newIntervalOfDateTime(@Nullable DvDateTime lower, @Nullable DvDateTime upper, boolean lowerIncluded,
            boolean upperIncluded) {
        return newIntervalOfDateTime(lower != null ? lower.getValue() : null, upper != null ? upper.getValue() : null, lowerIncluded,
                upperIncluded);
    }

    public static DvDuration newDvDuration(String value) {
        DvDuration result = new DvDuration();
        result.setValue(value);
        return result;
    }

    public static IntervalOfDuration newIntervalOfDuration(@Nullable String lower, @Nullable String upper) {
        return newIntervalOfDuration(lower, upper, lower != null, upper != null);
    }

    public static IntervalOfDuration newIntervalOfDuration(@Nullable String lower, @Nullable String upper, boolean lowerIncluded,
            boolean upperIncluded) {
        IntervalOfDuration result = new IntervalOfDuration();
        result.setLower(lower);
        result.setUpper(upper);
        result.setLowerUnbounded(lower == null);
        result.setUpperUnbounded(upper == null);
        result.setLowerIncluded(lowerIncluded);
        result.setUpperIncluded(upperIncluded);
        return result;
    }

    public static IntervalOfDuration newIntervalOfDuration(@Nullable DvDuration lower, @Nullable DvDuration upper, boolean lowerIncluded,
            boolean upperIncluded) {
        return newIntervalOfDuration(lower != null ? lower.getValue() : null, upper != null ? upper.getValue() : null, lowerIncluded,
                upperIncluded);
    }

    public static DvOrdinal newDvOrdinal(int value, CodePhrase code) {
        DvOrdinal result = new DvOrdinal();
        result.setValue(value);
        result.setSymbol(newDvCodedText(code.getCodeString(), code));
        return result;
    }

    public static DvCodedText newDvCodedText(String value, CodePhrase code) {
        DvCodedText result = new DvCodedText();
        result.setDefiningCode(code);
        result.setValue(value);
        return result;
    }

    public static DvQuantity newDvQuantity(String units, double magnitude, int precision) {
        DvQuantity result = new DvQuantity();
        result.setUnits(units);
        result.setMagnitude(magnitude);
        result.setPrecision(precision);
        return result;
    }


}
