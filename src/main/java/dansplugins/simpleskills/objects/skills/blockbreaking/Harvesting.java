package dansplugins.simpleskills.objects.skills.blockbreaking;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.BlockBreakingSkill;
import dansplugins.simpleskills.objects.benefits.ResourceExtraction;
import dansplugins.simpleskills.services.LocalConfigService;
import org.bukkit.Material;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 */
public class Harvesting extends BlockBreakingSkill {
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
        if (LocalConfigService.getInstance().getBoolean("MCVersion.1_14")) {
            materials.add(Material.BAMBOO); //TODO 1.14
            materials.add(Material.SWEET_BERRIES); //TODO 1.14
        }
        super.initializeAssociatedMaterials(materials);
    }
}