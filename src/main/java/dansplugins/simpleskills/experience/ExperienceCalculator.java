package dansplugins.simpleskills.experience;

/**
 * @author Daniel Stephenson
 */
public class ExperienceCalculator {

    public int getExperienceRequiredForLevelUp(int currentLevel, int baseExperienceRequirement, double experienceIncreaseFactor) {
        return (int) (baseExperienceRequirement * Math.pow(experienceIncreaseFactor, currentLevel));
    }
}