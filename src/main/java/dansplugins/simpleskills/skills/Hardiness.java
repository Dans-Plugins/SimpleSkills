package dansplugins.simpleskills.skills;

import dansplugins.simpleskills.AbstractSkill;
import dansplugins.simpleskills.data.PlayerRecord;
import dansplugins.simpleskills.utils.ChanceCalculator;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Callum Johnson
 * @since 11/01/2022 - 16:30
 */
public class Hardiness extends AbstractSkill {

    /**
     * The Hardiness skill is levelled by taking damage.
     */
    public Hardiness() {
        super("Hardiness", EntityDamageEvent.class);
    }

    /**
     * Method to get the chance of a skill incrementing or levelling.
     *
     * @return double chance (1-100).
     * @see #randomExpGainChance()
     */
    @Override
    public double getChance() {
        return 0;
    }

    /**
     * Method to determine if a skill is chance-incremented/levelled.
     *
     * @return {@code true} or {@code false}.
     */
    @Override
    public boolean randomExpGainChance() {
        return false;
    }

    /**
     * Method to handle the {@link EntityDamageEvent} event
     *
     * @param event to handle.
     */
    @EventHandler
    public void damageEvent(@NotNull EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        if (!(event.getEntity() instanceof Player)) return;
        incrementExperience((Player) event.getEntity());
        executeReward((Player) event.getEntity(), event.getCause(), event);
    }

    /**
     * Method to reward the player at their level.
     *
     * @param player    to reward.
     * @param skillData assigned data to the skill reward, 'Block' for 'BlockSkills' etc.
     */
    @Override
    public void executeReward(@NotNull Player player, Object... skillData) {
        final PlayerRecord record = getRecord(player);
        if (record == null) return;
        if (skillData.length != 2) throw new IllegalArgumentException("Skill Data is not of length '2'");
        final Object causeData = skillData[0];
        final Object eventData = skillData[1];
        if (!(causeData instanceof EntityDamageEvent.DamageCause))
            throw new IllegalArgumentException("Cause Data is not DamageCause.");
        if (!(eventData instanceof EntityDamageEvent)) throw new IllegalArgumentException("Event Data is not Event.");
        final EntityDamageEvent.DamageCause cause = (EntityDamageEvent.DamageCause) causeData;
        final EntityDamageEvent event = (EntityDamageEvent) eventData;
        if (!ChanceCalculator.getInstance().roll(record, this, 0.10)) return;
        final float weight = cause == EntityDamageEvent.DamageCause.FALL ? 0.75f : 0.5f;
        // More likely to cancel out if the damage is fall.
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
        if (ChanceCalculator.getInstance().roll(record, this, weight)) {
            // Damage Negation
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 5, 2);
            player.sendMessage("§bYour skin and bones have grown tough, you've sustained §ano §bdamage!");
        } else {
            // Damage Reduction
            event.setDamage(event.getDamage() / 2);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 5, 2);
            player.sendMessage("§bYour skin and bones have grown tough, you've sustained §aminimal §bdamage!");
        }
    }

}
