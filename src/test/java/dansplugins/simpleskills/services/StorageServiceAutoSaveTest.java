package dansplugins.simpleskills.services;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.skill.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StorageService integration with auto-save
 */
class StorageServiceAutoSaveTest {

    @Mock
    private PlayerRecordRepository playerRecordRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private MessageService messageService;
    @Mock
    private ConfigService configService;
    @Mock
    private ExperienceCalculator experienceCalculator;
    @Mock
    private Log log;

    private StorageService storageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storageService = new StorageService(playerRecordRepository, skillRepository, 
                                           messageService, configService, 
                                           experienceCalculator, log);
    }

    @Test
    void testStorageServiceCreation() {
        assertNotNull(storageService);
    }

    @Test
    void testSaveMethodExists() {
        // Verify that save() method can be called without crashing
        assertDoesNotThrow(() -> {
            storageService.save();
        });
    }

    @Test
    void testLoadMethodExists() {
        // Verify that load() method can be called without crashing
        assertDoesNotThrow(() -> {
            storageService.load();
        });
    }
}
