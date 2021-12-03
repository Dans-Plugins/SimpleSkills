package dansplugins.simpleskills.objects.skills.blockbreaking;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.BlockBreakingSkill;
import dansplugins.simpleskills.objects.benefits.DoubleDrop;
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
        addBenefit(new DoubleDrop());

        HashSet<Material> materials = new HashSet<>();
        materials.add(Material.WHEAT);
        materials.add(Material.BEETROOT);
        materials.add(Material.CARROT);
        materials.add(Material.POTATO);
        materials.add(Material.MELON);
        materials.add(Material.PUMPKIN);
        materials.add(Material.BAMBOO);
        materials.add(Material.COCOA_BEANS);
        materials.add(Material.SUGAR_CANE);
        materials.add(Material.SWEET_BERRIES);
        materials.add(Material.CHORUS_FRUIT);
        super.initializeAssociatedMaterials(materials);
    }
}