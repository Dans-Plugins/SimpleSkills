package dansplugins.simpleskills;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author Callum Johnson
 * @since 05/01/2022 - 15:41
 */
public abstract class AbstractBlockSkill extends AbstractSkill {

    /**
     * The overhead Block Skill abstraction class.
     * <p>
     * This class is an extension of the {@link AbstractSkill} class, providing
     * specific functionality to mining blocks and linking them to experience
     * gain by its material.
     * </p>
     *
     * @param name of the skill.
     */
    public AbstractBlockSkill(@NotNull String name) {
        super(name, BlockBreakEvent.class, BlockPlaceEvent.class, PlayerInteractEvent.class);
    }

    /**
     * Breaking mechanism.
     *
     * @param event to handle.
     */
    public void handle(@NotNull BlockBreakEvent event) {
        if (!event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) return;
        if (isItemRequired()) {
            if (!isRequiredItem(event.getPlayer().getInventory().getItemInMainHand(),
                    event.getBlock(), event.getClass().getSimpleName().toLowerCase())) return;
        }
        if (getBlockSkillType() != BlockSkillType.BREAK_SPECIFIC &&
                getBlockSkillType() != BlockSkillType.BREAK_OR_PLACE &&
                getBlockSkillType() != BlockSkillType.LEFT_OR_BREAK_OR_PLACE &&
                getBlockSkillType() != BlockSkillType.RIGHT_OR_BREAK_OR_PLACE) return;
        if (!isValidMaterial(event.getBlock().getType())) return;
        incrementExperience(event.getPlayer());
        executeReward(event.getPlayer(), event.getBlock(), event);
    }

    /**
     * Placing mechanism.
     *
     * @param event to handle.
     */
    public void handle(@NotNull BlockPlaceEvent event) {
        if (!event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) return;
        if (getBlockSkillType() != BlockSkillType.PLACE_SPECIFIC &&
                getBlockSkillType() != BlockSkillType.BREAK_OR_PLACE &&
                getBlockSkillType() != BlockSkillType.LEFT_OR_BREAK_OR_PLACE &&
                getBlockSkillType() != BlockSkillType.RIGHT_OR_BREAK_OR_PLACE) return;
        if (!isValidMaterial(event.getBlock().getType())) return;
        if (isItemRequired()) {
            if (!isRequiredItem(event.getPlayer().getInventory().getItemInMainHand(),
                    event.getBlock(), event.getClass().getSimpleName().toLowerCase())) {
                return;
            }
        }
        incrementExperience(event.getPlayer());
        executeReward(event.getPlayer(), event.getBlock(), event);
    }

    /**
     * Clicking mechanism.
     *
     * @param event to handle.
     */
    public void handle(@NotNull PlayerInteractEvent event) {
        if (!event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) return;
        if (event.getClickedBlock() == null) return;
        if (!isValidMaterial(event.getClickedBlock().getType())) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction().name().contains("AIR")) return;
        if (isItemRequired()) if (!isRequiredItem(event.getPlayer().getInventory().getItemInMainHand(),
                event.getClickedBlock(), event.getClass().getSimpleName().toLowerCase())) return;
        if ((getBlockSkillType() == BlockSkillType.LEFT_OR_BREAK_OR_PLACE ||
                getBlockSkillType() == BlockSkillType.LEFT_CLICK_SPECIFIC) &&
                event.getAction() == Action.LEFT_CLICK_BLOCK) {
            incrementExperience(event.getPlayer());
            executeReward(event.getPlayer(), event.getClickedBlock(), event);
        } else if ((getBlockSkillType() == BlockSkillType.RIGHT_OR_BREAK_OR_PLACE ||
                getBlockSkillType() == BlockSkillType.RIGHT_CLICK_SPECIFIC) &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            incrementExperience(event.getPlayer());
            executeReward(event.getPlayer(), event.getClickedBlock(), event);
        } else {
            if (getBlockSkillType() == BlockSkillType.LEFT_OR_RIGHT_CLICK) {
                incrementExperience(event.getPlayer());
                executeReward(event.getPlayer(), event.getClickedBlock(), event);
            }
        }
    }

    /**
     * Method to determine if the item provided is valid.
     *
     * @param item to check.
     * @param targetBlock to do sub-checks with.
     * @param context of which the event happened.
     * @return {@code true} if it is.
     */
    public abstract boolean isRequiredItem(@NotNull ItemStack item, @NotNull Block targetBlock, @NotNull String context);

    /**
     * Method to determine if an item is required to be in the hand of the participant.
     *
     * @return {@code true} if it is.
     */
    public abstract boolean isItemRequired();

    /**
     * Method to determine if the Block skill exclusivity.
     *
     * @return {@link BlockSkillType} for this skill.
     */
    @NotNull
    public abstract BlockSkillType getBlockSkillType();

    /**
     * Method to determine if the Block material is valid for this Skill.
     *
     * @param material to test.
     * @return {@code true} if it is.
     */
    public abstract boolean isValidMaterial(@NotNull Material material);

    /**
     * @author Callum Johnson
     * @since 05/01/2022 - 15:51
     */
    public enum BlockSkillType {

        /**
         * Skill is block-placing specific.
         */
        PLACE_SPECIFIC,

        /**
         * Skill is block-breaking specific.
         */
        BREAK_SPECIFIC,

        /**
         * Skill is block-left-click specific.
         */
        LEFT_CLICK_SPECIFIC,

        /**
         * Skill is block-right-click specific.
         */
        RIGHT_CLICK_SPECIFIC,

        /**
         * Skill is block-break or block-break specific.
         */
        BREAK_OR_PLACE,

        /**
         * Skill is block-click specific (Both left and right click).
         */
        LEFT_OR_RIGHT_CLICK,

        /**
         * Skill is left-click, break or place specific.
         */
        LEFT_OR_BREAK_OR_PLACE,

        /**
         * Skill is right-click, break or place specific.
         */
        RIGHT_OR_BREAK_OR_PLACE;

    }

}
