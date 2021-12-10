/*
 * Decompiled with CFR 0.150.
 */
package preponderous.ponder.modifiers;

import java.util.ArrayList;

public interface Feudal {
    public boolean isVassal(String var1);

    public boolean isLiege();

    public void setLiege(String var1);

    public String getLiege();

    public boolean hasLiege();

    public boolean isLiege(String var1);

    public void addVassal(String var1);

    public void removeVassal(String var1);

    public void clearVassals();

    public int getNumVassals();

    public ArrayList<String> getVassals();

    public String getVassalsSeparatedByCommas();

    public void addAttemptedVassalization(String var1);

    public boolean hasBeenOfferedVassalization(String var1);

    public void removeAttemptedVassalization(String var1);
}

