package dansplugins.simpleskills.objects.abs;

public abstract class BlockPlacingSkill extends BlockSkill {
    public BlockPlacingSkill(int ID, String name, int maxLevel, int baseExperienceRequirement, double experienceIncreaseFactor) {
        super(ID, name, maxLevel, baseExperienceRequirement, experienceIncreaseFactor);
    }

    public BlockPlacingSkill(int ID, String name) {
        super(ID, name);
    }
}