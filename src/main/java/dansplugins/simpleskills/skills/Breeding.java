package dansplugins.simpleskills.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.data.objects.PlayerRecord;
import dansplugins.simpleskills.services.ConfigService;
import dansplugins.simpleskills.services.MessageService;
import dansplugins.simpleskills.skills.abs.AbstractSkill;
import dansplugins.simpleskills.utils.ChanceCalculator;
import dansplugins.simpleskills.utils.Logger;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityBreedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Callum Johnson
 * @since 09/01/2022 - 16:55
 */
public class Breeding extends AbstractSkill {
    private final ChanceCalculator chanceCalculator;

    /**
     * Breeding is levelled up via mob-breeding.
     */
    public Breeding(ConfigService configService, Logger logger, PersistentData persistentData, SimpleSkills simpleSkills, MessageService messageService, ChanceCalculator chanceCalculator) {
        super(configService, logger, persistentData, simpleSkills, messageService, "Breeding", EntityBreedEvent.class);
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
     * Method handles the {@link EntityBreedEvent} event.
     *
     * @param event to handle.
     */
    @EventHandler
    public void breed(@NotNull EntityBreedEvent event) {
        if (event.getBreeder() == null) return;
        if (!(event.getBreeder() instanceof Player)) return;
        if (event.isCancelled()) return;
        final Player breeder = (Player) event.getBreeder();
        incrementExperience(breeder);
        executeReward(breeder, event);
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
        final Object eventData = skillData[0];
        if (!(eventData instanceof EntityBreedEvent)) return;
        if (!chanceCalculator.roll(record, this, 0.10)) return;
        final EntityBreedEvent breedEvent = (EntityBreedEvent) eventData;
        breedEvent.setExperience(breedEvent.getExperience() * 2);
        final String type = WordUtils.capitalizeFully(breedEvent.getFather().getType()
                .name().replaceAll("_", " ").toLowerCase() + "s");
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
        player.sendMessage(messageService.convert(Objects.requireNonNull(Objects.requireNonNull(messageService.getlang().getString("Skills.Breeding"))
                .replaceAll("%type%", type))));
    }

}
