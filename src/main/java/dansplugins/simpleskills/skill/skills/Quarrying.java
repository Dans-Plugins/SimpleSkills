package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.abs.AbstractBlockSkill;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.logging.Log;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Callum Johnson
 * @since 06/01/2022 - 00:39
 */
public class Quarrying extends AbstractBlockSkill {
    private final ChanceCalculator chanceCalculator;

    /**
     * Quarrying is for clearing out large spaces of blocks.
     */
    public Quarrying(ConfigService configService, Log log, PlayerRecordRepository playerRecordRepository, SimpleSkills simpleSkills, MessageService messageService, ChanceCalculator chanceCalculator) {
        super(configService, log, playerRecordRepository, simpleSkills, messageService, "Quarrying");
        this.chanceCalculator = chanceCalculator;
    }

    /**
     * Method to determine if the item provided is valid.
     *
     * @param item        to check.
     * @param targetBlock to do sub-checks with.
     * @param context     of which the event happened.
     * @return {@code true} if it is.
     */
    @Override
    public boolean isRequiredItem(@NotNull ItemStack item, @NotNull Block targetBlock, @NotNull String context) {
        return item.getType().name().contains("PICKAXE");
    }

    /**
     * Method to determine if an item is required to be in the hand of the participant.
     *
     * @return {@code true} if it is.
     */
    @Override
    public boolean isItemRequired() {
        return true;
    }

    /**
     * Method to determine if the Block skill exclusivity.
     *
     * @return {@link BlockSkillType} for this skill.
     */
    @Override
    public @NotNull BlockSkillType getBlockSkillType() {
        return BlockSkillType.BREAK_SPECIFIC;
    }

    /**
     * Method to determine if the Block material is valid for this Skill.
     *
     * @param material to test.
     * @return {@code true} if it is.
     */
    @Override
    public boolean isValidMaterial(@NotNull Material material) {
        switch (material.name()) {
            case "STONE":
            case "TERRACOTTA":
            case "GRANITE":
            case "ANDESITE":
            case "DIORITE":
            case "DEEPSLATE":
            case "SANDSTONE":
            case "RED_SANDSTONE":
            case "END_STONE":
            case "NETHERRACK":
                return true;
            default:
                return false;
        }
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
     * Method to reward the player at their level.
     *
     * @param player    to reward.
     * @param skillData assigned data to the skill reward, 'Block' for 'BlockSkills' etc.
     */
    @Override
    public void executeReward(@NotNull Player player, Object... skillData) {
        if (skillData.length != 2) throw new IllegalArgumentException("Skill Data is not of length '2'");
        final Object blockData = skillData[0];
        if (!(blockData instanceof Block)) throw new IllegalArgumentException("SkillData[0] is not Block");
        final PlayerRecord record = getRecord(player);
        if (record == null) return;
        final Block block = (Block) blockData;
        if (chanceCalculator.roll(record, this, 0.10)) {
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
            if (chanceCalculator.roll(record, this, 0.50)) {
                final Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
                drops.forEach(drop -> {
                    if (drop.getType().isAir()) return;
                    block.getWorld().dropItemNaturally(block.getLocation(), drop);
                });
                player.sendMessage(messageService.convert(Objects.requireNonNull(Objects.requireNonNull(messageService.getlang().getString("Skills.Quarrying.DoubleDrop"))
                        .replaceAll("%item%", WordUtils.capitalizeFully(block.getType()
                                .name().replaceAll("_", " ").toLowerCase())))));
                return;
            }
            // Drop experience orb instead of special items
            final ExperienceOrb entity = (ExperienceOrb) block.getWorld()
                    .spawnEntity(block.getLocation(), EntityType.EXPERIENCE_ORB);
            final int exp = new Random().nextInt(5) + 1; // 1-5 experience points
            entity.setExperience(exp);
            entity.setGlowing(true);
            final String expMessage = messageService.getlang().getString("Skills.Quarrying.Exp");
            player.sendMessage(messageService.convert(Objects.requireNonNull(expMessage)
                    .replaceAll("%exp%", String.valueOf(exp))));
        }
    }

}
