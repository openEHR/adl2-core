package org.openehr.adl.rm;

import java.util.Objects;

/**
 * @author markopi
 */
public class RmCardinality {
    private boolean isOrdered;
    private boolean isUnique;
    private RmMultiplicity interval;

    public RmCardinality() {
    }

    public RmCardinality(boolean isOrdered, boolean isUnique, RmMultiplicity interval) {
        this.isOrdered = isOrdered;
        this.isUnique = isUnique;
        this.interval = interval;
    }

    public boolean isOrdered() {
        return isOrdered;
    }

    public void setIsOrdered(boolean isOrdered) {
        this.isOrdered = isOrdered;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setIsUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

    public RmMultiplicity getInterval() {
        return interval;
    }

    public void setInterval(RmMultiplicity interval) {
        this.interval = interval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RmCardinality that = (RmCardinality) o;
        return Objects.equals(isOrdered, that.isOrdered) &&
                Objects.equals(isUnique, that.isUnique) &&
                Objects.equals(interval, that.interval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isOrdered, isUnique, interval);
    }
}
