package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.skill.SkillRepository;
import dansplugins.simpleskills.skill.abs.AbstractSkill;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link TopCommand}'s per-skill leaderboard, {@code /ss top <skillName>}.
 * <p>
 * The messages are read from a real {@link YamlConfiguration} holding the keys the command uses, so
 * that the text a sender receives is the one {@code message.yml} would produce. Colour codes are
 * left unconverted, as {@link MessageService#convert(String)} is stubbed to return its argument.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class TopCommandTest {
    private static final int SKILL_ID = 7;
    private static final String SKILL_NAME = "Woodcutting";

    @Mock
    private PlayerRecordRepository playerRecordRepository;

    @Mock
    private MessageService messageService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private AbstractSkill skill;

    @Mock
    private CommandSender commandSender;

    private TopCommand topCommand;

    @Before
    public void setUp() {
        YamlConfiguration lang = new YamlConfiguration();
        lang.set("SkillNotFound", "&cThat skill wasn't found.");
        lang.set("NoTop", "&cNo one is very skilled at %skill%.");
        lang.set("Top-Header", Collections.singletonList("&aTop Players in %skill%"));
        lang.set("Top-Body", Collections.singletonList("&a%rank%&7: &b%player% - LVL: %top%"));
        when(messageService.getlang()).thenReturn(lang);
        when(messageService.convert(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        when(skill.getId()).thenReturn(SKILL_ID);
        when(skill.getName()).thenReturn(SKILL_NAME);
        when(skillRepository.getActiveSkill(SKILL_NAME)).thenReturn(skill);

        topCommand = new TopCommand(playerRecordRepository, messageService, skillRepository);
    }

    @Test
    public void execute_sendsNoTop_whenNoPlayerHasTheSkill() {
        when(playerRecordRepository.getTopPlayerRecords(SKILL_ID)).thenReturn(new ArrayList<>());

        boolean result = topCommand.execute(commandSender, new String[]{SKILL_NAME});

        assertFalse(result);
        verify(commandSender).sendMessage("&cNo one is very skilled at Woodcutting.");
        verify(commandSender, never()).sendMessage("&aTop Players in Woodcutting");
    }

    @Test
    public void execute_sendsSkillNotFound_whenTheSkillIsNotActive() {
        boolean result = topCommand.execute(commandSender, new String[]{"Nonexistent"});

        assertFalse(result);
        verify(commandSender).sendMessage("&cThat skill wasn't found.");
    }
}
