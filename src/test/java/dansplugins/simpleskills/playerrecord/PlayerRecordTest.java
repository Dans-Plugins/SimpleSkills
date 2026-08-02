package dansplugins.simpleskills.playerrecord;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.SkillRepository;
import dansplugins.simpleskills.skill.abs.AbstractSkill;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link PlayerRecord}'s current behavior for the pure bookkeeping paths
 * (skill levels, experience, overall level, save/load). Paths that reach a live
 * {@code Bukkit.getPlayer(...)} call (auto-learning an unknown skill via
 * {@code getSkillLevel(id, true)} and leveling up) are intentionally not exercised here:
 * mockito-core (without mockito-inline) cannot stub Bukkit's static accessors, and the
 * project pins Mockito 3.12.4 for JDK 21 compatibility reasons, so those branches are
 * left for the manual Testcontainer smoke harness.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class PlayerRecordTest {

    private static final UUID PLAYER_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");

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
    @Mock
    private FileConfiguration fileConfiguration;

    private PlayerRecord record;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getInt("defaultMaxLevel")).thenReturn(100);

        record = new PlayerRecord(skillRepository, messageService, configService, experienceCalculator, log, PLAYER_UUID);
    }

    private AbstractSkill mockSkill(int id, boolean active) {
        AbstractSkill skill = mock(AbstractSkill.class);
        when(skill.getId()).thenReturn(id);
        when(skill.isActive()).thenReturn(active);
        return skill;
    }

    @Test
    public void getSkillLevel_returnsNegativeOne_whenUnknownAndLearnFlagFalse() {
        assertEquals(-1, record.getSkillLevel(1, false));
    }

    @Test
    public void getSkillLevel_returnsStoredLevel_whenKnown() {
        record.setSkillLevel(1, 5);

        assertEquals(5, record.getSkillLevel(1, true));
        assertEquals(5, record.getSkillLevel(1, false));
    }

    @Test
    public void setSkillLevel_replacesExistingEntry() {
        record.setSkillLevel(1, 5);
        record.setSkillLevel(1, 9);

        assertEquals(9, record.getSkillLevel(1, false));
    }

    @Test
    public void incrementSkillLevel_incrementsPreseededLevel() {
        record.setSkillLevel(1, 0);

        record.incrementSkillLevel(1);

        assertEquals(1, record.getSkillLevel(1, false));
    }

    @Test
    public void getExperience_defaultsToZero_andStoresValue() {
        assertEquals(0, record.getExperience(2));

        record.setExperience(2, 7);

        assertEquals(7, record.getExperience(2));
    }

    @Test
    public void getKnownSkills_and_isKnown_reflectStoredSkillLevels() {
        AbstractSkill skill = mockSkill(3, true);
        record.setSkillLevel(3, 0);

        assertTrue(record.getKnownSkills().contains(3));
        assertTrue(record.isKnown(skill));
    }

    @Test
    public void isKnown_returnsFalse_whenSkillNeverSeeded() {
        AbstractSkill skill = mockSkill(4, true);

        assertFalse(record.isKnown(skill));
    }

    @Test
    public void getOverallSkillLevel_sumsOnlyActiveKnownSkills() {
        AbstractSkill activeSkill = mockSkill(1, true);
        AbstractSkill inactiveSkill = mockSkill(2, false);
        when(skillRepository.getSkill(1)).thenReturn(activeSkill);
        when(skillRepository.getSkill(2)).thenReturn(inactiveSkill);
        // skill 3 is known locally but no longer exists in the repository (e.g. removed)
        when(skillRepository.getSkill(3)).thenReturn(null);

        record.setSkillLevel(1, 5);
        record.setSkillLevel(2, 10);
        record.setSkillLevel(3, 7);

        assertEquals(5, record.getOverallSkillLevel());
    }

    @Test
    public void incrementExperience_incrementsExperience_evenWhenSkillUnknownToRepository() {
        when(skillRepository.getSkill(5)).thenReturn(null);

        record.incrementExperience(5);

        assertEquals(1, record.getExperience(5));
    }

    @Test
    public void incrementExperience_doesNotLevelUp_whenBelowRequiredExperience() {
        AbstractSkill skill = mockSkill(6, true);
        when(skillRepository.getSkill(6)).thenReturn(skill);
        when(experienceCalculator.getExperienceRequiredForLevelUp(anyInt(), anyInt(), anyDouble())).thenReturn(50);
        record.setSkillLevel(6, 0);

        record.incrementExperience(6);

        assertEquals(1, record.getExperience(6));
        assertEquals(0, record.getSkillLevel(6, false));
    }

    @Test
    public void incrementExperience_skipsLevelUpCheck_whenAlreadyAtMaxLevel() {
        AbstractSkill skill = mockSkill(8, true);
        when(skillRepository.getSkill(8)).thenReturn(skill);
        when(fileConfiguration.getInt("defaultMaxLevel")).thenReturn(5);
        record.setSkillLevel(8, 5);

        record.incrementExperience(8);

        assertEquals(1, record.getExperience(8));
        assertEquals(5, record.getSkillLevel(8, false));
    }

    @Test
    public void checkForLevelUp_leavesLevelAndExperienceUnchanged_whenBelowThreshold() {
        AbstractSkill skill = mockSkill(7, true);
        when(skillRepository.getSkill(7)).thenReturn(skill);
        when(experienceCalculator.getExperienceRequiredForLevelUp(anyInt(), anyInt(), anyDouble())).thenReturn(10);
        record.setSkillLevel(7, 2);
        record.setExperience(7, 3);

        record.checkForLevelUp(7);

        assertEquals(2, record.getSkillLevel(7, false));
        assertEquals(3, record.getExperience(7));
    }

    @Test
    public void saveAndLoad_roundTripsPlayerUuidLevelsAndExperience() {
        record.setSkillLevel(1, 5);
        record.setSkillLevel(2, 1);
        record.setExperience(1, 7);

        Map<String, String> saved = record.save();
        PlayerRecord loaded = new PlayerRecord(saved, skillRepository, messageService, configService, experienceCalculator, log);

        assertEquals(record.getPlayerUUID(), loaded.getPlayerUUID());
        assertEquals(record.getSkillLevels(), loaded.getSkillLevels());
        assertEquals(new HashMap<>(record.getExperience()), new HashMap<>(loaded.getExperience()));
    }
}
