package dansplugins.simpleskills.skill.skills;

import com.cryptomorin.xseries.XMaterial;

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
 * @since 05/01/2022 - 23:55
 */
public class Digging extends AbstractBlockSkill {
    private final ChanceCalculator chanceCalculator;

    /**
     * Variable used for the determination of random reward chance.
     */
    private static final double scalar = 0.2;

    /**
     * The Digging skill is incremented by mining sand, gravel, dirt etc. etc.
     */
    public Digging(ConfigService configService, Log log, PlayerRecordRepository playerRecordRepository, SimpleSkills simpleSkills, MessageService messageService, ChanceCalculator chanceCalculator) {
        super(configService, log, playerRecordRepository, simpleSkills, messageService, "Digging");
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
        if (chanceCalculator.roll(record, this, 0.10)) {
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
            // Randomly choose between three possible benefits: double drops, special items, or experience
            final Random random = new Random();
            final int benefitType = random.nextInt(3); // 0, 1, or 2
            
            if (benefitType == 0) {
                // Double drops
                final Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
                drops.forEach(drop -> {
                    if (drop.getType().isAir()) return;
                    block.getWorld().dropItemNaturally(block.getLocation(), drop);
                });
                player.sendMessage(messageService.convert(Objects.requireNonNull(messageService.getlang().getString("Skills.Digging.DoubleDrop"))
                        .replaceAll("%type%", WordUtils.capitalizeFully(block.getType().name().replaceAll("_", " ").toLowerCase()))));
            } else if (benefitType == 1) {
                // Special item drops
                final List<Material> rewardTypes = getRewardTypes(block.getType());
                final Material reward = rewardTypes.get(random.nextInt(rewardTypes.size()));
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(reward));
                player.sendMessage(messageService.convert(Objects.requireNonNull(messageService.getlang().getString("Skills.Digging.Special"))));
            } else {
                // Experience orbs
                final ExperienceOrb entity = (ExperienceOrb) block.getWorld()
                        .spawnEntity(block.getLocation(), EntityType.EXPERIENCE_ORB);
                final int exp = random.nextInt(5) + 1; // 1-5 experience points
                entity.setExperience(exp);
                entity.setGlowing(true);
                final String expMessage = messageService.getlang().getString("Skills.Digging.Exp");
                player.sendMessage(messageService.convert(Objects.requireNonNull(expMessage)
                        .replaceAll("%exp%", String.valueOf(exp))));
            }
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
