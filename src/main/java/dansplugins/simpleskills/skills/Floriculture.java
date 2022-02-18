package dansplugins.simpleskills.skills;

import com.cryptomorin.xseries.XMaterial;
import dansplugins.simpleskills.AbstractBlockSkill;
import dansplugins.simpleskills.data.PlayerRecord;
import dansplugins.simpleskills.utils.ChanceCalculator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Callum Johnson
 * @since 07/01/2022 - 22:31
 */
public class Floriculture extends AbstractBlockSkill {

    /**
     * Floriculture is levelled up via mining flowers.
     */
    public Floriculture() {
        super("Floriculture");
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
        if (context.equals("playerinteractevent")) {
            return targetBlock.getType() == XMaterial.FLOWER_POT.parseMaterial() && isValidMaterial(item.getType())
                    && item.getType() != XMaterial.FLOWER_POT.parseMaterial();
        } else if (context.equals("blockbreakevent")) {
            return true;
        } else {
            return isValidMaterial(item.getType());
        }
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
        return BlockSkillType.RIGHT_OR_BREAK_OR_PLACE;
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
            case "DANDELION":
            case "POPPY":
            case "BLUE_ORCHID":
            case "ALLIUM":
            case "AZURE_BLUET":
            case "ORANGE_TULIP":
            case "PINK_TULIP":
            case "RED_TULIP":
            case "WHITE_TULIP":
            case "OXEYE_DAISY":
            case "SUNFLOWER":
            case "LILAC":
            case "ROSE_BUSH":
            case "PEONY":
            case "CORNFLOWER":
            case "LILY_OF_THE_VALLEY":
            case "WITHER_ROSE":
            case "FLOWER_POT":
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
        if (!ChanceCalculator.getInstance().roll(record, this, 0.10)) return;
        player.playSound(block.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Objects.requireNonNull(XMaterial.BONE_MEAL.parseItem())));
        player.sendMessage("§bYou got bone meal, make more flowers!");
    }

}
