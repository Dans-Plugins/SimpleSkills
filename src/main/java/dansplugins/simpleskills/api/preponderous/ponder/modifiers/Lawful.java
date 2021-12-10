/*
 * Decompiled with CFR 0.150.
 */
package preponderous.ponder.modifiers;

import java.util.ArrayList;

public interface Lawful {
    public void addLaw(String var1);

    public boolean removeLaw(String var1);

    public boolean removeLaw(int var1);

    public boolean editLaw(int var1, String var2);

    public int getNumLaws();

    public ArrayList<String> getLaws();
}

