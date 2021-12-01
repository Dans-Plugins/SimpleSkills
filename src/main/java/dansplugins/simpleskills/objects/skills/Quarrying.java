package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.objects.skills.abs.Skill;
import org.bukkit.Material;

import java.util.HashSet;

public class Quarrying extends Skill {
    public Quarrying(int ID) {
        super(ID, "Quarrying", 100, 10, 2);
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
