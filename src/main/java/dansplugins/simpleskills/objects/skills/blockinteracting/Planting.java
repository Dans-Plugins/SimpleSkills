package dansplugins.simpleskills.objects.skills.blockinteracting;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.BlockInteractingSkill;
import org.bukkit.Material;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 */
public class Planting extends BlockInteractingSkill {
    public Planting() {
        super(SupportedSkill.PLANTING.ordinal(), "Planting");
        initialize();
    }

    private void initialize() {
        HashSet<Material> materials = new HashSet<>();
        materials.add(Material.BEETROOT_SEEDS);
        materials.add(Material.MELON_SEEDS);
        materials.add(Material.PUMPKIN_SEEDS);
        materials.add(Material.WHEAT_SEEDS);
        materials.add(Material.POTATO);
        materials.add(Material.CARROT);
        super.initializeAssociatedMaterials(materials);
    }
}