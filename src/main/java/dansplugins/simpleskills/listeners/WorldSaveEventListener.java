package dansplugins.simpleskills.listeners;

import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.services.StorageService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.jetbrains.annotations.NotNull;

public class WorldSaveEventListener implements Listener {
    private final StorageService storageService;
    private final Log log;

    public WorldSaveEventListener(@NotNull StorageService storageService, @NotNull Log log) {
        this.storageService = storageService;
        this.log = log;
    }

    @EventHandler
    public void onWorldSave(@NotNull WorldSaveEvent event) {
        log.debug("World save event detected for world: " + event.getWorld().getName() + ". Saving SimpleSkills plugin data.");
        storageService.save();
        log.debug("SimpleSkills plugin data saved successfully during world save.");
    }
}