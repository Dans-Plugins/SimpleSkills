package dansplugins.simpleskills.objects.skills;

import org.bukkit.Material;

import java.util.HashSet;

public class Woodcutting extends Skill {

    public Woodcutting() {
        super(0, "Woodcutting", 100);
        initialize();
    }

    private void initialize() {
        HashSet<Material> woodcuttingMaterials = new HashSet<>();
        woodcuttingMaterials.add(Material.ACACIA_WOOD);
        woodcuttingMaterials.add(Material.BIRCH_WOOD);
        woodcuttingMaterials.add(Material.DARK_OAK_WOOD);
        woodcuttingMaterials.add(Material.JUNGLE_WOOD);
        woodcuttingMaterials.add(Material.OAK_WOOD);
        woodcuttingMaterials.add(Material.SPRUCE_WOOD);
        woodcuttingMaterials.add(Material.STRIPPED_ACACIA_WOOD);
        woodcuttingMaterials.add(Material.STRIPPED_BIRCH_WOOD);
        woodcuttingMaterials.add(Material.STRIPPED_DARK_OAK_WOOD);
        woodcuttingMaterials.add(Material.STRIPPED_JUNGLE_WOOD);
        woodcuttingMaterials.add(Material.STRIPPED_OAK_WOOD);
        woodcuttingMaterials.add(Material.STRIPPED_SPRUCE_WOOD);
        super.initializeAssociatedMaterials(woodcuttingMaterials);
    }
}
