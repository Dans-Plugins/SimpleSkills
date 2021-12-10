/*
 * Decompiled with CFR 0.150.
 */
package preponderous.ponder.modifiers;

import java.util.ArrayList;

public interface Diplomatic {
    public void addAlly(String var1);

    public void removeAlly(String var1);

    public boolean isAlly(String var1);

    public ArrayList<String> getAllies();

    public String getAlliesSeparatedByCommas();

    public void requestAlly(String var1);

    public boolean isRequestedAlly(String var1);

    public void removeAllianceRequest(String var1);

    public void addEnemy(String var1);

    public void removeEnemy(String var1);

    public boolean isEnemy(String var1);

    public ArrayList<String> getEnemyFactions();

    public String getEnemiesSeparatedByCommas();

    public void requestTruce(String var1);

    public boolean isTruceRequested(String var1);

    public void removeRequestedTruce(String var1);
}

