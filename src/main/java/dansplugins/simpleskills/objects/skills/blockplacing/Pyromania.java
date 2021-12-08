package dansplugins.simpleskills.objects.skills.blockplacing;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.BlockPlacingSkill;
import dansplugins.simpleskills.objects.abs.BlockSkill;
import org.bukkit.Material;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 */
public class Pyromania extends BlockPlacingSkill {
    public Pyromania() {
        super(SupportedSkill.PYROMANIA.ordinal(), "Pyromania");
        initialize();
    }

    private void initialize() {
        HashSet<Material> materials = new HashSet<>();
        materials.add(Material.FIRE);
        materials.add(Material.SOUL_FIRE);
        super.initializeAssociatedMaterials(materials);
    }
}