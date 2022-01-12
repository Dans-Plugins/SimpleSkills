package dansplugins.simpleskills.utils;

import dansplugins.simpleskills.AbstractSkill;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.data.PlayerRecord;
import dansplugins.simpleskills.services.LocalConfigService;

import java.util.Random;
import java.util.UUID;

/**
 * @author Daniel Stephenson
 */
public class ChanceCalculator {

    private static ChanceCalculator instance;

    private ChanceCalculator() {}

    public static ChanceCalculator getInstance() {
        if (instance == null) instance = new ChanceCalculator();
        return instance;
    }

    public boolean roll(UUID playerUUID, int skillID, double nerfFactor) {
        final PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(playerUUID);
        final AbstractSkill skill = PersistentData.getInstance().getSkill(skillID);
        return roll(playerRecord, skill, nerfFactor);
    }

    public boolean roll(PlayerRecord playerRecord, AbstractSkill skill, double nerfFactor) {
        final Random random = new Random();
        double skillLevel = playerRecord.getSkillLevel(skill.getId(), true);
        double maxLevel = LocalConfigService.getInstance().getconfig().getInt("defaultMaxLevel");
        double chance = skillLevel/maxLevel;
        double result = random.nextDouble() * maxLevel;
        double threshold = maxLevel * chance * nerfFactor;
        return (result < threshold);
    }

}