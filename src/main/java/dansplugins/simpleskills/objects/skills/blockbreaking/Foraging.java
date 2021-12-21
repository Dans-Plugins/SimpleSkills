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
public class Foraging extends BlockBreakingSkill {

    String nms = NMSVersion.getNMSVersion();
    public Foraging() {
        super(SupportedSkill.FORAGING.ordinal(), "Foraging");
        initialize();
    }

    private void initialize() {
        addBenefit(new ResourceExtraction());

        HashSet<Material> materials = new HashSet<>();

        materials.add(Material.CACTUS);
        materials.add(Material.SEA_PICKLE);
        if (!(nms.contains("v1_13_R1") || nms.contains("v1_13_R2"))) {
            materials.add(Material.SWEET_BERRIES); 
        }
        materials.add(Material.BROWN_MUSHROOM);
        materials.add(Material.RED_MUSHROOM);
        materials.add(Material.KELP_PLANT);
        if (!(nms.contains("v1_13_R1") || nms.contains("v1_13_R2") || nms.contains("v1_14_R1") || nms.contains("v1_15_R1"))) {
            materials.add(Material.CRIMSON_FUNGUS); 
            materials.add(Material.WARPED_FUNGUS); 
        }
        if (!(nms.contains("v1_13_R1") || nms.contains("v1_13_R2") || nms.contains("v1_14_R1") || nms.contains("v1_15_R1") || nms.contains("v1_16_R1") || nms.contains("v1_16_R2") || nms.contains("v1_16_R3"))) {
            materials.add(Material.GLOW_BERRIES); 
        }
        super.initializeAssociatedMaterials(materials);
    }
}