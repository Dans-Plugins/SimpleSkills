package dansplugins.simpleskills.objects.skills.blockbreaking;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.BlockBreakingSkill;
import dansplugins.simpleskills.objects.abs.BlockSkill;
import dansplugins.simpleskills.objects.benefits.ResourceExtraction;
import org.bukkit.Material;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 */
public class Digging extends BlockBreakingSkill {
    public Digging() {
        super(SupportedSkill.DIGGING.ordinal(), "Digging");
        initialize();
    }

    private void initialize() {
        addBenefit(new ResourceExtraction());

        HashSet<Material> materials = new HashSet<>();
        materials.add(Material.GRASS_BLOCK);
        materials.add(Material.DIRT);
        materials.add(Material.GRAVEL);
        materials.add(Material.SAND);
        materials.add(Material.SOUL_SAND);
        super.initializeAssociatedMaterials(materials);
    }
}
