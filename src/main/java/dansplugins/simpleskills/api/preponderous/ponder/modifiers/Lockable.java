/*
 * Decompiled with CFR 0.150.
 */
package preponderous.ponder.modifiers;

import java.util.ArrayList;
import java.util.UUID;

public interface Lockable {
    public void setOwner(UUID var1);

    public UUID getOwner();

    public void addToAccessList(UUID var1);

    public void removeFromAccessList(UUID var1);

    public boolean hasAccess(UUID var1);

    public ArrayList<UUID> getAccessList();
}

