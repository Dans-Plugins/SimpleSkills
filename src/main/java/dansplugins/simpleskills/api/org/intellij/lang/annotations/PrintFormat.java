/*
 * Decompiled with CFR 0.150.
 */
package org.intellij.lang.annotations;

import org.intellij.lang.annotations.Pattern;

@Pattern(value="(?:[^%]|%%|(?:%(?:\\d+\\$)?(?:[-#+ 0,(<]*)?(?:\\d+)?(?:\\.\\d+)?(?:[tT])?(?:[a-zA-Z%])))*")
public @interface PrintFormat {
}

