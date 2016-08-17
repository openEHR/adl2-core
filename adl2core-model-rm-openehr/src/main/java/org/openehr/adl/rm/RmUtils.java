package org.openehr.adl.rm;

import com.google.common.base.CaseFormat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

/**
 * @author markopi
 */
public class RmUtils {
    public static <T> Collection<T> emptyIfNull(@Nullable Collection<T> details) {
        if (details == null) return Collections.emptyList();
        return details;
    }

    public static String getRmTypeName(@Nonnull Class<?> clazz) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, clazz.getSimpleName());
    }

}
