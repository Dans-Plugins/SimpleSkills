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
public class Harvesting extends BlockBreakingSkill {

    String nms = NMSVersion.getNMSVersion();
    public Harvesting() {
        super(SupportedSkill.HARVESTING.ordinal(), "Harvesting");
        initialize();
    }

    private void initialize() {
        addBenefit(new ResourceExtraction());

        HashSet<Material> materials = new HashSet<>();
        materials.add(Material.WHEAT);
        materials.add(Material.BEETROOT);
        materials.add(Material.CARROT);
        materials.add(Material.POTATO);
        materials.add(Material.MELON);
        materials.add(Material.PUMPKIN);
        materials.add(Material.COCOA_BEANS);
        materials.add(Material.SUGAR_CANE);
        materials.add(Material.CHORUS_FRUIT);
        if (!(nms.contains("v1_13_R1") || nms.contains("v1_13_R2"))) {
            materials.add(Material.BAMBOO); 
            materials.add(Material.SWEET_BERRIES); 
        }
        super.initializeAssociatedMaterials(materials);
    }
}