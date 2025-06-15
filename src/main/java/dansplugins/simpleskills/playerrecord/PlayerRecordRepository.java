package dansplugins.simpleskills.playerrecord;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.SkillRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Daniel Stephenson
 */
public class PlayerRecordRepository {
    private HashSet<PlayerRecord> playerRecords = new HashSet<>();
    private final Log log;
    private final MessageService messageService;
    private final SkillRepository skillRepository;
    private final ConfigService configService;
    private final ExperienceCalculator experienceCalculator;

    public PlayerRecordRepository(Log log,
                                  MessageService messageService,
                                  SkillRepository skillRepository,
                                  ConfigService configService,
                                  ExperienceCalculator experienceCalculator) {
        this.log = log;
        this.messageService = messageService;
        this.skillRepository = skillRepository;
        this.configService = configService;
        this.experienceCalculator = experienceCalculator;
    }

    public HashSet<PlayerRecord> getPlayerRecords() {
        return playerRecords;
    }

    public void setPlayerRecords(HashSet<PlayerRecord> playerRecords) {
        this.playerRecords = playerRecords;
    }

    private boolean addPlayerRecord(PlayerRecord playerRecord) {
        return playerRecords.add(playerRecord);
    }

    public PlayerRecord getPlayerRecord(UUID playerUUID) {
        log.info("Searching for player record with UUID: " + playerUUID);
        for (PlayerRecord record : playerRecords) {
            if (record.getPlayerUUID().equals(playerUUID)) {
                log.info("Found player record for UUID: " + playerUUID);
                return record;
            }
        }
        log.info("No player record found for UUID: " + playerUUID);
        return null;
    }

    // ---

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

    public boolean createPlayerRecord(UUID uniqueId) {
        PlayerRecord newPlayerRecord = new PlayerRecord(skillRepository, messageService, configService, experienceCalculator, log, uniqueId);
        return addPlayerRecord(newPlayerRecord);
    }
}