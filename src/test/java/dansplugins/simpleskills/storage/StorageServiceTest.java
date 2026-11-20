package dansplugins.simpleskills.storage;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.skill.SkillRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Round-trips player records through the real file and checks that a file which could not be
 * loaded is never overwritten by the (empty) in-memory state.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class StorageServiceTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private Log log;
    @Mock
    private MessageService messageService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private ConfigService configService;
    @Mock
    private ExperienceCalculator experienceCalculator;

    private File dataFolder;
    private File recordsFile;

    @Before
    public void setUp() throws IOException {
        dataFolder = temporaryFolder.newFolder("SimpleSkills");
        recordsFile = new File(dataFolder, "playerRecords.json");
    }

    private PlayerRecordRepository newRepository() {
        return new PlayerRecordRepository(log, messageService, skillRepository, configService, experienceCalculator);
    }

    private StorageService newStorageService(PlayerRecordRepository repository) {
        return new StorageService(repository, skillRepository, messageService, configService, experienceCalculator, log, dataFolder);
    }

    private PlayerRecord newRecord(UUID playerUUID) {
        return new PlayerRecord(skillRepository, messageService, configService, experienceCalculator, log, playerUUID);
    }

    private static void write(File file, String content) throws IOException {
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] read(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    @Test
    public void playerRecordsSurviveASaveAndLoadRoundTrip() {
        // prepare
        PlayerRecordRepository repository = newRepository();
        StorageService storageService = newStorageService(repository);
        assertTrue(storageService.load());
        UUID playerUUID = UUID.randomUUID();
        PlayerRecord record = newRecord(playerUUID);
        record.setSkillLevel(3, 12);
        record.setSkillLevel(7, 1);
        record.setExperience(3, 450);
        repository.getPlayerRecords().add(record);
        assertTrue(storageService.save());

        PlayerRecordRepository restored = newRepository();

        // execute
        boolean loaded = newStorageService(restored).load();

        // verify
        assertTrue(loaded);
        assertEquals(1, restored.getPlayerRecords().size());
        PlayerRecord restoredRecord = restored.getPlayerRecord(playerUUID);
        assertNotNull(restoredRecord);
        assertEquals(12, restoredRecord.getSkillLevel(3, false));
        assertEquals(1, restoredRecord.getSkillLevel(7, false));
        assertEquals(450, restoredRecord.getExperience(3));
    }

    @Test
    public void loadWithNoFileSucceedsWithNoRecords() {
        PlayerRecordRepository repository = newRepository();

        assertTrue(newStorageService(repository).load());
        assertTrue(repository.getPlayerRecords().isEmpty());
    }

    @Test
    public void saveBeforeAnyLoadIsRefusedAndLeavesTheFileAlone() throws IOException {
        // prepare - data from a previous run; onEnable() failed before load() ran
        String previousRun = "[{\"playerUUID\":\"\\\"" + UUID.randomUUID() + "\\\"\"}]";
        write(recordsFile, previousRun);
        StorageService storageService = newStorageService(newRepository());

        // execute
        boolean saved = storageService.save();

        // verify
        assertFalse(saved);
        assertTrue(storageService.isLoadFailed());
        assertEquals(previousRun, new String(read(recordsFile), StandardCharsets.UTF_8));
    }

    @Test
    public void truncatedFileFailsTheLoadAndIsNeverOverwritten() throws IOException {
        // prepare - a good save, then the file cut off mid-write
        PlayerRecordRepository repository = newRepository();
        StorageService storageService = newStorageService(repository);
        storageService.load();
        PlayerRecord record = newRecord(UUID.randomUUID());
        record.setSkillLevel(1, 30);
        repository.getPlayerRecords().add(record);
        storageService.save();
        String full = new String(read(recordsFile), StandardCharsets.UTF_8);
        write(recordsFile, full.substring(0, full.length() / 2));
        byte[] truncated = read(recordsFile);

        PlayerRecordRepository restarted = newRepository();
        StorageService restartedService = newStorageService(restarted);

        // execute - load on the next start, then the autosave / world save / onDisable save
        boolean loaded = restartedService.load();
        boolean saved = restartedService.save();

        // verify
        assertFalse(loaded);
        assertFalse(saved);
        assertTrue(restarted.getPlayerRecords().isEmpty());
        assertArrayEquals(truncated, read(recordsFile));
    }

    @Test
    public void malformedRecordFailsTheWholeLoadAndLeavesMemoryUntouched() throws IOException {
        // prepare - a loaded service holding one record, then a file whose record has a bad UUID
        PlayerRecordRepository repository = newRepository();
        StorageService storageService = newStorageService(repository);
        storageService.load();
        repository.getPlayerRecords().add(newRecord(UUID.randomUUID()));
        write(recordsFile, "[{\"playerUUID\":\"\\\"not-a-uuid\\\"\"}]");

        // execute
        boolean loaded = storageService.load();

        // verify
        assertFalse(loaded);
        assertEquals(1, repository.getPlayerRecords().size());
        assertFalse(storageService.save());
        assertTrue(new String(read(recordsFile), StandardCharsets.UTF_8).contains("not-a-uuid"));
    }

    @Test
    public void emptyFileLoadsAsNoRecords() throws IOException {
        write(recordsFile, "");
        PlayerRecordRepository repository = newRepository();

        assertTrue(newStorageService(repository).load());
        assertTrue(repository.getPlayerRecords().isEmpty());
    }

    @Test
    public void saveLeavesNoTemporaryFileBehind() {
        PlayerRecordRepository repository = newRepository();
        StorageService storageService = newStorageService(repository);
        storageService.load();
        repository.getPlayerRecords().add(newRecord(UUID.randomUUID()));

        assertTrue(storageService.save());

        String[] names = dataFolder.list();
        assertNotNull(names);
        for (String name : names) {
            assertFalse(name, name.endsWith(".tmp"));
        }
    }
}
