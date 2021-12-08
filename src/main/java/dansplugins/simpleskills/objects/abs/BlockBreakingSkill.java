package dansplugins.simpleskills.objects.abs;

public abstract class BlockBreakingSkill extends BlockSkill {
    public BlockBreakingSkill(int ID, String name, int maxLevel, int baseExperienceRequirement, double experienceIncreaseFactor) {
        super(ID, name, maxLevel, baseExperienceRequirement, experienceIncreaseFactor);
    }

    public BlockBreakingSkill(int ID, String name) {
        super(ID, name);
    }
}
