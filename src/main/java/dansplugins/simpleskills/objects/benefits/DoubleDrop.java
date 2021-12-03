package dansplugins.simpleskills.objects.benefits;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.enums.SupportedBenefit;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.objects.abs.Benefit;
import dansplugins.simpleskills.objects.abs.Skill;

import java.util.Random;
import java.util.UUID;

public class DoubleDrop extends Benefit {
    public DoubleDrop() {
        super(SupportedBenefit.DOUBLE_DROP.ordinal(), "Double Drop");
    }

    public static boolean roll(UUID playerUUID, int skillID) {
        PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(playerUUID);
        Skill skill = PersistentData.getInstance().getSkill(skillID);
        return roll(playerRecord, skill);
    }

    public static boolean roll(PlayerRecord playerRecord, Skill skill) {
        Random random = new Random();
        double skillLevel = playerRecord.getSkillLevel(skill.getID());
        double maxLevel = skill.getMaxLevel();
        double chance = skillLevel/maxLevel;
        double result = random.nextDouble() * maxLevel;
        double nerfFactor = 0.10;
        double threshold = maxLevel * chance * nerfFactor;
        return (result < threshold);
    }
}