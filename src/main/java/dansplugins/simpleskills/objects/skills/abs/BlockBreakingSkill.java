package dansplugins.simpleskills.objects.skills.abs;

import org.bukkit.Material;

import java.util.HashSet;

public class BlockBreakingSkill extends Skill {
    private HashSet<Material> associatedMaterials = new HashSet<>();

    public BlockBreakingSkill(int ID, String name, int maxLevel, int baseExperienceRequirement, int experienceIncreaseFactor) {
        super(ID, name, maxLevel, baseExperienceRequirement, experienceIncreaseFactor);
    }

    public void initializeAssociatedMaterials(HashSet<Material> associatedMaterials) {
        this.associatedMaterials = associatedMaterials;
    }

    public boolean isMaterialAssociated(Material material) {
        return associatedMaterials.contains(material);
    }
}
