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

package org.openehr.adl.rm;

/**
 * @author markopi
 */
public class RmTypes {
    public static final String INTEGER = "Integer";
    public static final String BOOLEAN = "Boolean";
    public static final String REAL = "Real";
    public static final String STRING = "String";
    public static final String RM = "RM";
    public static final String DURATION = "DURATION";
    public static final String DATE_TIME = "DATE_TIME";
    public static final String LIST = "LIST";
    public static final String DATE = "ISO8601_DATE";
    public static final String TIME = "ISO8601_TIME";
    public static final String OBJECT = "OBJECT";
    public static final String RM_OBJECT = "RM_OBJECT";

    public enum ReferenceType {CONSTANT, ATTRIBUTE, FUNCTION, CONSTRAINT}

    ;
}
