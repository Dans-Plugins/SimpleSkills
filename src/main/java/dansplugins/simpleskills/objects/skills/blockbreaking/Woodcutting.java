package dansplugins.simpleskills.objects.skills.blockbreaking;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.BlockBreakingSkill;
import dansplugins.simpleskills.objects.benefits.ResourceExtraction;
import org.bukkit.Material;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 */
public class Woodcutting extends BlockBreakingSkill {
    public Woodcutting() {
        super(SupportedSkill.WOODCUTTING.ordinal(), "Woodcutting");
        initialize();
    }

    private void initialize() {
        addBenefit(new ResourceExtraction());

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
