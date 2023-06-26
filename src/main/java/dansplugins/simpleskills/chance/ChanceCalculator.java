package dansplugins.simpleskills.chance;

import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Logger;
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
    private final Logger logger;

    public ChanceCalculator(PlayerRecordRepository playerRecordRepository, ConfigService configService, SkillRepository skillRepository, MessageService messageService, ExperienceCalculator experienceCalculator, Logger logger) {
        this.playerRecordRepository = playerRecordRepository;
        this.configService = configService;
        this.skillRepository = skillRepository;
        this.messageService = messageService;
        this.experienceCalculator = experienceCalculator;
        this.logger = logger;
    }

    public boolean roll(UUID playerUUID, int skillID, double nerfFactor) {
        final PlayerRecord playerRecord = playerRecordRepository.getPlayerRecord(playerUUID);
        if (playerRecord == null) {
            PlayerRecord newPlayerRecord = new PlayerRecord(skillRepository, messageService, configService, experienceCalculator, logger, playerUUID);
            playerRecordRepository.addPlayerRecord(newPlayerRecord);
            return false;
        }
        final AbstractSkill skill = skillRepository.getSkill(skillID);
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