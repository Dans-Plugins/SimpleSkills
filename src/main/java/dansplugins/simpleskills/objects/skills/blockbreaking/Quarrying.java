package dansplugins.simpleskills.objects.skills.blockbreaking;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.BlockBreakingSkill;
import dansplugins.simpleskills.objects.benefits.DoubleDrop;
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
        addBenefit(new DoubleDrop());

        HashSet<Material> materials = new HashSet<>();
        materials.add(Material.STONE);
        materials.add(Material.ANDESITE);
        materials.add(Material.DIORITE);
        materials.add(Material.GRANITE);
        super.initializeAssociatedMaterials(materials);
    }
}
