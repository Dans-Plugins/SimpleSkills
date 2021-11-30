package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.objects.skills.abs.Skill;
import org.bukkit.Material;

import java.util.HashSet;

public class Digging extends Skill {
    public Digging(int ID) {
        super(ID, "Digging", 100);
        initialize();
    }

    private void initialize() {
        HashSet<Material> materials = new HashSet<>();
        materials.add(Material.GRASS_BLOCK);
        materials.add(Material.DIRT);
        materials.add(Material.GRAVEL);
        materials.add(Material.SAND);
        materials.add(Material.SOUL_SAND);
        super.initializeAssociatedMaterials(materials);
    }
}
