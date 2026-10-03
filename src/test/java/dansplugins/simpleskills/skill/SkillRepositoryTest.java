package dansplugins.simpleskills.skill;

import dansplugins.simpleskills.skill.abs.AbstractSkill;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link SkillRepository}'s lookups. The difference between {@code getSkill(String)} and
 * {@code getActiveSkill(String)} is what decides whether a command can see a deactivated skill
 * ({@code ForceCommand} uses the former, {@code SkillCommand} and {@code TopCommand} the latter).
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SkillRepositoryTest {

    private SkillRepository skillRepository;
    private AbstractSkill activeSkill;
    private AbstractSkill inactiveSkill;

    @Before
    public void setUp() {
        skillRepository = new SkillRepository();
        activeSkill = mockSkill(1, "Mining", true);
        inactiveSkill = mockSkill(2, "Boating", false);
        skillRepository.addSkill(activeSkill);
        skillRepository.addSkill(inactiveSkill);
    }

    private AbstractSkill mockSkill(int id, String name, boolean active) {
        AbstractSkill skill = mock(AbstractSkill.class);
        when(skill.getId()).thenReturn(id);
        when(skill.getName()).thenReturn(name);
        when(skill.isActive()).thenReturn(active);
        return skill;
    }

    @Test
    public void getSkills_returnsActiveAndInactiveSkills() {
        HashSet<AbstractSkill> skills = skillRepository.getSkills();

        assertEquals(2, skills.size());
        assertTrue(skills.contains(activeSkill));
        assertTrue(skills.contains(inactiveSkill));
    }

    @Test
    public void getActiveSkills_returnsOnlyActiveSkills() {
        HashSet<AbstractSkill> activeSkills = skillRepository.getActiveSkills();

        assertEquals(1, activeSkills.size());
        assertTrue(activeSkills.contains(activeSkill));
    }

    @Test
    public void getActiveSkills_returnsCopy_soRemovalDoesNotAffectRepository() {
        skillRepository.getActiveSkills().clear();

        assertEquals(2, skillRepository.getSkills().size());
    }

    @Test
    public void getSkillById_returnsMatchingSkill_regardlessOfActiveState() {
        assertSame(activeSkill, skillRepository.getSkill(1));
        assertSame(inactiveSkill, skillRepository.getSkill(2));
    }

    @Test
    public void getSkillById_returnsNull_whenNoSkillHasThatId() {
        assertNull(skillRepository.getSkill(99));
    }

    @Test
    public void getSkillByName_isCaseInsensitive_andIncludesInactiveSkills() {
        assertSame(activeSkill, skillRepository.getSkill("mInInG"));
        assertSame(inactiveSkill, skillRepository.getSkill("boating"));
    }

    @Test
    public void getSkillByName_returnsNull_whenNameUnknown() {
        assertNull(skillRepository.getSkill("Archaeology"));
    }

    @Test
    public void getActiveSkill_isCaseInsensitive_forActiveSkill() {
        assertSame(activeSkill, skillRepository.getActiveSkill("MINING"));
    }

    @Test
    public void getActiveSkill_returnsNull_forInactiveSkill() {
        assertNull(skillRepository.getActiveSkill("Boating"));
    }

    @Test
    public void addSkill_returnsFalse_whenSameSkillAddedTwice() {
        assertFalse(skillRepository.addSkill(activeSkill));
        assertEquals(2, skillRepository.getSkills().size());
    }

    @Test
    public void removeSkill_removesSkill_andReturnsWhetherItWasPresent() {
        assertTrue(skillRepository.removeSkill(activeSkill));
        assertFalse(skillRepository.removeSkill(activeSkill));
        assertNull(skillRepository.getSkill("Mining"));
    }
}
