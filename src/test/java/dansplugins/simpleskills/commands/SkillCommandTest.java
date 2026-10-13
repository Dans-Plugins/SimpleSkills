package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.SkillRepository;
import dansplugins.simpleskills.skill.abs.AbstractSkill;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link SkillCommand}, {@code /ss skill}, which shows a skill's info by name.
 * <p>
 * A real {@link SkillRepository} is used so that the lookup's handling of deactivated skills is
 * exercised rather than stubbed. A deactivated skill is currently answered as not found; whether
 * it should instead be shown with {@code Active: false} is open in issue #175.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SkillCommandTest {

    private static final String USAGE = "&cUsage: /ss skill (skillName)";
    private static final String NOT_FOUND = "&cThat skill wasn't found.";

    @Mock
    private MessageService messageService;

    @Mock
    private FileConfiguration lang;

    @Mock
    private CommandSender sender;

    private SkillRepository skillRepository;
    private AbstractSkill activeSkill;
    private AbstractSkill inactiveSkill;

    private SkillCommand skillCommand;

    @Before
    public void setUp() {
        when(messageService.getlang()).thenReturn(lang);
        when(lang.getString("SkillHUsage")).thenReturn(USAGE);
        when(lang.getString("SkillNotFound")).thenReturn(NOT_FOUND);
        when(messageService.convert(any())).thenAnswer(invocation -> "converted:" + invocation.getArgument(0));

        skillRepository = new SkillRepository();
        activeSkill = mockSkill("Mining", true);
        inactiveSkill = mockSkill("Boating", false);
        skillRepository.addSkill(activeSkill);
        skillRepository.addSkill(inactiveSkill);

        skillCommand = new SkillCommand(messageService, skillRepository);
    }

    private AbstractSkill mockSkill(String name, boolean active) {
        AbstractSkill skill = mock(AbstractSkill.class);
        when(skill.getName()).thenReturn(name);
        when(skill.isActive()).thenReturn(active);
        return skill;
    }

    @Test
    public void execute_withoutArguments_sendsTheUsageMessage() {
        boolean result = skillCommand.execute(sender);

        assertFalse(result);
        verify(sender).sendMessage("converted:" + USAGE);
    }

    @Test
    public void execute_sendsTheInfoOfAnActiveSkill() {
        boolean result = skillCommand.execute(sender, new String[]{"Mining"});

        assertTrue(result);
        verify(activeSkill).sendInfo(sender);
        verify(sender, never()).sendMessage("converted:" + NOT_FOUND);
    }

    @Test
    public void execute_matchesTheSkillNameIgnoringCase() {
        boolean result = skillCommand.execute(sender, new String[]{"mInInG"});

        assertTrue(result);
        verify(activeSkill).sendInfo(sender);
    }

    @Test
    public void execute_reportsAnUnknownSkillAsNotFound() {
        boolean result = skillCommand.execute(sender, new String[]{"Juggling"});

        assertFalse(result);
        verify(sender).sendMessage("converted:" + NOT_FOUND);
        verify(activeSkill, never()).sendInfo(any());
        verify(inactiveSkill, never()).sendInfo(any());
    }

    @Test
    public void execute_reportsADeactivatedSkillAsNotFound_ratherThanShowingItsInfo() {
        boolean result = skillCommand.execute(sender, new String[]{"Boating"});

        assertFalse(result);
        verify(sender).sendMessage("converted:" + NOT_FOUND);
        verify(inactiveSkill, never()).sendInfo(any());
    }
}
