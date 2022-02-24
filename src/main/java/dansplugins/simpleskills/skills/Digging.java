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
 * @since 05/01/2022 - 23:55
 */
public class Digging extends AbstractBlockSkill {

    /**
     * Variable used for the determination of random reward chance.
     */
    private static final double scalar = 0.2;

    /**
     * The Digging skill is incremented by mining sand, gravel, dirt etc. etc.
     */
    public Digging() {
        super("Digging");
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
        return item.getType().name().contains("SHOVEL") || item.getType().isAir();
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
            case "GRASS_BLOCK":
            case "DIRT":
            case "GRAVEL":
            case "SAND":
            case "RED_SAND":
            case "SOUL_SAND":
            case "COARSE_DIRT":
            case "ROOTED_DIRT":
            case "PODZOL":
            case "MYCELIUM":
            case "MOSS_BLOCK":
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
                player.sendMessage(LocalMessageService.getInstance().convert(Objects.requireNonNull(LocalMessageService.getInstance().getlang().getString("Skills.Digging.DoubleDrop"))
                        .replaceAll("%type%", WordUtils.capitalizeFully(block.getType().name().replaceAll("_", " ").toLowerCase()))));
                return;
            }
            final List<Material> rewardTypes = getRewardTypes(block.getType());
            final Material reward = rewardTypes.get(new Random().nextInt(rewardTypes.size()));
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(reward));
            player.sendMessage(LocalMessageService.getInstance().convert(Objects.requireNonNull(LocalMessageService.getInstance().getlang().getString("Skills.Digging.Special"))));
        }
    }

    @NotNull
    private List<Material> getRewardTypes(@NotNull Material material) {
        switch (material.name()) {
            case "GRASS_BLOCK":
                return Collections.singletonList(XMaterial.GRASS_BLOCK.parseMaterial());
            case "DIRT":
                return Collections.singletonList(XMaterial.DIRT.parseMaterial());
            case "COARSE_DIRT":
                return Collections.singletonList(XMaterial.COARSE_DIRT.parseMaterial());
            case "ROOTED_DIRT":
                return Collections.singletonList(XMaterial.ROOTED_DIRT.parseMaterial());
            case "RED_SAND":
                return Arrays.asList(XMaterial.IRON_NUGGET.parseMaterial(), XMaterial.RAW_COPPER.parseMaterial());
            case "SAND":
                return Arrays.asList(XMaterial.IRON_NUGGET.parseMaterial(), XMaterial.GOLD_NUGGET.parseMaterial());
            case "GRAVEL":
                return Collections.singletonList(XMaterial.FLINT.parseMaterial());
            case "SOUL_SAND":
                return Collections.singletonList(XMaterial.CHARCOAL.parseMaterial());
            case "PODZOL":
                return Collections.singletonList(XMaterial.COAL.parseMaterial());
            case "MYCELIUM":
                return Arrays.asList(XMaterial.BROWN_MUSHROOM.parseMaterial(), XMaterial.RED_MUSHROOM.parseMaterial());
            case "MOSS_BLOCK":
                return Arrays.asList(XMaterial.BEETROOT_SEEDS.parseMaterial(), XMaterial.MELON_SEEDS.parseMaterial(), XMaterial.PUMPKIN_SEEDS.parseMaterial(), XMaterial.WHEAT_SEEDS.parseMaterial());
            default:
                throw new IllegalArgumentException("Material " + material + " is not valid.");
        }
    }

}
