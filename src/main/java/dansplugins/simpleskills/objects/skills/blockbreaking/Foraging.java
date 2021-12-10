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
public class Foraging extends BlockBreakingSkill {
    public Foraging() {
        super(SupportedSkill.FORAGING.ordinal(), "Foraging");
        initialize();
    }

    private void initialize() {
        addBenefit(new ResourceExtraction());

        HashSet<Material> materials = new HashSet<>();

        materials.add(Material.CACTUS);
        materials.add(Material.SEA_PICKLE);
        if (LocalConfigService.getInstance().getBoolean("MCVersion.1_14")) {
            materials.add(Material.SWEET_BERRIES); //TODO 1.14
        }
        materials.add(Material.BROWN_MUSHROOM);
        materials.add(Material.RED_MUSHROOM);
        materials.add(Material.KELP_PLANT);
        if (LocalConfigService.getInstance().getBoolean("MCVersion.1_16")) {
            materials.add(Material.CRIMSON_FUNGUS); //TODO 1.16
            materials.add(Material.WARPED_FUNGUS); //TODO 1.16
        }
        if (LocalConfigService.getInstance().getBoolean("MCVersion.1_17")) {
            materials.add(Material.GLOW_BERRIES); //TODO 1.17
        }
        super.initializeAssociatedMaterials(materials);
    }
}