package dansplugins.simpleskills.data;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.objects.PlayerRecord;
import dansplugins.simpleskills.services.ConfigService;
import dansplugins.simpleskills.services.MessageService;
import dansplugins.simpleskills.skills.*;
import dansplugins.simpleskills.skills.abs.AbstractSkill;
import dansplugins.simpleskills.utils.ChanceCalculator;
import dansplugins.simpleskills.utils.ExperienceCalculator;
import dansplugins.simpleskills.utils.Logger;
import preponderous.ponder.minecraft.bukkit.tools.UUIDChecker;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Daniel Stephenson
 */
public class PersistentData {
    private final Logger logger;
    private final MessageService messageService;
    private final ConfigService configService;
    private final ExperienceCalculator experienceCalculator;
    private final ChanceCalculator chanceCalculator;
    private final SimpleSkills simpleSkills;

    private final HashSet<AbstractSkill> skills = new HashSet<>();
    private HashSet<PlayerRecord> playerRecords = new HashSet<>();

    public PersistentData(Logger logger, MessageService messageService, ConfigService configService, ExperienceCalculator experienceCalculator, ChanceCalculator chanceCalculator, SimpleSkills simpleSkills) {
        this.logger = logger;
        this.messageService = messageService;
        this.configService = configService;
        this.experienceCalculator = experienceCalculator;
        this.chanceCalculator = chanceCalculator;
        this.simpleSkills = simpleSkills;
        initializeSkills();
    }

    public HashSet<AbstractSkill> getSkills() {
        return skills;
    }

    public AbstractSkill getSkill(int Id) {
        for (AbstractSkill skill : skills) {
            if (skill.getId() == Id) {
                return skill;
            }
        }
        return null;
    }

    public AbstractSkill getSkill(String skillName) {
        for (AbstractSkill skill : skills) {
            if (skill.getName().equalsIgnoreCase(skillName)) {
                return skill;
            }
        }
        return null;
    }

    public boolean addSkill(AbstractSkill skill) {
        if (!skill.isActive()) {
            return false;
        }
        logger.log("Added skill: " + skill.getName());
        return skills.add(skill);
    }

    public boolean removeSkill(AbstractSkill skill) {
        logger.log("Removed skill: " + skill.getName());
        return skills.remove(skill);
    }

    public HashSet<PlayerRecord> getPlayerRecords() {
        return playerRecords;
    }

    public void setPlayerRecords(HashSet<PlayerRecord> playerRecords) {
        this.playerRecords = playerRecords;
    }

    public boolean addPlayerRecord(PlayerRecord playerRecord) {
        return playerRecords.add(playerRecord);
    }

    public PlayerRecord getPlayerRecord(UUID playerUUID) {
        for (PlayerRecord record : playerRecords) {
            if (record.getPlayerUUID().equals(playerUUID)) {
                return record;
            }
        }
        logger.log("A player record wasn't found for " + new UUIDChecker().findPlayerNameBasedOnUUID(playerUUID) + " wasn't found. One is being created for them.");
        PlayerRecord record = new PlayerRecord(this, messageService, configService, experienceCalculator, logger, playerUUID);
        addPlayerRecord(record);
        return record;
    }

    // ---

    public int getNumUnknownSkills() {
        return skills.size() - getNumKnownSkills();
    }

    public int getNumKnownSkills() {
        int knownSkills = 0;
        for (AbstractSkill skill : skills) {
            for (PlayerRecord record : playerRecords) {
                if (record.isKnown(skill)) {
                    knownSkills++;
                    break;
                }
            }
        }
        return knownSkills;
    }

    @Deprecated
    public int getNumUselessSkills() {
        return 0;
    }

    @Deprecated
    public int getNumUsefulSkills() {
        return 0;
    }

    public List<PlayerRecord> getTopPlayers() {
        final Comparator<PlayerRecord> recordComparator = (o1, o2) ->
                Integer.compare(o2.getOverallSkillLevel(), o1.getOverallSkillLevel());
        final List<PlayerRecord> playerRecords = new ArrayList<>(this.playerRecords);
        playerRecords.sort(recordComparator);
        return playerRecords.subList(0, Math.min(playerRecords.size(), 9));
    }

    public List<PlayerRecord> getTopPlayerRecords(int skillID) {
        final Comparator<PlayerRecord> recordComparator = (o1, o2) ->
                Integer.compare(o2.getSkillLevel(skillID, false), o1.getSkillLevel(skillID, false));
        return new ArrayList<>(this.playerRecords)
                .stream().sorted(recordComparator)
                .filter(record -> record.getSkillLevel(skillID, false) != -1)
                .limit(11)
                .collect(Collectors.toList());
    }

    private void initializeSkills() {
        addSkill(new Athlete(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Boating(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Breeding(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Cardio(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Crafting(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Digging(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Dueling(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Enchanting(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Farming(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Fishing(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Floriculture(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Gliding(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Hardiness(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Woodcutting(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Mining(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new MonsterHunting(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Pyromaniac(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Quarrying(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Riding(configService, logger, this, simpleSkills, messageService, chanceCalculator));
        addSkill(new Strength(configService, logger, this, simpleSkills, messageService, chanceCalculator));
    }

}