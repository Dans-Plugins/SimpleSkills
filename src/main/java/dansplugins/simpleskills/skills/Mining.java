package dansplugins.simpleskills.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.data.objects.PlayerRecord;
import dansplugins.simpleskills.services.ConfigService;
import dansplugins.simpleskills.services.MessageService;
import dansplugins.simpleskills.skills.abs.AbstractBlockSkill;
import dansplugins.simpleskills.utils.ChanceCalculator;
import dansplugins.simpleskills.utils.Logger;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author Callum Johnson
 * @since 06/01/2022 - 00:59
 */
public class Mining extends AbstractBlockSkill {
    private final ChanceCalculator chanceCalculator;

    /**
     * The mining skill is levelled up by mining ore.
     */
    public Mining(ConfigService configService, Logger logger, PersistentData persistentData, SimpleSkills simpleSkills, MessageService messageService, ChanceCalculator chanceCalculator) {
        super(configService, logger, persistentData, simpleSkills, messageService, "Mining");
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
        return material.name().contains("ORE");
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
            final Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
            while (recipeIterator.hasNext()) {
                final Recipe recipe = recipeIterator.next();
                if (!(recipe instanceof FurnaceRecipe)) continue;
                final FurnaceRecipe furnaceRecipe = (FurnaceRecipe) recipe;
                if (furnaceRecipe.getInput().getType().equals(block.getType()) ||
                        furnaceRecipe.getInputChoice().test(new ItemStack(block.getType()))) {
                    final ItemStack result = furnaceRecipe.getResult();
                    if (block.getDrops().contains(result)) continue;
                    block.getWorld().dropItemNaturally(block.getLocation(), result);
                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
                    player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 5, 2);
                    player.sendMessage(messageService.convert(Objects.requireNonNull(messageService.getlang().getString("Skills.Mining.Smelt"))));
                    return;
                }
            }
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
            final Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
            drops.forEach(drop -> {
                if (drop.getType().isAir()) return;
                block.getWorld().dropItemNaturally(block.getLocation(), drop);
            });
            player.sendMessage(messageService.convert(Objects.requireNonNull(Objects.requireNonNull(messageService.getlang().getString("Skills.Mining.Drop"))
                    .replaceAll("%item%", WordUtils.capitalizeFully(block.getType().name()
                            .replaceAll("_", " ").toLowerCase())))));
        }
    }

}
