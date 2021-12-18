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
public class Quarrying extends BlockBreakingSkill {
    String nms = NMSVersion.getNMSVersion();

    public Quarrying() {
        super(SupportedSkill.QUARRYING.ordinal(), "Quarrying");
        initialize();
    }

    private void initialize() {
        addBenefit(new ResourceExtraction());

        HashSet<Material> materials = new HashSet<>();
        materials.add(Material.STONE);
        materials.add(Material.ANDESITE);
        materials.add(Material.DIORITE);
        materials.add(Material.GRANITE);
        materials.add(Material.SANDSTONE);
        materials.add(Material.TERRACOTTA);
        if (!(nms.contains("v1_13_R1") || nms.contains("v1_13_R2") || nms.contains("v1_14_R1") || nms.contains("v1_15_R1") || nms.contains("v1_16_R1") || nms.contains("v1_16_R2") || nms.contains("v1_16_R3"))) {
            materials.add(Material.DEEPSLATE);
        }
        super.initializeAssociatedMaterials(materials);
    }
}
