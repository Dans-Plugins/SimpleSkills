package dansplugins.simpleskills.skills;

import dansplugins.simpleskills.AbstractSkill;
import dansplugins.simpleskills.data.PlayerRecord;
import dansplugins.simpleskills.utils.ChanceCalculator;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityBreedEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Callum Johnson
 * @since 09/01/2022 - 16:55
 */
public class Breeding extends AbstractSkill {

    /**
     * Breeding is levelled up via mob-breeding.
     */
    public Breeding() {
        super("Breeding", EntityBreedEvent.class);
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
     * Method handle the {@link EntityBreedEvent} event.
     *
     * @param event to handle.
     */
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
        if (!ChanceCalculator.getInstance().roll(record, this, 0.10)) return;
        final EntityBreedEvent breedEvent = (EntityBreedEvent) eventData;
        breedEvent.setExperience(breedEvent.getExperience() * 2);
        final String type = WordUtils.capitalizeFully(breedEvent.getFather().getType()
                .name().replaceAll("_", " ").toLowerCase() + "s");
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
        player.sendMessage("§bYou got double experience for breeding your §a" + type + "§b!");
    }

}
