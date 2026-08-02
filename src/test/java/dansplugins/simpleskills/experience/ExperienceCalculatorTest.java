package dansplugins.simpleskills.experience;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExperienceCalculatorTest {

    private ExperienceCalculator experienceCalculator;

    @Before
    public void setUp() {
        experienceCalculator = new ExperienceCalculator();
    }

    @Test
    public void getExperienceRequiredForLevelUp_returnsZero_whenCurrentLevelIsZero() {
        int result = experienceCalculator.getExperienceRequiredForLevelUp(0, 100, 1.2);

        assertEquals(0, result);
    }

    @Test
    public void getExperienceRequiredForLevelUp_returnsBaseRequirement_whenCurrentLevelIsOne() {
        int result = experienceCalculator.getExperienceRequiredForLevelUp(1, 50, 1.2);

        assertEquals(50, result);
    }

    @Test
    public void getExperienceRequiredForLevelUp_returnsBaseRequirement_whenIncreaseFactorIsZero() {
        int result = experienceCalculator.getExperienceRequiredForLevelUp(25, 10, 0.0);

        assertEquals(10, result);
    }

    @Test
    public void getExperienceRequiredForLevelUp_scalesWithLevelAndFactor() {
        int currentLevel = 5;
        int baseExperienceRequirement = 10;
        double experienceIncreaseFactor = 1.5;

        int result = experienceCalculator.getExperienceRequiredForLevelUp(
                currentLevel, baseExperienceRequirement, experienceIncreaseFactor);

        int expected = (int) (baseExperienceRequirement * Math.pow(currentLevel, experienceIncreaseFactor));
        assertEquals(expected, result);
    }

    @Test
    public void getExperienceRequiredForLevelUp_truncatesFractionalResultTowardZero() {
        // base=3, level=2, factor=1.5 -> 3 * 2^1.5 = 8.485..., truncated to 8
        int result = experienceCalculator.getExperienceRequiredForLevelUp(2, 3, 1.5);

        assertEquals(8, result);
    }
}
