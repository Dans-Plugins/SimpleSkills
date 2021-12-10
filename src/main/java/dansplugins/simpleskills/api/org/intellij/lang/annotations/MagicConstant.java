/*
 * Decompiled with CFR 0.150.
 */
package org.intellij.lang.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NonNls;

@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface MagicConstant {
    public long[] intValues() default {};

    @NonNls
    public String[] stringValues() default {};

    public long[] flags() default {};

    public Class<?> valuesFromClass() default void.class;

    public Class<?> flagsFromClass() default void.class;
}

