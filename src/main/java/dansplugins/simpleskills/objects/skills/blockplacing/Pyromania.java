package dansplugins.simpleskills.objects.skills.blockplacing;

import dansplugins.simpleskills.nms.NMSVersion;
import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.BlockPlacingSkill;
import org.bukkit.Material;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 */
public class Pyromania extends BlockPlacingSkill {

    String nms = NMSVersion.getNMSVersion();
    public Pyromania() {
        super(SupportedSkill.PYROMANIA.ordinal(), "Pyromania");
        initialize();
    }

    private void initialize() {
        HashSet<Material> materials = new HashSet<>();
        materials.add(Material.FIRE);
        if (!(nms.contains("v1_13_R1") || nms.contains("v1_13_R2") || nms.contains("v1_14_R1") || nms.contains("v1_15_R1"))) {
            materials.add(Material.SOUL_FIRE); 
        }
        super.initializeAssociatedMaterials(materials);
    }
}