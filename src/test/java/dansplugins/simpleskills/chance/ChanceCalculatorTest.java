package dansplugins.simpleskills.chance;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.skill.SkillRepository;
import dansplugins.simpleskills.skill.abs.AbstractSkill;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link ChanceCalculator}'s benefit roll. The roll draws from a fresh
 * {@link java.util.Random} on every call, so only the inputs whose outcome does not depend on
 * the draw are asserted: a success threshold of zero (level 0, or a nerf factor of 0), which
 * can never be met, and a threshold at or above {@code defaultMaxLevel}, which is always met.
 * Each of those is rolled {@link #ROLLS} times so that a change making the outcome
 * draw-dependent fails reliably rather than occasionally. The guards ahead of the roll — an
 * inactive or unknown skill, and a player without a record — are covered too.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ChanceCalculatorTest {

    private static final UUID PLAYER_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final int SKILL_ID = 7;
    private static final int MAX_LEVEL = 100;
    private static final int ROLLS = 500;

    @Mock
    private PlayerRecordRepository playerRecordRepository;
    @Mock
    private ConfigService configService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private MessageService messageService;
    @Mock
    private ExperienceCalculator experienceCalculator;
    @Mock
    private Log log;
    @Mock
    private FileConfiguration fileConfiguration;
    @Mock
    private PlayerRecord playerRecord;

    private ChanceCalculator chanceCalculator;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getInt("defaultMaxLevel")).thenReturn(MAX_LEVEL);

        chanceCalculator = new ChanceCalculator(playerRecordRepository, configService, skillRepository,
                messageService, experienceCalculator, log);
    }

    private AbstractSkill mockSkill(boolean active) {
        AbstractSkill skill = mock(AbstractSkill.class);
        when(skill.getId()).thenReturn(SKILL_ID);
        when(skill.isActive()).thenReturn(active);
        return skill;
    }

    private void givenSkillLevel(int level) {
        when(playerRecord.getSkillLevel(SKILL_ID, true)).thenReturn(level);
    }

    private boolean anyRollSucceeds(AbstractSkill skill, double nerfFactor) {
        for (int i = 0; i < ROLLS; i++) {
            if (chanceCalculator.roll(playerRecord, skill, nerfFactor)) {
                return true;
            }
        }
        return false;
    }

    private boolean everyRollSucceeds(AbstractSkill skill, double nerfFactor) {
        for (int i = 0; i < ROLLS; i++) {
            if (!chanceCalculator.roll(playerRecord, skill, nerfFactor)) {
                return false;
            }
        }
        return true;
    }

    // --- roll(PlayerRecord, AbstractSkill, double) ---

    @Test
    public void roll_neverSucceeds_atLevelZero() {
        givenSkillLevel(0);

        assertFalse(anyRollSucceeds(mockSkill(true), 1.0));
    }

    @Test
    public void roll_alwaysSucceeds_atMaxLevelWithoutNerf() {
        givenSkillLevel(MAX_LEVEL);

        assertTrue(everyRollSucceeds(mockSkill(true), 1.0));
    }

    @Test
    public void roll_neverSucceeds_atMaxLevelWithNerfFactorZero() {
        givenSkillLevel(MAX_LEVEL);

        assertFalse(anyRollSucceeds(mockSkill(true), 0.0));
    }

    @Test
    public void roll_alwaysSucceeds_whenLevelAboveMaxOffsetsTheNerfFactor() {
        // A level past defaultMaxLevel is not capped: 200 / 100 * 0.5 puts the threshold at the maximum.
        givenSkillLevel(2 * MAX_LEVEL);

        assertTrue(everyRollSucceeds(mockSkill(true), 0.5));
    }

    @Test
    public void roll_readsTheLevelWithTheLearnFlagSet() {
        givenSkillLevel(MAX_LEVEL);

        chanceCalculator.roll(playerRecord, mockSkill(true), 1.0);

        verify(playerRecord).getSkillLevel(SKILL_ID, true);
    }

    @Test
    public void roll_fails_forAnInactiveSkill_withoutReadingTheLevel() {
        givenSkillLevel(MAX_LEVEL);

        assertFalse(anyRollSucceeds(mockSkill(false), 1.0));
        verify(playerRecord, never()).getSkillLevel(anyInt(), anyBoolean());
    }

    @Test
    public void roll_fails_forANullSkill() {
        givenSkillLevel(MAX_LEVEL);

        assertFalse(chanceCalculator.roll(playerRecord, null, 1.0));
        verify(playerRecord, never()).getSkillLevel(anyInt(), anyBoolean());
    }

    // --- roll(UUID, int, double) ---

    @Test
    public void rollByUuid_usesTheExistingRecord_withoutCreatingOne() {
        AbstractSkill skill = mockSkill(true);
        when(playerRecordRepository.getPlayerRecord(PLAYER_UUID)).thenReturn(playerRecord);
        when(skillRepository.getSkill(SKILL_ID)).thenReturn(skill);
        givenSkillLevel(MAX_LEVEL);

        assertTrue(chanceCalculator.roll(PLAYER_UUID, SKILL_ID, 1.0));
        verify(playerRecordRepository, never()).createPlayerRecord(PLAYER_UUID);
    }

    @Test
    public void rollByUuid_createsAMissingRecord_andRollsAgainstIt() {
        AbstractSkill skill = mockSkill(true);
        when(playerRecordRepository.getPlayerRecord(PLAYER_UUID)).thenReturn(null, playerRecord);
        when(playerRecordRepository.createPlayerRecord(PLAYER_UUID)).thenReturn(true);
        when(skillRepository.getSkill(SKILL_ID)).thenReturn(skill);
        givenSkillLevel(MAX_LEVEL);

        assertTrue(chanceCalculator.roll(PLAYER_UUID, SKILL_ID, 1.0));
        verify(playerRecordRepository).createPlayerRecord(PLAYER_UUID);
    }

    @Test
    public void rollByUuid_failsAndLogs_whenTheRecordCannotBeCreated() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_UUID)).thenReturn(null);
        when(playerRecordRepository.createPlayerRecord(PLAYER_UUID)).thenReturn(false);

        assertFalse(chanceCalculator.roll(PLAYER_UUID, SKILL_ID, 1.0));
        verify(log).error("Failed to create player record for UUID: " + PLAYER_UUID);
        verify(skillRepository, never()).getSkill(anyInt());
    }

    @Test
    public void rollByUuid_fails_forAnUnknownSkillId() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_UUID)).thenReturn(playerRecord);
        when(skillRepository.getSkill(SKILL_ID)).thenReturn(null);
        givenSkillLevel(MAX_LEVEL);

        assertFalse(chanceCalculator.roll(PLAYER_UUID, SKILL_ID, 1.0));
        verify(playerRecord, never()).getSkillLevel(anyInt(), anyBoolean());
    }

    @Test
    public void rollByUuid_fails_forAnInactiveSkill() {
        AbstractSkill skill = mockSkill(false);
        when(playerRecordRepository.getPlayerRecord(PLAYER_UUID)).thenReturn(playerRecord);
        when(skillRepository.getSkill(SKILL_ID)).thenReturn(skill);
        givenSkillLevel(MAX_LEVEL);

        assertFalse(chanceCalculator.roll(PLAYER_UUID, SKILL_ID, 1.0));
        verify(playerRecord, never()).getSkillLevel(anyInt(), anyBoolean());
    }
}
