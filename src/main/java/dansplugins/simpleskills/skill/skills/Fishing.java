package dansplugins.simpleskills.skill.skills;

import com.cryptomorin.xseries.XMaterial;

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
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Callum Johnson
 * @since 11/01/2022 - 16:16
 */
public class Fishing extends AbstractSkill {
    private final ChanceCalculator chanceCalculator;

    /**
     * The Fishing skill is levelled through fishing up fishies and goodies.
     */
    public Fishing(ConfigService configService, Logger logger, PlayerRecordRepository playerRecordRepository, SimpleSkills simpleSkills, MessageService messageService, ChanceCalculator chanceCalculator) {
        super(configService, logger, playerRecordRepository, simpleSkills, messageService, "Fishing", PlayerFishEvent.class);
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
     * Method to handle the {@link PlayerFishEvent} event.
     *
     * @param event to handle.
     */
    @EventHandler
    public void onFish(@NotNull PlayerFishEvent event) {
        final Player player = event.getPlayer();
        if (event.getCaught() != null && event.getState().name().contains("CAUGHT")) {
            incrementExperience(player);
            executeReward(event.getPlayer(), event.getCaught());
        }
    }

    /**
     * Method to reward the player at their level.
     *
     * @param player    to reward.
     * @param skillData assigned data to the skill reward, 'Block' for 'BlockSkills' etc.
     */
    @Override
    public void executeReward(@NotNull Player player, Object... skillData) {
        if (skillData.length != 1) throw new IllegalArgumentException("Skill Data is not of length '1'");
        final Object fishedData = skillData[0];
        if (!(fishedData instanceof Entity)) throw new IllegalArgumentException("Skill Data[0] is not Entity.");
        final Entity entity = (Entity) fishedData;
        final PlayerRecord record = getRecord(player);
        if (record == null) return;
        if (!chanceCalculator.roll(record, this, 0.10)) return;
        String entityName;
        if (entity instanceof Item) entityName = ((Item) entity).getItemStack().getType().name();
        else entityName = entity.getType().getKey().getKey();
        entityName = WordUtils.capitalizeFully(entityName.toLowerCase().replaceAll("_", " "));
        player.sendMessage(messageService.convert(Objects.requireNonNull(Objects.requireNonNull(messageService.getlang().getString("Skills.Fishing"))
                .replaceAll("%entityname%", entityName))));
        player.getInventory().addItem(new ItemStack(Objects.requireNonNull(XMaterial.GOLDEN_APPLE.parseMaterial()), 1));
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
    }

}
