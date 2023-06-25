package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.abs.AbstractSkill;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.logging.Logger;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Callum Johnson
 * @since 11/01/2022 - 16:59
 */
public class Strength extends AbstractSkill {
    private final ChanceCalculator chanceCalculator;

    /**
     * The Strength Skill is levelled by hitting Entities.
     */
    public Strength(ConfigService configService, Logger logger, PlayerRecordRepository playerRecordRepository, SimpleSkills simpleSkills, MessageService messageService, ChanceCalculator chanceCalculator) {
        super(configService, logger, playerRecordRepository, simpleSkills, messageService, "Strength", EntityDamageByEntityEvent.class);
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
     * Event handler for the Strength skill.
     *
     * @param event to handle.
     */
    @EventHandler
    public void onKill(@NotNull EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        final Player killer = (Player) event.getDamager();
        incrementExperience(killer);
        executeReward(killer, event.getEntity());
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
        if (skillData.length != 1) throw new IllegalArgumentException("Skill Data is not of length '1'");
        final Object entityData = skillData[0];
        if (!(entityData instanceof Entity)) return;
        final Entity entity = (Entity) entityData;
        if (!chanceCalculator.roll(record, this, 0.05)) return;
        if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 1, true, false));
        final String typeName = WordUtils.capitalizeFully(entity.getType().name().replaceAll("_", " ").toLowerCase());
        final boolean nRequired = "aeiou".contains(String.valueOf(typeName.toLowerCase().charAt(0)));
        final String attacked = entity instanceof Player ? entity.getName() :
                "a" + (nRequired ? "n" : "") + " §b"
                        + WordUtils.capitalizeFully(entity.getType().name().replaceAll("_", " ").toLowerCase());
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 5, 2);
        player.sendMessage(messageService.convert(Objects.requireNonNull(Objects.requireNonNull(messageService.getlang().getString("Skills.Strength"))
                .replaceAll("%attacked%", attacked))));
    }

}
