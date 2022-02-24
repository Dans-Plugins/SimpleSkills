package dansplugins.simpleskills.skills;

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

import java.util.Collection;
import java.util.Objects;

/**
 * @author Callum Johnson
 * @since 05/01/2022 - 13:55
 */
public class Woodcutting extends AbstractBlockSkill {

    /**
     * Variable used for the determination of random reward chance.
     */
    private static final double scalar = 0.2;

    /**
     * The Woodcutting skill is a skill where players mine Logs/Wood to gain experience.
     */
    public Woodcutting() {
        super("lumberjack");
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
            final Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
            drops.forEach(drop -> {
                if (drop.getType().isAir()) return;
                block.getWorld().dropItemNaturally(block.getLocation(), drop);
            });
            player.sendMessage(LocalMessageService.getInstance().convert(Objects.requireNonNull(Objects.requireNonNull(LocalMessageService.getInstance().getlang().getString("Skills.WoodCutting"))
                    .replaceAll("%item%", WordUtils.capitalizeFully(block.getType()
                            .name().replaceAll("_", " ").toLowerCase())))));
        }
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
        return item.getType().name().contains("_AXE") || item.getType().isAir();
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
        final String name = material.name();
        return name.contains("WOOD") || name.contains("LOG");
    }

}
