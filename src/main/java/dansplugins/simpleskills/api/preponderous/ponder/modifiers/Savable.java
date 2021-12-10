/*
 * Decompiled with CFR 0.150.
 */
package preponderous.ponder.modifiers;

import java.util.Map;

public interface Savable {
    public Map<String, String> save();

    public void load(Map<String, String> var1);
}

