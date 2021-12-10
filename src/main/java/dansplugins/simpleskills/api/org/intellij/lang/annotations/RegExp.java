/*
 * Decompiled with CFR 0.150.
 */
package org.intellij.lang.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NonNls;

@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.ANNOTATION_TYPE})
@Language(value="RegExp")
public @interface RegExp {
    @NonNls
    public String prefix() default "";

    @NonNls
    public String suffix() default "";
}

