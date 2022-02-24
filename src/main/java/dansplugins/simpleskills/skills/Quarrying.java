package dansplugins.simpleskills.skills;

import com.cryptomorin.xseries.XMaterial;
import dansplugins.simpleskills.AbstractBlockSkill;
import dansplugins.simpleskills.data.PlayerRecord;
import dansplugins.simpleskills.services.LocalMessageService;
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
        if (ChanceCalculator.getInstance().roll(record, this, 0.10)) {
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
            if (ChanceCalculator.getInstance().roll(record, this, 0.50)) {
                final Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
                drops.forEach(drop -> {
                    if (drop.getType().isAir()) return;
                    block.getWorld().dropItemNaturally(block.getLocation(), drop);
                });
                player.sendMessage(LocalMessageService.getInstance().convert(Objects.requireNonNull(Objects.requireNonNull(LocalMessageService.getInstance().getlang().getString("Skills.Quarrying.DoubleDrop"))
                        .replaceAll("%item%", WordUtils.capitalizeFully(block.getType()
                                .name().replaceAll("_", " ").toLowerCase())))));
                return;
            }
            final List<Material> rewardTypes = getRewardTypes(block.getType());
            final Material reward = rewardTypes.get(new Random().nextInt(rewardTypes.size()));
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(reward));
            if (reward == XMaterial.GLASS_BOTTLE.parseMaterial())
                player.sendMessage(LocalMessageService.getInstance().convert(Objects.requireNonNull(LocalMessageService.getInstance().getlang().getString("Skills.Quarrying.Water"))));
            else
                player.sendMessage(LocalMessageService.getInstance().convert(Objects.requireNonNull(LocalMessageService.getInstance().getlang().getString("Skills.Quarrying.Luck"))));
        }
    }

    @NotNull
    private List<Material> getRewardTypes(@NotNull Material material) {
        switch (material.name()) {
            case "STONE":
                return Collections.singletonList(XMaterial.STONE.parseMaterial());
            case "TERRACOTTA":
                return Arrays.asList(XMaterial.WHITE_TERRACOTTA.parseMaterial(), XMaterial.ORANGE_TERRACOTTA.parseMaterial(), XMaterial.MAGENTA_TERRACOTTA.parseMaterial(),
                        XMaterial.LIGHT_BLUE_TERRACOTTA.parseMaterial(), XMaterial.YELLOW_TERRACOTTA.parseMaterial(), XMaterial.LIME_TERRACOTTA.parseMaterial(),
                        XMaterial.PINK_TERRACOTTA.parseMaterial(), XMaterial.GRAY_TERRACOTTA.parseMaterial(), XMaterial.LIGHT_GRAY_TERRACOTTA.parseMaterial(),
                        XMaterial.CYAN_TERRACOTTA.parseMaterial(), XMaterial.PURPLE_TERRACOTTA.parseMaterial(), XMaterial.BLUE_TERRACOTTA.parseMaterial(),
                        XMaterial.BROWN_TERRACOTTA.parseMaterial(), XMaterial.GREEN_TERRACOTTA.parseMaterial(), XMaterial.RED_TERRACOTTA.parseMaterial(),
                        XMaterial.BLACK_TERRACOTTA.parseMaterial());
            case "GRANITE":
                return Collections.singletonList(XMaterial.POLISHED_GRANITE.parseMaterial());
            case "ANDESITE":
                return Collections.singletonList(XMaterial.POLISHED_ANDESITE.parseMaterial());
            case "DIORITE":
                return Collections.singletonList(XMaterial.POLISHED_DIORITE.parseMaterial());
            case "DEEPSLATE":
                return Collections.singletonList(XMaterial.DEEPSLATE.parseMaterial());
            case "SANDSTONE":
                return Collections.singletonList(XMaterial.SANDSTONE.parseMaterial());
            case "RED_SANDSTONE":
                return Collections.singletonList(XMaterial.GLASS_BOTTLE.parseMaterial());
            case "END_STONE":
                return Collections.singletonList(XMaterial.ENDER_PEARL.parseMaterial());
            case "NETHERRACK":
                return Collections.singletonList(XMaterial.NETHER_BRICK.parseMaterial());
            default:
                throw new IllegalArgumentException("Material " + material + " is not valid.");
        }
    }

}
