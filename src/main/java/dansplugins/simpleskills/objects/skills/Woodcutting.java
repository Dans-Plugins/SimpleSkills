package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.objects.skills.abs.BlockBreakingSkill;
import dansplugins.simpleskills.objects.skills.abs.Skill;
import org.bukkit.Material;

import java.util.HashSet;

public class Woodcutting extends BlockBreakingSkill {
    public Woodcutting(int ID) {
        super(ID, "Woodcutting", 100, 10, 2);
        initialize();
    }

    private void initialize() {
        HashSet<Material> materials = new HashSet<>();
        materials.add(Material.ACACIA_LOG);
        materials.add(Material.BIRCH_LOG);
        materials.add(Material.DARK_OAK_LOG);
        materials.add(Material.JUNGLE_LOG);
        materials.add(Material.OAK_LOG);
        materials.add(Material.SPRUCE_LOG);
        materials.add(Material.STRIPPED_ACACIA_LOG);
        materials.add(Material.STRIPPED_BIRCH_LOG);
        materials.add(Material.STRIPPED_DARK_OAK_LOG);
        materials.add(Material.STRIPPED_JUNGLE_LOG);
        materials.add(Material.STRIPPED_OAK_LOG);
        materials.add(Material.STRIPPED_SPRUCE_LOG);
        materials.add(Material.ACACIA_WOOD);
        materials.add(Material.BIRCH_WOOD);
        materials.add(Material.DARK_OAK_WOOD);
        materials.add(Material.JUNGLE_WOOD);
        materials.add(Material.OAK_WOOD);
        materials.add(Material.SPRUCE_WOOD);
        materials.add(Material.STRIPPED_ACACIA_WOOD);
        materials.add(Material.STRIPPED_BIRCH_WOOD);
        materials.add(Material.STRIPPED_DARK_OAK_WOOD);
        materials.add(Material.STRIPPED_JUNGLE_WOOD);
        materials.add(Material.STRIPPED_OAK_WOOD);
        materials.add(Material.STRIPPED_SPRUCE_WOOD);
        super.initializeAssociatedMaterials(materials);
    }
}
