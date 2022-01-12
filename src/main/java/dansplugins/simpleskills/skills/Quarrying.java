package dansplugins.simpleskills.skills;

import dansplugins.simpleskills.AbstractBlockSkill;
import dansplugins.simpleskills.data.PlayerRecord;
import dansplugins.simpleskills.utils.ChanceCalculator;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Callum Johnson
 * @since 06/01/2022 - 00:39
 */
public class Quarrying extends AbstractBlockSkill {

    /**
     * Quarrying is for clearing out large spaces of blocks.
     */
    public Quarrying() {
        super("Quarrying");
    }

    /**
     * Method to determine if the item provided is valid.
     *
     * @param item to check.
     * @param targetBlock to do sub-checks with.
     * @param context of which the event happened.
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
        if (ChanceCalculator.getInstance().roll(record, this, 0.10)) {
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
            if (ChanceCalculator.getInstance().roll(record, this, 0.50)) {
                final Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
                drops.forEach(drop -> {
                    if (drop.getType().isAir()) return;
                    block.getWorld().dropItemNaturally(block.getLocation(), drop);
                });
                player.sendMessage("§bYou got double drops for that §a" + WordUtils.capitalizeFully(block.getType()
                        .name().replaceAll("_", " ").toLowerCase()) + "!");
                return;
            }
            final List<Material> rewardTypes = getRewardTypes(block.getType());
            final Material reward = rewardTypes.get(new Random().nextInt(rewardTypes.size()));
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(reward));
            if (reward == Material.GLASS_BOTTLE) player.sendMessage("§bMake sure you get water for your thirst!");
            else player.sendMessage("§bYou got extra lucky and found something §6special§b!");
        }
    }

    @NotNull
    private List<Material> getRewardTypes(@NotNull Material material) {
        switch (material.name()) {
            case "STONE":
                return Collections.singletonList(Material.STONE);
            case "TERRACOTTA":
                return Arrays.asList(Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.MAGENTA_TERRACOTTA,
                        Material.LIGHT_BLUE_TERRACOTTA, Material.YELLOW_TERRACOTTA, Material.LIME_TERRACOTTA,
                        Material.PINK_TERRACOTTA, Material.GRAY_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA,
                        Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA, Material.BLUE_TERRACOTTA,
                        Material.BROWN_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA,
                        Material.BLACK_TERRACOTTA);
            case "GRANITE":
                return Collections.singletonList(Material.POLISHED_GRANITE);
            case "ANDESITE":
                return Collections.singletonList(Material.POLISHED_ANDESITE);
            case "DIORITE":
                return Collections.singletonList(Material.POLISHED_DIORITE);
            case "DEEPSLATE":
                return Collections.singletonList(Material.DEEPSLATE);
            case "SANDSTONE":
            case "RED_SANDSTONE":
                return Collections.singletonList(Material.GLASS_BOTTLE);
            case "END_STONE":
                return Collections.singletonList(Material.ENDER_PEARL);
            case "NETHERRACK":
                return Collections.singletonList(Material.NETHER_BRICK);
            default:
                throw new IllegalArgumentException("Material " + material + " is not valid.");
        }
    }

}
