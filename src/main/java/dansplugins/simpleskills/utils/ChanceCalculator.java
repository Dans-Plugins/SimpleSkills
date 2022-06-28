package dansplugins.simpleskills.utils;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.data.objects.PlayerRecord;
import dansplugins.simpleskills.services.ConfigService;
import dansplugins.simpleskills.skills.abs.AbstractSkill;

import java.util.Random;
import java.util.UUID;

/**
 * @author Daniel Stephenson
 */
public class ChanceCalculator {
    private final PersistentData persistentData;
    private final ConfigService configService;

    public ChanceCalculator(PersistentData persistentData, ConfigService configService) {
        this.persistentData = persistentData;
        this.configService = configService;
    }

    public boolean roll(UUID playerUUID, int skillID, double nerfFactor) {
        final PlayerRecord playerRecord = persistentData.getPlayerRecord(playerUUID);
        final AbstractSkill skill = persistentData.getSkill(skillID);
        return roll(playerRecord, skill, nerfFactor);
    }

    public boolean roll(PlayerRecord playerRecord, AbstractSkill skill, double nerfFactor) {
        final Random random = new Random();
        double skillLevel = playerRecord.getSkillLevel(skill.getId(), true);
        double maxLevel = configService.getconfig().getInt("defaultMaxLevel");
        double chance = skillLevel / maxLevel;
        double result = random.nextDouble() * maxLevel;
        double threshold = maxLevel * chance * nerfFactor;
        return (result < threshold);
    }

}