package org.openehr.adl.rm;

import java.util.Objects;

/**
 * @author markopi
 */
public class RmMultiplicity {
    private Integer lower;
    private Integer upper;
    private Boolean lowerIncluded;
    private Boolean upperIncluded;
    private boolean lowerUnbounded;
    private boolean upperUnbounded;

    public RmMultiplicity() {
    }

    public RmMultiplicity(Integer lower, Integer upper) {
        this.lower = lower;
        this.upper = upper;

        this.lowerIncluded = lower != null;
        this.lowerUnbounded = lower != null;

        this.upperIncluded = upper != null;
        this.upperUnbounded = upper != null;
    }

    public Integer getLower() {
        return lower;
    }

    public void setLower(Integer lower) {
        this.lower = lower;
    }

    public Integer getUpper() {
        return upper;
    }

    public void setUpper(Integer upper) {
        this.upper = upper;
    }

    public Boolean getLowerIncluded() {
        return lowerIncluded;
    }

    public void setLowerIncluded(Boolean lowerIncluded) {
        this.lowerIncluded = lowerIncluded;
    }

    public Boolean getUpperIncluded() {
        return upperIncluded;
    }

    public void setUpperIncluded(Boolean upperIncluded) {
        this.upperIncluded = upperIncluded;
    }

    public boolean isLowerUnbounded() {
        return lowerUnbounded;
    }

    public void setLowerUnbounded(boolean lowerUnbounded) {
        this.lowerUnbounded = lowerUnbounded;
    }

    public boolean isUpperUnbounded() {
        return upperUnbounded;
    }

    public void setUpperUnbounded(boolean upperUnbounded) {
        this.upperUnbounded = upperUnbounded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RmMultiplicity that = (RmMultiplicity) o;
        return Objects.equals(lowerUnbounded, that.lowerUnbounded) &&
                Objects.equals(upperUnbounded, that.upperUnbounded) &&
                Objects.equals(lower, that.lower) &&
                Objects.equals(upper, that.upper) &&
                Objects.equals(lowerIncluded, that.lowerIncluded) &&
                Objects.equals(upperIncluded, that.upperIncluded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lower, upper, lowerIncluded, upperIncluded, lowerUnbounded, upperUnbounded);
    }
}
