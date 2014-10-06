/*
 * Copyright (C) 2014 Marand
 */

package com.marand.thinkehr.adl.validator;

/**
 * Holds a single validation error
 *
 * @author markopi
 */
public class AqlValidationError {
    private final Level level;
    private final String message;

    public AqlValidationError(Level level, String message, int line, int column) {
        this.level = level;
        this.message = message;
    }

    public AqlValidationError(Level level, String message) {
        this(level, message, -1, -1);
    }

    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AqlValidationError that = (AqlValidationError) o;

        if (level != that.level) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;

        return true;
    }

    @Override
    public String toString() {
        return level + ":" + message;
    }

    @Override
    public int hashCode() {
        int result = level != null ? level.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    public static enum Level {
        WARNING, ERROR
    }

}
