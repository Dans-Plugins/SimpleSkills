/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 */
package preponderous.ponder.toolbox.tools;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import preponderous.ponder.Ponder;

public class EventHandlerRegistry {
    private final PluginManager manager;
    private Ponder ponder;

    public EventHandlerRegistry(Ponder ponder) {
        this.ponder = ponder;
        this.manager = Bukkit.getPluginManager();
    }

    public void registerEventHandlers(ArrayList<Listener> listeners, Plugin plugin) {
        if (listeners == null || listeners.isEmpty()) {
            throw new IllegalArgumentException("Listeners cannot be null or empty!");
        }
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null!");
        }
        listeners.forEach(listener -> this.manager.registerEvents(listener, plugin));
    }
}

