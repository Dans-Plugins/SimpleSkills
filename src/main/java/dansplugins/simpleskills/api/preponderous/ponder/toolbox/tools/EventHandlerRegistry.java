package dansplugins.simpleskills.api.preponderous.ponder.toolbox.tools;

import dansplugins.simpleskills.api.preponderous.ponder.Ponder;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;

/**
 * @author Daniel Stephenson
 */
public class EventHandlerRegistry {

    private final PluginManager manager;
    private Ponder ponder;

    public EventHandlerRegistry(Ponder ponder) {
        this.ponder = ponder;
        this.manager = Bukkit.getPluginManager();
    }

    /**
     * Method to register multiple listeners for a given plugin.
     *
     * @param listeners to register.
     * @param plugin to register it for.
     * @throws IllegalArgumentException if the arguments are invalid.
     */
    public void registerEventHandlers(ArrayList<Listener> listeners, Plugin plugin) {
        if (listeners == null || listeners.isEmpty()) throw new IllegalArgumentException("Listeners cannot be null or empty!");
        if (plugin == null) throw new IllegalArgumentException("Plugin cannot be null!");
        listeners.forEach(listener -> manager.registerEvents(listener, plugin));
    }

}
