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
public class Mining extends BlockBreakingSkill {
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
        if (LocalConfigService.getInstance().getBoolean("MCVersion.1_16")) {
            materials.add(Material.NETHER_GOLD_ORE); //TODO 1.16
        }

        if (LocalConfigService.getInstance().getBoolean("MCVersion.1_17")) {
            materials.add(Material.COPPER_ORE); //TODO 1.17
            materials.add(Material.DEEPSLATE_COAL_ORE); //TODO 1.17
            materials.add(Material.DEEPSLATE_COPPER_ORE); //TODO 1.17
            materials.add(Material.DEEPSLATE_DIAMOND_ORE); //TODO 1.17
            materials.add(Material.DEEPSLATE_EMERALD_ORE); //TODO 1.17
            materials.add(Material.DEEPSLATE_GOLD_ORE); //TODO 1.17
            materials.add(Material.DEEPSLATE_IRON_ORE); //TODO 1.17
            materials.add(Material.DEEPSLATE_LAPIS_ORE); //TODO 1.17
            materials.add(Material.DEEPSLATE_REDSTONE_ORE); //TODO 1.17
        }
        super.initializeAssociatedMaterials(materials);
    }
}