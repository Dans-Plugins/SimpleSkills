package dansplugins.simpleskills.playerrecord;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.SkillRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link PlayerRecordRepository}'s lookup, creation, and leaderboard ordering. The
 * leaderboards are driven by mocked {@link PlayerRecord}s so that each record's overall and per-skill
 * level can be set directly.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class PlayerRecordRepositoryTest {

    private static final int SKILL_ID = 5;

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

    private PlayerRecordRepository repository;

    @Before
    public void setUp() {
        repository = new PlayerRecordRepository(log, messageService, skillRepository, configService, experienceCalculator);
    }

    private PlayerRecord recordWithOverallLevel(int overallLevel) {
        PlayerRecord record = mock(PlayerRecord.class);
        when(record.getOverallSkillLevel()).thenReturn(overallLevel);
        return record;
    }

    private PlayerRecord recordWithSkillLevel(int skillLevel) {
        PlayerRecord record = mock(PlayerRecord.class);
        when(record.getSkillLevel(SKILL_ID, false)).thenReturn(skillLevel);
        return record;
    }

    private void seed(PlayerRecord... records) {
        repository.setPlayerRecords(new HashSet<>(Arrays.asList(records)));
    }

    @Test
    public void createPlayerRecord_addsRecordFindableByUuid() {
        UUID uuid = UUID.fromString("22222222-2222-2222-2222-222222222222");

        assertTrue(repository.createPlayerRecord(uuid));

        PlayerRecord record = repository.getPlayerRecord(uuid);
        assertEquals(uuid, record.getPlayerUUID());
        assertTrue(record.getSkillLevels().isEmpty());
    }

    @Test
    public void createPlayerRecord_addsSecondRecord_whenUuidAlreadyHasOne() {
        // PlayerRecord does not override equals, so the HashSet does not deduplicate by UUID;
        // callers (PlayerJoinEventListener) check getPlayerRecord(...) first.
        UUID uuid = UUID.fromString("33333333-3333-3333-3333-333333333333");

        assertTrue(repository.createPlayerRecord(uuid));
        assertTrue(repository.createPlayerRecord(uuid));

        assertEquals(2, repository.getPlayerRecords().size());
    }

    @Test
    public void getPlayerRecord_returnsNull_whenUuidUnknown() {
        repository.createPlayerRecord(UUID.fromString("44444444-4444-4444-4444-444444444444"));

        assertNull(repository.getPlayerRecord(UUID.fromString("55555555-5555-5555-5555-555555555555")));
    }

    @Test
    public void getTopPlayers_sortsByOverallLevelDescending() {
        PlayerRecord low = recordWithOverallLevel(3);
        PlayerRecord high = recordWithOverallLevel(40);
        PlayerRecord mid = recordWithOverallLevel(12);
        seed(low, high, mid);

        assertEquals(Arrays.asList(high, mid, low), repository.getTopPlayers());
    }

    @Test
    public void getTopPlayers_returnsAtMostNineRecords() {
        PlayerRecord[] records = new PlayerRecord[12];
        for (int i = 0; i < records.length; i++) {
            records[i] = recordWithOverallLevel(i);
        }
        seed(records);

        List<PlayerRecord> top = repository.getTopPlayers();

        assertEquals(9, top.size());
        assertSame(records[11], top.get(0));
        assertSame(records[3], top.get(8));
    }

    @Test
    public void getTopPlayerRecords_sortsBySkillLevelDescending_andExcludesUnknownSkill() {
        PlayerRecord unknown = recordWithSkillLevel(-1);
        PlayerRecord zero = recordWithSkillLevel(0);
        PlayerRecord high = recordWithSkillLevel(20);
        PlayerRecord mid = recordWithSkillLevel(7);
        seed(unknown, zero, high, mid);

        assertEquals(Arrays.asList(high, mid, zero), repository.getTopPlayerRecords(SKILL_ID));
    }

    @Test
    public void getTopPlayerRecords_returnsAtMostElevenRecords() {
        PlayerRecord[] records = new PlayerRecord[15];
        for (int i = 0; i < records.length; i++) {
            records[i] = recordWithSkillLevel(i);
        }
        seed(records);

        List<PlayerRecord> top = repository.getTopPlayerRecords(SKILL_ID);

        assertEquals(11, top.size());
        assertSame(records[14], top.get(0));
        assertSame(records[4], top.get(10));
    }
}
