package dansplugins.simpleskills.objects.skills.blockbreaking;

import dansplugins.simpleskills.nms.NMSVersion;
import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.BlockBreakingSkill;
import dansplugins.simpleskills.objects.benefits.ResourceExtraction;
import org.bukkit.Material;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 */

public class Floriculture extends BlockBreakingSkill {

    String nms = NMSVersion.getNMSVersion();
    public Floriculture() {
        super(SupportedSkill.FLORICULTURE.ordinal(), "Floriculture");
        initialize();
    }

    private void initialize() {
        addBenefit(new ResourceExtraction());

        HashSet<Material> materials = new HashSet<>();
        materials.add(Material.DANDELION);
        materials.add(Material.POPPY);
        materials.add(Material.BLUE_ORCHID);
        materials.add(Material.ALLIUM);
        materials.add(Material.AZURE_BLUET);
        materials.add(Material.ORANGE_TULIP);
        materials.add(Material.PINK_TULIP);
        materials.add(Material.RED_TULIP);
        materials.add(Material.WHITE_TULIP);
        materials.add(Material.OXEYE_DAISY);
        materials.add(Material.SUNFLOWER);
        materials.add(Material.LILAC);
        materials.add(Material.ROSE_BUSH);
        materials.add(Material.PEONY);
        if (!(nms.contains("v1_13_R1") || nms.contains("v1_13_R2"))) {
            materials.add(Material.CORNFLOWER);
            materials.add(Material.LILY_OF_THE_VALLEY);
            materials.add(Material.WITHER_ROSE);
        }
        super.initializeAssociatedMaterials(materials);
    }
}