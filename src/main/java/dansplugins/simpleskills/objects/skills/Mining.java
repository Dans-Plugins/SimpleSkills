package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.objects.skills.abs.Skill;
import org.bukkit.Material;

import java.util.HashSet;

public class Mining extends Skill {
    public Mining(int ID) {
        super(ID, "Mining", 100);
        initialize();
    }

    private void initialize() {
        HashSet<Material> materials = new HashSet<>();
        materials.add(Material.COAL_ORE);
        materials.add(Material.COPPER_ORE);
        materials.add(Material.DIAMOND_ORE);
        materials.add(Material.EMERALD_ORE);
        materials.add(Material.GOLD_ORE);
        materials.add(Material.IRON_ORE);
        materials.add(Material.LAPIS_ORE);
        materials.add(Material.REDSTONE_ORE);
        materials.add(Material.DEEPSLATE_COAL_ORE);
        materials.add(Material.DEEPSLATE_COPPER_ORE);
        materials.add(Material.DEEPSLATE_DIAMOND_ORE);
        materials.add(Material.DEEPSLATE_EMERALD_ORE);
        materials.add(Material.DEEPSLATE_GOLD_ORE);
        materials.add(Material.DEEPSLATE_IRON_ORE);
        materials.add(Material.DEEPSLATE_LAPIS_ORE);
        materials.add(Material.DEEPSLATE_REDSTONE_ORE);
        materials.add(Material.NETHER_GOLD_ORE);
        materials.add(Material.NETHER_QUARTZ_ORE);
        super.initializeAssociatedMaterials(materials);
    }
}