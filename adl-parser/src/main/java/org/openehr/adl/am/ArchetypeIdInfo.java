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

package org.openehr.adl.am;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author markopi
 */
public class ArchetypeIdInfo {

    //    archetype id id formalism (currently missing suffix)
    //    ALPHANUM_CHAR [a-zA-Z0-9_]
    //    ALPHANUM_STR {ALPHANUM_CHAR}+
    //    NAMECHAR [a-zA-Z0-9._\-]
    //    NAMESTR [a-zA-Z][a-zA-Z0-9_]+
    //    ARCHETYPE_ID ({NAMESTR}(\.{ALPHANUM_STR})*::)?{NAMESTR}-{ALPHANUM_STR}-{NAMESTR}\.{NAMESTR}(-{ALPHANUM_STR})*\.v[0-9]+((\.[0-9]+){0,2}((-rc|\+u|\+)[0-9]+)?)?
    private static final Pattern ARCHETYPE_ID_PATTERN = Pattern.compile(
            "(?:(?<namespace>[a-zA-Z0-9_.]+)::)?(?<openehr>\\w+)\\-(?<ehr>\\w+)\\-(?<rmtype>\\w+)\\.(?<ident>[a-zA-Z0-9_-]+)\\.v(?<major>\\d+)(?:\\.(?<minor>\\d+))?(?:\\.(?<patch>\\d+))?"
    );


    private String namespace;
    private String openEHR;
    private String ehr;
    private String rmType;
    private String identifier;
    private Integer versionMajor;
    private Integer versionMinor;
    private Integer versionPatch;

    private ArchetypeIdInfo() {
    }

    public static ArchetypeIdInfo ofOpenEHR(@Nullable String namespace, String rmType, String identifier,
                                            Integer versionMajor, @Nullable Integer versionMinor, @Nullable Integer versionPatch) {
        ArchetypeIdInfo result = new ArchetypeIdInfo();
        result.namespace = namespace;
        result.openEHR = "openEHR";
        result.ehr = "EHR";
        result.rmType=rmType;
        result.identifier = identifier;
        result.versionMajor = versionMajor;
        result.versionMinor = versionMinor;
        result.versionPatch = versionPatch;
        return result;

    }

    public static ArchetypeIdInfo parse(String archetypeId) {
        Matcher m = ARCHETYPE_ID_PATTERN.matcher(archetypeId);
        if (!m.find()) {
            throw new IllegalArgumentException("Not a valid archetype id: " + archetypeId);
        }
        ArchetypeIdInfo result = new ArchetypeIdInfo();
        result.namespace = m.group("namespace");
        result.openEHR = m.group("openehr");
        result.ehr = m.group("ehr");
        result.rmType = m.group("rmtype");
        result.identifier = m.group("ident");
        result.versionMajor = parseNullableInt(m.group("major"));
        result.versionMinor = parseNullableInt(m.group("minor"));
        result.versionPatch = parseNullableInt(m.group("patch"));
        return result;
    }

    @Nullable
    private static Integer parseNullableInt(@Nullable String str) {
        return str != null ? Integer.parseInt(str) : null;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getOpenEHR() {
        return openEHR;
    }

    public void setOpenEHR(String openEHR) {
        this.openEHR = openEHR;
    }

    public String getEhr() {
        return ehr;
    }

    public void setEhr(String ehr) {
        this.ehr = ehr;
    }

    public String getRmType() {
        return rmType;
    }

    public void setRmType(String rmType) {
        this.rmType = rmType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getVersionMajor() {
        return versionMajor;
    }

    public void setVersionMajor(Integer versionMajor) {
        this.versionMajor = versionMajor;
    }

    public Integer getVersionMinor() {
        return versionMinor;
    }

    public void setVersionMinor(Integer versionMinor) {
        this.versionMinor = versionMinor;
    }

    public Integer getVersionPatch() {
        return versionPatch;
    }

    public void setVersionPatch(Integer versionPatch) {
        this.versionPatch = versionPatch;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        appendInterfaceId(result);
        if (versionMinor != null) {
            result.append(".").append(versionMinor);
        }
        if (versionPatch != null) {
            result.append(".").append(versionPatch);
        }
        return result.toString();
    }

    /**
     * Like toString, except stops at major version.
     * @return interface string for the archetypeId
     */
    public String toInterfaceString() {
        StringBuilder result = new StringBuilder();
        appendInterfaceId(result);
        return result.toString();

    }

    private void appendInterfaceId(StringBuilder result) {
        if (namespace != null) {
            result.append(namespace).append("::");
        }
        result.append(openEHR).append("-");
        result.append(ehr).append("-");
        result.append(rmType).append(".");
        result.append(identifier);
        result.append(".v").append(versionMajor);
    }

}
