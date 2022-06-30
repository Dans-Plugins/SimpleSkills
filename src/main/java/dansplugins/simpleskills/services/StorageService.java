package dansplugins.simpleskills.services;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.data.objects.PlayerRecord;
import dansplugins.simpleskills.utils.ExperienceCalculator;
import dansplugins.simpleskills.utils.Logger;
import preponderous.ponder.misc.JsonWriterReader;

import java.util.*;

/**
 * @author Daniel Stephenson
 */
public class StorageService {
    private final PersistentData persistentData;
    private final MessageService messageService;
    private final ConfigService configService;
    private final ExperienceCalculator experienceCalculator;
    private final Logger logger;

    private final static String FILE_PATH = "./plugins/SimpleSkills/";
    private final static String PLAYER_RECORDS_FILE_NAME = "playerRecords.json";
    private final JsonWriterReader jsonWriterReader = new JsonWriterReader();

    public StorageService(PersistentData persistentData, MessageService messageService, ConfigService configService, ExperienceCalculator experienceCalculator, Logger logger) {
        this.persistentData = persistentData;
        this.messageService = messageService;
        this.configService = configService;
        this.experienceCalculator = experienceCalculator;
        this.logger = logger;
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
        for (PlayerRecord playerRecord : persistentData.getPlayerRecords()) {
            playerRecords.add(playerRecord.save());
        }
        jsonWriterReader.writeOutFiles(playerRecords, PLAYER_RECORDS_FILE_NAME);
    }


    private void loadPlayerRecords() {
        persistentData.getPlayerRecords().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + PLAYER_RECORDS_FILE_NAME);
        HashSet<PlayerRecord> playerRecords = new HashSet<>();
        for (Map<String, String> playerRecordData : data) {
            PlayerRecord playerRecord = new PlayerRecord(playerRecordData, persistentData, messageService, configService, experienceCalculator, logger);
            playerRecords.add(playerRecord);
        }
        persistentData.setPlayerRecords(playerRecords);
    }
}