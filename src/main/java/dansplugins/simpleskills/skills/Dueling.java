package dansplugins.simpleskills.skills;

import dansplugins.simpleskills.AbstractSkill;
import dansplugins.simpleskills.data.PlayerRecord;
import dansplugins.simpleskills.utils.ChanceCalculator;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Callum Johnson
 * @since 11/01/2022 - 11:17
 */
public class Dueling extends AbstractSkill {

    /**
     * The dueling skill is levelled by killing Players.
     */
    public Dueling() {
        super("Dueling", PlayerDeathEvent.class);
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
     * Event handler for the dueling skill.
     *
     * @param event to handle.
     */
    public void onKill(@NotNull PlayerDeathEvent event) {
        final Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        incrementExperience(killer);
        executeReward(killer, event.getEntity().getName());
    }

    /**
     * Method to reward the player at their level.
     *
     * @param player    to reward.
     * @param skillData assigned data to the skill reward, 'Block' for 'BlockSkills' etc.
     */
    @Override
    public void executeReward(@NotNull Player player, Object... skillData) {
        if (skillData.length != 1) throw new IllegalArgumentException("Skill Data is not of length [0].");
        final PlayerRecord record = getRecord(player);
        if (record == null) return;
        if (!ChanceCalculator.getInstance().roll(record, this, 0.10)) return;
        if (player.hasPotionEffect(PotionEffectType.ABSORPTION)) player.removePotionEffect(PotionEffectType.ABSORPTION);
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 500, 5, true, false));
        player.sendMessage("§bYou got a boost after killing §a" + skillData[0] + "§b!");
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
        player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 5, 2);
    }

}
