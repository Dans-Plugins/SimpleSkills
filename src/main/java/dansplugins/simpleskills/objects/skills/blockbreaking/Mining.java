package dansplugins.simpleskills.objects.skills.blockbreaking;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.nms.NMSVersion;
import dansplugins.simpleskills.objects.abs.BlockBreakingSkill;
import dansplugins.simpleskills.objects.benefits.ResourceExtraction;
import org.bukkit.Material;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 */
public class Mining extends BlockBreakingSkill {

    String nms = NMSVersion.getNMSVersion();
    public Mining() {
        super(SupportedSkill.MINING.ordinal(), "Mining");
        initialize();
    }

    private void initialize() {
        addBenefit(new ResourceExtraction());

        HashSet<Material> materials = new HashSet<>();
        materials.add(Material.COAL_ORE);
        materials.add(Material.DIAMOND_ORE);
        materials.add(Material.EMERALD_ORE);
        materials.add(Material.GOLD_ORE);
        materials.add(Material.IRON_ORE);
        materials.add(Material.LAPIS_ORE);
        materials.add(Material.REDSTONE_ORE);
        materials.add(Material.NETHER_QUARTZ_ORE);
        if (!(nms.contains("v1_13_R1") || nms.contains("v1_13_R2") || nms.contains("v1_14_R1") || nms.contains("v1_15_R1"))) {
            materials.add(Material.NETHER_GOLD_ORE); 
        }

        if (!(nms.contains("v1_13_R1") || nms.contains("v1_13_R2") || nms.contains("v1_14_R1") || nms.contains("v1_15_R1") || nms.contains("v1_16_R1") || nms.contains("v1_16_R2") || nms.contains("v1_16_R3"))) {
            materials.add(Material.COPPER_ORE); 
            materials.add(Material.DEEPSLATE_COAL_ORE); 
            materials.add(Material.DEEPSLATE_COPPER_ORE); 
            materials.add(Material.DEEPSLATE_DIAMOND_ORE); 
            materials.add(Material.DEEPSLATE_EMERALD_ORE); 
            materials.add(Material.DEEPSLATE_GOLD_ORE); 
            materials.add(Material.DEEPSLATE_IRON_ORE); 
            materials.add(Material.DEEPSLATE_LAPIS_ORE); 
            materials.add(Material.DEEPSLATE_REDSTONE_ORE); 
        }
        super.initializeAssociatedMaterials(materials);
    }
}