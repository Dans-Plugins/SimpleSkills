package dansplugins.simpleskills.listeners;

import dansplugins.simpleskills.SimpleSkills;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

/**
 * Tracks which blocks were placed by a player so that block-based skills can refuse to grant
 * experience for them, closing the exploit where a Silk Touch-harvested ore (or other
 * skill-tracked block) is placed back down and re-mined repeatedly for infinite experience.
 * <p>
 * Must be registered <em>after</em> the skills in {@code SimpleSkills#registerEventListeners()}
 * so that {@link #onBlockBreak(BlockBreakEvent)} (which cleans up the tracking metadata) runs
 * after the skills' own {@code MONITOR}-priority handlers have had a chance to check it.
 * </p>
 */
public class PlacedBlockListener implements Listener {
    private static final String METADATA_KEY = "simpleskills-player-placed";

    private final SimpleSkills simpleSkills;

    public PlacedBlockListener(@NotNull SimpleSkills simpleSkills) {
        this.simpleSkills = simpleSkills;
    }

    /**
     * @param block the block to check.
     * @return {@code true} if this block was placed by a player and has not yet been broken.
     */
    public static boolean isPlayerPlaced(@NotNull Block block) {
        return block.hasMetadata(METADATA_KEY);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        event.getBlock().setMetadata(METADATA_KEY, new FixedMetadataValue(simpleSkills, true));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (block.hasMetadata(METADATA_KEY)) {
            block.removeMetadata(METADATA_KEY, simpleSkills);
        }
    }
}
