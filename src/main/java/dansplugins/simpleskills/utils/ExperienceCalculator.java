package dansplugins.simpleskills.utils;

/**
 * @author Daniel Stephenson
 */
public class ExperienceCalculator {
    private static ExperienceCalculator instance;

    private ExperienceCalculator() {

    }

    public static ExperienceCalculator getInstance() {
        if (instance == null) {
            instance = new ExperienceCalculator();
        }
        return instance;
    }

    public int getExperienceRequiredForLevelUp(int currentLevel, int baseExperienceRequirement, double experienceIncreaseFactor) {
        return (int) (baseExperienceRequirement * Math.pow(experienceIncreaseFactor, currentLevel));
    }
}