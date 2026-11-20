package dansplugins.simpleskills.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.SkillRepository;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
    private final static Type LIST_MAP_TYPE = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFolder;

    // Cleared only by a successful load(). Until then save() refuses to run: the in-memory
    // records are empty (or stale), and writing them would replace the very file that could not
    // be read. The autosave, the world-save listener and onDisable() all call save(), and Bukkit
    // still runs onDisable() for a plugin whose onEnable() threw.
    private volatile boolean loadFailed = true;

    public StorageService(PlayerRecordRepository playerRecordRepository, SkillRepository skillRepository, MessageService messageService, ConfigService configService, ExperienceCalculator experienceCalculator, Log log) {
        this(playerRecordRepository, skillRepository, messageService, configService, experienceCalculator, log, new File(FILE_PATH));
    }

    StorageService(PlayerRecordRepository playerRecordRepository, SkillRepository skillRepository, MessageService messageService, ConfigService configService, ExperienceCalculator experienceCalculator, Log log, File dataFolder) {
        this.playerRecordRepository = playerRecordRepository;
        this.skillRepository = skillRepository;
        this.messageService = messageService;
        this.configService = configService;
        this.experienceCalculator = experienceCalculator;
        this.log = log;
        this.dataFolder = dataFolder;
    }

    /**
     * Writes the plugin's data out.
     * <p>
     * Synchronized because the callers do not share a thread: the scheduled autosave in
     * {@code SimpleSkills#scheduleAutoSave()} runs asynchronously, while shutdown and
     * {@link dansplugins.simpleskills.listeners.WorldSaveEventListener} both run on the main
     * thread. Without this, an autosave landing on the same tick as a world save would have two
     * threads writing the same file at once.
     * </p>
     */
    public synchronized boolean save() {
        if (loadFailed) {
            log.error("Player records were not saved because they were not loaded when the plugin was enabled. "
                    + new File(dataFolder, PLAYER_RECORDS_FILE_NAME) + " has been left as it was.");
            return false;
        }
        return savePlayerRecords();
    }

    /**
     * Reads every stored player record. Nothing in the repository changes until the whole file
     * has been parsed, so a file that cannot be read, or one malformed record, leaves it as it
     * was.
     * @return Whether the records were loaded. On failure the cause has been logged and
     *         {@link #save()} is refused until a later load succeeds.
     */
    public synchronized boolean load() {
        File file = new File(dataFolder, PLAYER_RECORDS_FILE_NAME);
        HashSet<PlayerRecord> playerRecords = new HashSet<>();
        try {
            for (Map<String, String> playerRecordData : readRecords(file)) {
                playerRecords.add(new PlayerRecord(playerRecordData, skillRepository, messageService, configService, experienceCalculator, log));
            }
        } catch (IOException | RuntimeException e) {
            // RuntimeException covers Gson's syntax errors and a record whose UUID does not parse.
            loadFailed = true;
            log.error("Player records could not be loaded from " + file + " (" + e + "). The file has been left "
                    + "untouched and will not be overwritten; fix or restore it and restart the server.");
            return false;
        }
        playerRecordRepository.setPlayerRecords(playerRecords);
        loadFailed = false;
        log.info("Loaded " + playerRecords.size() + " player record(s).");
        return true;
    }

    /**
     * Whether the last {@link #load()} failed (or none has run yet), in which case
     * {@link #save()} is refused.
     */
    public boolean isLoadFailed() {
        return loadFailed;
    }

    private boolean savePlayerRecords() {
        List<Map<String, String>> playerRecords = new ArrayList<>();
        for (PlayerRecord playerRecord : playerRecordRepository.getPlayerRecords()) {
            playerRecords.add(playerRecord.save());
        }
        File file = new File(dataFolder, PLAYER_RECORDS_FILE_NAME);
        try {
            writeRecords(file, playerRecords);
            return true;
        } catch (IOException | RuntimeException e) {
            log.error("Player records could not be saved to " + file + " (" + e + ").");
            return false;
        }
    }

    // A file that does not exist yet holds no records. Nor does an empty one: it can only be
    // left by a write cut off before this class wrote through a temp file, and there is
    // nothing in it to keep.
    private List<HashMap<String, String>> readRecords(File file) throws IOException {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            List<HashMap<String, String>> records = gson.fromJson(reader, LIST_MAP_TYPE);
            return records == null ? new ArrayList<>() : records;
        }
    }

    // Written to a sibling temp file and moved into place, so a server killed or a disk filled
    // mid-write (the autosave runs every five minutes) leaves the previous file intact instead
    // of a truncated one.
    private void writeRecords(File file, List<Map<String, String>> records) throws IOException {
        if (!dataFolder.isDirectory() && !dataFolder.mkdirs()) {
            throw new IOException(dataFolder + " could not be created");
        }
        Path target = file.toPath();
        Path temp = target.resolveSibling(file.getName() + ".tmp");
        try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            gson.toJson(records, writer);
        }
        try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
