package dansplugins.simpleskills.enums;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Callum Johnson
 * @since 05/01/2022 - 23:33
 */
public enum Triggers {

    BLOCK_PLACE(BlockPlaceEvent.class),
    BLOCK_BREAK(BlockBreakEvent.class),
    BLOCK_CLICK(PlayerInteractEvent.class),
    BREED(EntityBreedEvent.class),
    CRAFT(CraftItemEvent.class),
    PLAYER_KILL(PlayerDeathEvent.class),
    ENCHANT(EnchantItemEvent.class),
    FISH(PlayerFishEvent.class),
    DAMAGE(EntityDamageEvent.class),
    ENTITY_KILL(EntityDeathEvent.class),
    ATTACK(EntityDamageByEntityEvent.class),
    MOVE(PlayerMoveEvent.class);

    private final Class<? extends Event> triggerClass;

    Triggers(@NotNull Class<? extends Event> triggerClass) {
        this.triggerClass = triggerClass;
    }

    @NotNull
    public Class<? extends Event> getTriggerClass() {
        return triggerClass;
    }

}
