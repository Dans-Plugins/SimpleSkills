package dansplugins.simpleskills.api.preponderous.ponder.toolbox.tools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

import static org.bukkit.Bukkit.getLogger;

/**
 * @author Daniel Stephenson
 */
public class BlockChecker {

    /**
     * Method to check whether or not the block is a chest.
     *
     * @return A {@link boolean} signifying whether or not the block is a chest.
     */
    public boolean isChest(Block block) {
        return block.getType() == Material.CHEST && block.getState() instanceof Chest;
    }

    /**
     * Method to check whether or not the block is a door.
     *
     * @return A {@link boolean} signifying whether or not the block is a door.
     */
    public boolean isDoor(Block block) {
        if (block.getType() == Material.ACACIA_DOOR ||
                block.getType() == Material.BIRCH_DOOR ||
                block.getType() == Material.DARK_OAK_DOOR ||
                block.getType() == Material.IRON_DOOR ||
                block.getType() == Material.JUNGLE_DOOR ||
                block.getType() == Material.OAK_DOOR ||
                block.getType() == Material.SPRUCE_DOOR ||
                block.getType() == compatMaterial("CRIMSON_DOOR") ||
                block.getType() == compatMaterial("WARPED_DOOR")) {

            return true;

        }
        return false;
    }

    /**
     * Method to check whether or not the block is a trapdoor.
     *
     * @return A {@link boolean} signifying whether or not the block is a trapdoor.
     */
    public boolean isTrapdoor(Block block) {
        if (block.getType() == Material.IRON_TRAPDOOR ||
                block.getType() == Material.OAK_TRAPDOOR ||
                block.getType() == Material.SPRUCE_TRAPDOOR ||
                block.getType() == Material.BIRCH_TRAPDOOR ||
                block.getType() == Material.JUNGLE_TRAPDOOR ||
                block.getType() == Material.ACACIA_TRAPDOOR ||
                block.getType() == Material.DARK_OAK_TRAPDOOR ||
                block.getType() == compatMaterial("CRIMSON_TRAPDOOR") ||
                block.getType() == compatMaterial("WARPED_TRAPDOOR")) {
            return true;
        }
        return false;
    }

    /**
     * Method to check whether or not the block is a furnace.
     *
     * @return A {@link boolean} signifying whether or not the block is a furnace.
     */
    public boolean isFurnace(Block block) {
        if (block.getType() == Material.FURNACE ||
                block.getType() == Material.BLAST_FURNACE) {
            return true;
        }
        return false;
    }

    /**
     * Method to check whether or not the block is an anvil.
     *
     * @return A {@link boolean} signifying whether or not the block is an anvil.
     */
    public boolean isAnvil(Block block) {
        if (block.getType() == Material.ANVIL ||
                block.getType() == Material.CHIPPED_ANVIL ||
                block.getType() == Material.DAMAGED_ANVIL) {
            return true;
        }
        return false;
    }

    /**
     * Method to check whether or not the block is a gate.
     *
     * @return A {@link boolean} signifying whether or not the block is a gate.
     */
    public boolean isGate(Block block) {
        if (block.getType() == Material.OAK_FENCE_GATE ||
                block.getType() == Material.SPRUCE_FENCE_GATE ||
                block.getType() == Material.BIRCH_FENCE_GATE ||
                block.getType() == Material.JUNGLE_FENCE_GATE ||
                block.getType() == Material.ACACIA_FENCE_GATE ||
                block.getType() == Material.DARK_OAK_FENCE_GATE ||
                block.getType() == compatMaterial("CRIMSON_FENCE_GATE") ||
                block.getType() == compatMaterial("WARPED_FENCE_GATE")) {
            return true;
        }
        return false;
    }

    /**
     * Method to check whether or not the block is an anvil.
     *
     * @return A {@link boolean} signifying whether or not the block is an anvil.
     */
    public boolean isBarrel(Block block) {
        if (block.getType() == Material.BARREL) {
            return true;
        }
        return false;
    }

    /**
     * Method to get a Material by name.
     *
     * @return A {@link Material} matching the name.
     */
    public Material compatMaterial(String materialName) {
        Material mat = Material.getMaterial(materialName);
        if (mat == null) {
            // Find compatible substitute.
            switch(materialName) {
                case "CRIMSON_FENCE_GATE":
                    return Material.OAK_FENCE_GATE;
                case "WARPED_FENCE_GATE":
                    return Material.OAK_FENCE_GATE;
                case "CRIMSON_DOOR":
                    return Material.OAK_DOOR;
                case "WARPED_DOOR":
                    return Material.OAK_DOOR;
                case "CRIMSON_TRAPDOOR":
                    return Material.OAK_TRAPDOOR;
                case "WARPED_TRAPDOOR":
                    return Material.OAK_TRAPDOOR;
                default:
                    getLogger().info("ERROR: Could not locate a compatible material matching '" + materialName + "'.");
                    return null;
            }
        }
        else {
            return mat;
        }
    }

}
