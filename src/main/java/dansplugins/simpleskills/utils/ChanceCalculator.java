package dansplugins.simpleskills.utils;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.objects.abs.Skill;

import java.util.Random;
import java.util.UUID;

/**
 * @author Daniel Stephenson
 */
public class ChanceCalculator {
    private static ChanceCalculator instance;

    private ChanceCalculator() {

    }

    public static ChanceCalculator getInstance() {
        if (instance == null) {
            instance = new ChanceCalculator();
        }
        return instance;
    }

    public boolean roll(UUID playerUUID, int skillID, double nerfFactor) {
        PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(playerUUID);
        Skill skill = PersistentData.getInstance().getSkill(skillID);
        return roll(playerRecord, skill, nerfFactor);
    }

    public boolean roll(PlayerRecord playerRecord, Skill skill, double nerfFactor) {
        Random random = new Random();
        double skillLevel = playerRecord.getSkillLevel(skill.getID());
        double maxLevel = skill.getMaxLevel();
        double chance = skillLevel/maxLevel;
        double result = random.nextDouble() * maxLevel;
        double threshold = maxLevel * chance * nerfFactor;
        return (result < threshold);
    }
}