package dansplugins.simpleskills.services;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.SkillRepository;
import preponderous.ponder.misc.JsonWriterReader;

import java.util.*;

/**
 * @author Daniel Stephenson
 */
public class StorageService {
    private final PlayerRecordRepository playerRecordRepository;
    private final SkillRepository skillRepository;
    private final MessageService messageService;
    private final ConfigService configService;
    private final ExperienceCalculator experienceCalculator;
    private final Log log;

    private final static String FILE_PATH = "./plugins/SimpleSkills/";
    private final static String PLAYER_RECORDS_FILE_NAME = "playerRecords.json";
    private final JsonWriterReader jsonWriterReader = new JsonWriterReader();

    public StorageService(PlayerRecordRepository playerRecordRepository, SkillRepository skillRepository, MessageService messageService, ConfigService configService, ExperienceCalculator experienceCalculator, Log log) {
        this.playerRecordRepository = playerRecordRepository;
        this.skillRepository = skillRepository;
        this.messageService = messageService;
        this.configService = configService;
        this.experienceCalculator = experienceCalculator;
        this.log = log;
        jsonWriterReader.initialize(FILE_PATH);
    }

    public void save() {
        savePlayerRecords();
    }

    public void load() {
        loadPlayerRecords();
    }

    private void savePlayerRecords() {
        List<Map<String, String>> playerRecords = new ArrayList<>();
        for (PlayerRecord playerRecord : playerRecordRepository.getPlayerRecords()) {
            playerRecords.add(playerRecord.save());
        }
        jsonWriterReader.writeOutFiles(playerRecords, PLAYER_RECORDS_FILE_NAME);
    }


    private void loadPlayerRecords() {
        playerRecordRepository.getPlayerRecords().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + PLAYER_RECORDS_FILE_NAME);
        HashSet<PlayerRecord> playerRecords = new HashSet<>();
        for (Map<String, String> playerRecordData : data) {
            PlayerRecord playerRecord = new PlayerRecord(playerRecordData, skillRepository, messageService, configService, experienceCalculator, log);
            playerRecords.add(playerRecord);
        }
        playerRecordRepository.setPlayerRecords(playerRecords);
    }
}