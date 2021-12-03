package dansplugins.simpleskills.objects.skills.blockbreaking;

import dansplugins.simpleskills.SupportedSkill;
import dansplugins.simpleskills.objects.skills.abs.BlockBreakingSkill;
import org.bukkit.Material;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 */
public class Quarrying extends BlockBreakingSkill {
    public Quarrying() {
        super(SupportedSkill.QUARRYING.ordinal(), "Quarrying");
        initialize();
    }

    private void initialize() {
        HashSet<Material> materials = new HashSet<>();
        materials.add(Material.STONE);
        materials.add(Material.ANDESITE);
        materials.add(Material.DIORITE);
        materials.add(Material.GRANITE);
        super.initializeAssociatedMaterials(materials);
    }
}
