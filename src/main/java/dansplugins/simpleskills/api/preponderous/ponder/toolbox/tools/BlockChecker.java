/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.Chest
 */
package preponderous.ponder.toolbox.tools;

import dansplugins.simpleskills.services.LocalConfigService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

public class BlockChecker {
    public boolean isChest(Block block) {
        return block.getType() == Material.CHEST && block.getState() instanceof Chest;
    }

    public boolean isDoor(Block block) {
        return block.getType() == Material.ACACIA_DOOR || block.getType() == Material.BIRCH_DOOR || block.getType() == Material.DARK_OAK_DOOR || block.getType() == Material.IRON_DOOR || block.getType() == Material.JUNGLE_DOOR || block.getType() == Material.OAK_DOOR || block.getType() == Material.SPRUCE_DOOR || block.getType() == this.compatMaterial("CRIMSON_DOOR") || block.getType() == this.compatMaterial("WARPED_DOOR");
    }

    public boolean isTrapdoor(Block block) {
        return block.getType() == Material.IRON_TRAPDOOR || block.getType() == Material.OAK_TRAPDOOR || block.getType() == Material.SPRUCE_TRAPDOOR || block.getType() == Material.BIRCH_TRAPDOOR || block.getType() == Material.JUNGLE_TRAPDOOR || block.getType() == Material.ACACIA_TRAPDOOR || block.getType() == Material.DARK_OAK_TRAPDOOR || block.getType() == this.compatMaterial("CRIMSON_TRAPDOOR") || block.getType() == this.compatMaterial("WARPED_TRAPDOOR");
    }

    public boolean islegacyFurnace(Block block) {
        return block.getType() == Material.FURNACE;
    }
    public boolean isAnvil(Block block) {
        return block.getType() == Material.ANVIL || block.getType() == Material.CHIPPED_ANVIL || block.getType() == Material.DAMAGED_ANVIL;
    }

    public boolean isGate(Block block) {
        return block.getType() == Material.OAK_FENCE_GATE || block.getType() == Material.SPRUCE_FENCE_GATE || block.getType() == Material.BIRCH_FENCE_GATE || block.getType() == Material.JUNGLE_FENCE_GATE || block.getType() == Material.ACACIA_FENCE_GATE || block.getType() == Material.DARK_OAK_FENCE_GATE || block.getType() == this.compatMaterial("CRIMSON_FENCE_GATE") || block.getType() == this.compatMaterial("WARPED_FENCE_GATE");
    }
    public boolean isBarrel(Block block) {
        return block.getType() == Material.BARREL; //TODO 1.14
    }

    public boolean isFurnace(Block block) {
        return block.getType() == Material.BLAST_FURNACE; //TODO 1.14
    }


    public Material compatMaterial(String materialName) {
        Material mat = Material.getMaterial((String)materialName);
        if (mat == null) {
            switch (materialName) {
                case "CRIMSON_FENCE_GATE": {
                    return Material.OAK_FENCE_GATE;
                }
                case "WARPED_FENCE_GATE": {
                    return Material.OAK_FENCE_GATE;
                }
                case "CRIMSON_DOOR": {
                    return Material.OAK_DOOR;
                }
                case "WARPED_DOOR": {
                    return Material.OAK_DOOR;
                }
                case "CRIMSON_TRAPDOOR": {
                    return Material.OAK_TRAPDOOR;
                }
                case "WARPED_TRAPDOOR": {
                    return Material.OAK_TRAPDOOR;
                }
            }
            Bukkit.getLogger().info("ERROR: Could not locate a compatible material matching '" + materialName + "'.");
            return null;
        }
        return mat;
    }
}

