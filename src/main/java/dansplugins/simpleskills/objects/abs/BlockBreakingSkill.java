package dansplugins.simpleskills.objects.abs;

import org.bukkit.Material;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 */
public abstract class BlockBreakingSkill extends Skill {
    private HashSet<Material> associatedMaterials = new HashSet<>();

    public BlockBreakingSkill(int ID, String name, int maxLevel, int baseExperienceRequirement, double experienceIncreaseFactor) {
        super(ID, name, maxLevel, baseExperienceRequirement, experienceIncreaseFactor);
    }

    public BlockBreakingSkill(int ID, String name) {
        super(ID, name);
    }

    public void initializeAssociatedMaterials(HashSet<Material> associatedMaterials) {
        this.associatedMaterials = associatedMaterials;
    }

    public boolean isMaterialAssociated(Material material) {
        return associatedMaterials.contains(material);
    }
}
