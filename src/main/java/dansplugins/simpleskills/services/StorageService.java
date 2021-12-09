package dansplugins.simpleskills.services;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.PlayerRecord;
import preponderous.ponder.misc.JsonWriterReader;

import java.util.*;

/**
 * @author Daniel Stephenson
 */
public class StorageService {
    private static StorageService instance;
    private final static String FILE_PATH = "./plugins/SimpleSkills/";
    private final static String PLAYER_RECORDS_FILE_NAME = "playerRecords.json";
    private final JsonWriterReader jsonWriterReader = new JsonWriterReader();

    private StorageService() {
        jsonWriterReader.initialize(FILE_PATH);
    }

    public static StorageService getInstance() {
        if (instance == null) {
            instance = new StorageService();
        }
        return instance;
    }

    public void save() {
        savePlayerRecords();
    }

    public void load() {
        loadPlayerRecords();
    }

    private void savePlayerRecords() {
        List<Map<String, String>> playerRecords = new ArrayList<>();
        for (PlayerRecord playerRecord : PersistentData.getInstance().getPlayerRecords()){
            playerRecords.add(playerRecord.save());
        }
        jsonWriterReader.writeOutFiles(playerRecords, PLAYER_RECORDS_FILE_NAME);
    }


    private void loadPlayerRecords() {
        PersistentData.getInstance().getPlayerRecords().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + PLAYER_RECORDS_FILE_NAME);
        HashSet<PlayerRecord> playerRecords = new HashSet<>();
        for (Map<String, String> playerRecordData : data){
            PlayerRecord playerRecord = new PlayerRecord(playerRecordData);
            playerRecords.add(playerRecord);
        }
        PersistentData.getInstance().setPlayerRecords(playerRecords);
    }
}