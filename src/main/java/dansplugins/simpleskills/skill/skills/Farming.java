package dansplugins.simpleskills.skill.skills;

import com.cryptomorin.xseries.XMaterial;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.abs.AbstractBlockSkill;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.logging.Logger;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

/**
 * @author Callum Johnson
 * @since 09/01/2022 - 15:23
 */
public class Farming extends AbstractBlockSkill {
    private final ChanceCalculator chanceCalculator;

    /**
     * The farming skill is levelled by... farming!
     */
    public Farming(ConfigService configService, Logger logger, PlayerRecordRepository playerRecordRepository, SimpleSkills simpleSkills, MessageService messageService, ChanceCalculator chanceCalculator) {
        super(configService, logger, playerRecordRepository, simpleSkills, messageService, "Farming");
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
        if (context.contains("break")) return true;
        else return isValidMaterial(item.getType()) &&
                targetBlock.getRelative(BlockFace.DOWN).getType().equals(XMaterial.FARMLAND.parseMaterial()) &&
                item.getType() != XMaterial.FARMLAND.parseMaterial();
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
        return BlockSkillType.BREAK_OR_PLACE;
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
            case "WHEAT":
            case "WHEAT_SEEDS":
            case "BEETROOT":
            case "BEETROOT_SEEDS":
            case "CARROT":
            case "CARROTS":
            case "POTATO":
            case "POTATOES":
            case "MELON_SEEDS":
            case "PUMPKIN_SEEDS":
            case "COCOA_BEANS":
            case "SUGAR_CANE":
            case "CHORUS_FLOWER":
            case "CHORUS_PLANT":
            case "CHORUS_FRUIT":
            case "BAMBOO":
            case "SWEET_BERRY_BUSH":
            case "CACTUS":
            case "SEA_PICKLE":
            case "BROWN_MUSHROOM":
            case "RED_MUSHROOM":
            case "CRIMSON_FUNGUS":
            case "WARPED_FUNGUS":
            case "GLOW_BERRIES":
            case "FARMLAND":
            case "KELP":
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
        final Object eventData = skillData[1];
        if (!(blockData instanceof Block)) throw new IllegalArgumentException("SkillData[0] is not Block");
        if (!(eventData instanceof Event)) throw new IllegalArgumentException("SkillData[1] is not Event");
        final PlayerRecord record = getRecord(player);
        if (record == null) return;
        final Block block = (Block) blockData;
        final Event event = (Event) eventData;
        if (!chanceCalculator.roll(record, this, 0.10)) return;
        if (event instanceof BlockBreakEvent) {
            if (block.getType().equals(XMaterial.FARMLAND.parseMaterial())) return;
            final BlockState state = block.getState();
            if (state.getBlockData() instanceof Ageable) {
                final Ageable cropState = (Ageable) state.getBlockData();
                if (cropState.getMaximumAge() == cropState.getAge()) {
                    player.playSound(block.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
                    ((BlockBreakEvent) event).setCancelled(true);
                    final Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand(), player);
                    drops.forEach(drop -> block.getWorld().dropItemNaturally(block.getLocation(), drop));
                    player.playSound(block.getLocation(), Sound.ITEM_CROP_PLANT, 5, 2);
                    player.sendMessage(messageService.convert(Objects.requireNonNull(Objects.requireNonNull(messageService.getlang().getString("Skills.Farming.Replant"))
                            .replaceAll("%block%", block.getType().name().replaceAll("_", " ").toLowerCase()))));
                    cropState.setAge(0);
                    block.setBlockData(cropState);
                }
                return;
            }
        } else if (event instanceof BlockPlaceEvent) {
            player.playSound(block.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
            final ExperienceOrb entity = (ExperienceOrb) block.getWorld()
                    .spawnEntity(block.getLocation(), EntityType.EXPERIENCE_ORB);
            final int exp = (int) (Math.random() * 10.0);
            entity.setExperience(exp);
            entity.setGlowing(true);
            player.sendMessage(messageService.convert(Objects.requireNonNull(Objects.requireNonNull(messageService.getlang().getString("Skills.Farming.Exp"))
                    .replaceAll("%exp%", String.valueOf(exp)))));
            return;
        }
        player.playSound(block.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
        final Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand(), player);
        drops.forEach(drop -> {
            if (drop.getType().isAir()) return;
            block.getWorld().dropItemNaturally(block.getLocation(), drop);
        });
        player.sendMessage(messageService.convert(Objects.requireNonNull(messageService.getlang().getString("Skills.Farming.DoubleCrop"))));
    }

}
