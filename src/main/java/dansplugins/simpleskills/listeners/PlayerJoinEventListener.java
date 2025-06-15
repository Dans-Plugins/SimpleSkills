package dansplugins.simpleskills.listeners;

import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinEventListener implements Listener {
    private final PlayerRecordRepository playerRecordRepository;
    private final Log log;

public PlayerJoinEventListener(@NotNull PlayerRecordRepository playerRecordRepository, @NotNull Log log) {
        this.playerRecordRepository = playerRecordRepository;
        this.log = log;
    }

    @EventHandler
    public void onPlayerJoin(@NotNull org.bukkit.event.player.PlayerJoinEvent event) {
        if (playerRecordRepository.getPlayerRecord(event.getPlayer().getUniqueId()) == null) {
            log.debug("No player record found for " + event.getPlayer().getName() + ". Creating a new one.");
            boolean success = playerRecordRepository.createPlayerRecord(event.getPlayer().getUniqueId());
            if (!success) {
                log.error("Failed to create player record for " + event.getPlayer().getName() + ". Please check the database connection.");
                event.getPlayer().sendMessage("Error creating SimpleSkills player record. Please try again later.");
            }
        }
    }

}
