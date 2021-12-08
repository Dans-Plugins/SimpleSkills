package dansplugins.simpleskills.objects.abs;

import org.bukkit.Material;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 */
public abstract class BlockSkill extends Skill {
    private HashSet<Material> associatedMaterials = new HashSet<>();

    public BlockSkill(int ID, String name, int maxLevel, int baseExperienceRequirement, double experienceIncreaseFactor) {
        super(ID, name, maxLevel, baseExperienceRequirement, experienceIncreaseFactor);
    }

    public BlockSkill(int ID, String name) {
        super(ID, name);
    }

    public void initializeAssociatedMaterials(HashSet<Material> associatedMaterials) {
        this.associatedMaterials = associatedMaterials;
    }

    public boolean isMaterialAssociated(Material material) {
        return associatedMaterials.contains(material);
    }
}
