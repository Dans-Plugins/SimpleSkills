/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.java.JavaPlugin
 */
package preponderous.ponder;

import org.bukkit.plugin.java.JavaPlugin;
import preponderous.ponder.Ponder;
import preponderous.ponder.misc.PonderAPI_Integrator;
import preponderous.ponder.toolbox.Toolbox;

public abstract class AbstractPonderPlugin
extends JavaPlugin {
    protected PonderAPI_Integrator ponderAPI_integrator;
    protected Toolbox toolbox;

    public Ponder getPonderAPI() {
        return this.ponderAPI_integrator.getAPI();
    }

    public Toolbox getToolbox() {
        return this.toolbox;
    }
}

