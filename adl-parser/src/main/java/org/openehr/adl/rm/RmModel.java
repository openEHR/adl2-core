/*
 * Copyright (C) 2014 Marand
 */

package org.openehr.adl.rm;

/**
 * Provides information about a given rm model
 *
 * @author markopi
 */
public interface RmModel {
    /**
     * Maps a type name to a java class with that name.
     *
     * @param rmType type name
     * @return java class
     * @throws org.openehr.adl.rm.RmModelException if no such rm type exists
     */
    Class<?> getRmClass(String rmType);

    /**
     * Gets the type name of a particular java class
     *
     * @param rmClass java class
     * @return type name
     * @throws org.openehr.adl.rm.RmModelException if java class does not represent a rm type
     */
    String getRmTypeName(Class<?> rmClass);

    /**
     * checks if a given rm type exists
     *
     * @param rmType type name
     * @return true if the type exists
     */
    boolean rmTypeExists(String rmType);

    /**
     * Returns information about a given rm attribute
     *
     * @param rmType    name of the containing rm type
     * @param attribute attribute name
     * @return attribute information
     * @throws org.openehr.adl.rm.RmModelException if no such attribute exists
     */
    RmTypeAttribute getRmAttribute(String rmType, String attribute);


}
