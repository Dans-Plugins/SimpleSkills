package dansplugins.simpleskills.listeners;

import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.services.StorageService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Writes the plugin's data out whenever the server saves a world, so that a crash costs at most
 * the skill progress made since the server's own last save rather than everything since the last
 * scheduled autosave.
 * <p>
 * The save runs on the thread the event arrives on, which is the main thread. The scheduled
 * autosave in {@code SimpleSkills#scheduleAutoSave()} runs asynchronously, but a world save is
 * already a synchronous, disk-bound pause for the server, and writing from the main thread is what
 * guarantees the player records are not being mutated while they are being iterated.
 * </p>
 */
public class WorldSaveEventListener implements Listener {
    /**
     * How long after a save the next {@link WorldSaveEvent} is ignored for.
     * <p>
     * A server fires this event once per world, so a single {@code /save-all} — or a single tick
     * of the server's own autosave — arrives as a burst of events that would otherwise rewrite the
     * same file once per world.
     * </p>
     */
    static final long MINIMUM_SAVE_INTERVAL_MILLIS = 5000L;

    private final StorageService storageService;
    private final Log log;

    private boolean hasSaved = false;
    private long lastSaveMillis;

    public WorldSaveEventListener(@NotNull StorageService storageService, @NotNull Log log) {
        this.storageService = storageService;
        this.log = log;
    }

    @EventHandler
    public void onWorldSave(@NotNull WorldSaveEvent event) {
        final long now = currentTimeMillis();
        if (hasSaved && now - lastSaveMillis < MINIMUM_SAVE_INTERVAL_MILLIS) {
            log.debug("Skipping save on world save of " + event.getWorld().getName()
                    + "; the previous save was less than " + MINIMUM_SAVE_INTERVAL_MILLIS + "ms ago.");
            return;
        }
        hasSaved = true;
        lastSaveMillis = now;
        log.debug("Saving files because the server saved world " + event.getWorld().getName() + ".");
        storageService.save();
    }

    /**
     * Method to obtain the current time the coalescing window is measured against.
     * <p>
     * This indirection exists so that tests can advance time without waiting for it to pass.
     * </p>
     *
     * @return the current time in milliseconds.
     */
    long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
