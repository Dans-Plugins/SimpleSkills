package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.abs.AbstractSkill;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.logging.Log;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Callum Johnson
 * @since 11/01/2022 - 16:30
 */
public class Hardiness extends AbstractSkill {
    private final ChanceCalculator chanceCalculator;

    /**
     * The Hardiness skill is levelled by taking damage.
     */
    public Hardiness(ConfigService configService, Log log, PlayerRecordRepository playerRecordRepository, SimpleSkills simpleSkills, MessageService messageService, ChanceCalculator chanceCalculator) {
        super(configService, log, playerRecordRepository, simpleSkills, messageService, "Hardiness", EntityDamageEvent.class);
        this.chanceCalculator = chanceCalculator;
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
        if (!chanceCalculator.roll(record, this, 0.10)) return;
        final float weight = cause == EntityDamageEvent.DamageCause.FALL ? 0.75f : 0.5f;
        // More likely to cancel out if the damage is fall.
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
        if (chanceCalculator.roll(record, this, weight)) {
            // Damage Negation
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 5, 2);
            player.sendMessage(messageService.convert(Objects.requireNonNull(messageService.getlang().getString("Skills.Hardiness.Negation"))));
        } else {
            // Damage Reduction
            event.setDamage(event.getDamage() / 2);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 5, 2);
            player.sendMessage(messageService.convert(Objects.requireNonNull(messageService.getlang().getString("Skills.Hardiness.Reduction"))));

        }
    }

}
