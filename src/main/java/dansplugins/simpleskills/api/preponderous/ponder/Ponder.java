/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.java.JavaPlugin
 */
package preponderous.ponder;

import org.bukkit.plugin.java.JavaPlugin;
import preponderous.ponder.services.CommandService;
import preponderous.ponder.services.LocaleService;
import preponderous.ponder.toolbox.Toolbox;

public class Ponder {
    private boolean debug = false;
    private JavaPlugin plugin;
    private CommandService commandService;
    private LocaleService localeService;
    private Toolbox toolbox;
    private String version = "v0.8";

    public Ponder(JavaPlugin plugin) {
        this.plugin = plugin;
        this.toolbox = new Toolbox(this);
        this.commandService = new CommandService(this);
        this.localeService = new LocaleService(this);
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public CommandService getCommandService() {
        return this.commandService;
    }

    public LocaleService getLocaleService() {
        return this.localeService;
    }

    public Toolbox getToolbox() {
        return this.toolbox;
    }

    public boolean isDebugEnabled() {
        return this.debug;
    }

    public String getVersion() {
        return this.version;
    }

    public void setDebug(boolean b) {
        this.debug = b;
    }

    public boolean log(String message) {
        if (this.debug) {
            System.out.println("[Ponder] " + message);
            return true;
        }
        return false;
    }
}

