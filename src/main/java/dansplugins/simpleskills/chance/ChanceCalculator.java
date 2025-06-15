package dansplugins.simpleskills.chance;

import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.skill.SkillRepository;
import dansplugins.simpleskills.skill.abs.AbstractSkill;

import java.util.Random;
import java.util.UUID;

/**
 * @author Daniel Stephenson
 */
public class ChanceCalculator {
    private final PlayerRecordRepository playerRecordRepository;
    private final ConfigService configService;
    private final SkillRepository skillRepository;
    private final MessageService messageService;
    private final ExperienceCalculator experienceCalculator;
    private final Log log;

    public ChanceCalculator(PlayerRecordRepository playerRecordRepository, ConfigService configService, SkillRepository skillRepository, MessageService messageService, ExperienceCalculator experienceCalculator, Log log) {
        this.playerRecordRepository = playerRecordRepository;
        this.configService = configService;
        this.skillRepository = skillRepository;
        this.messageService = messageService;
        this.experienceCalculator = experienceCalculator;
        this.log = log;
    }

    public boolean roll(UUID playerUUID, int skillID, double nerfFactor) {
        PlayerRecord playerRecord = playerRecordRepository.getPlayerRecord(playerUUID);
        if (playerRecord == null) {
            boolean success = playerRecordRepository.createPlayerRecord(playerUUID);
            if (!success) {
                log.error("Failed to create player record for UUID: " + playerUUID);
                return false;
            }
            playerRecord = playerRecordRepository.getPlayerRecord(playerUUID);
        }
        final AbstractSkill skill = skillRepository.getSkill(skillID);
        return roll(playerRecord, skill, nerfFactor);
    }

    public boolean roll(PlayerRecord playerRecord, AbstractSkill skill, double nerfFactor) {
        final Random random = new Random();
        double skillLevel = playerRecord.getSkillLevel(skill.getId(), true);
        double maxLevel = configService.getConfig().getInt("defaultMaxLevel");
        double chance = skillLevel / maxLevel;
        double result = random.nextDouble() * maxLevel;
        double threshold = maxLevel * chance * nerfFactor;
        return (result < threshold);
    }

}