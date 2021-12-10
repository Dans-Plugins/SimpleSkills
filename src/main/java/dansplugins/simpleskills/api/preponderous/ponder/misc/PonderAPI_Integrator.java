/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.java.JavaPlugin
 */
package preponderous.ponder.misc;

import org.bukkit.plugin.java.JavaPlugin;
import preponderous.ponder.Ponder;

public class PonderAPI_Integrator {
    private Ponder ponder = null;

    public PonderAPI_Integrator(JavaPlugin plugin) {
        this.ponder = new Ponder(plugin);
    }

    public Ponder getAPI() {
        return this.ponder;
    }
}

