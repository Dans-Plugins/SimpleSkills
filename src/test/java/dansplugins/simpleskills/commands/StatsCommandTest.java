package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.skill.SkillRepository;
import dansplugins.simpleskills.skill.abs.AbstractSkill;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link StatsCommand}, {@code /ss stats}, which reports how many skills are active,
 * how many player records exist, and how many active skills no player knows yet.
 * <p>
 * The messages are read from a real {@link YamlConfiguration} holding the {@code Stats} lines of
 * {@code message.yml}. Colour codes are left unconverted, as {@link MessageService#convert(String)}
 * is stubbed to return its argument.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class StatsCommandTest {

    @Mock
    private MessageService messageService;

    @Mock
    private PlayerRecordRepository playerRecordRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private AbstractSkill mining;

    @Mock
    private AbstractSkill fishing;

    @Mock
    private AbstractSkill farming;

    @Mock
    private PlayerRecord alice;

    @Mock
    private PlayerRecord bob;

    @Mock
    private CommandSender commandSender;

    private StatsCommand statsCommand;

    @Before
    public void setUp() {
        YamlConfiguration lang = new YamlConfiguration();
        lang.set("Stats", Arrays.asList(
                "&9=== SimpleSkills Stats ===",
                "&bNumber of skills: %nos%",
                "&bNumber of player records: %nopr%",
                "&bUnknown skills: %uns%"));
        when(messageService.getlang()).thenReturn(lang);
        when(messageService.convert(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        statsCommand = new StatsCommand(messageService, playerRecordRepository, skillRepository);
    }

    @Test
    public void getNumKnownSkills_countsEachActiveSkillOnce_whenSeveralPlayersKnowIt() {
        when(skillRepository.getActiveSkills()).thenReturn(new HashSet<>(Arrays.asList(mining, fishing, farming)));
        when(playerRecordRepository.getPlayerRecords()).thenReturn(new HashSet<>(Arrays.asList(alice, bob)));
        when(alice.isKnown(mining)).thenReturn(true);
        when(bob.isKnown(mining)).thenReturn(true);
        when(bob.isKnown(fishing)).thenReturn(true);

        assertEquals(2, statsCommand.getNumKnownSkills());
        assertEquals(1, statsCommand.getNumUnknownSkills());
    }

    @Test
    public void getNumUnknownSkills_isEveryActiveSkill_whenThereAreNoPlayerRecords() {
        when(skillRepository.getActiveSkills()).thenReturn(new HashSet<>(Arrays.asList(mining, fishing, farming)));
        when(playerRecordRepository.getPlayerRecords()).thenReturn(new HashSet<>());

        assertEquals(0, statsCommand.getNumKnownSkills());
        assertEquals(3, statsCommand.getNumUnknownSkills());
    }

    @Test
    public void getNumUnknownSkills_isZero_whenThereAreNoActiveSkills() {
        when(skillRepository.getActiveSkills()).thenReturn(new HashSet<>());
        when(playerRecordRepository.getPlayerRecords()).thenReturn(new HashSet<>(Arrays.asList(alice, bob)));

        assertEquals(0, statsCommand.getNumKnownSkills());
        assertEquals(0, statsCommand.getNumUnknownSkills());
    }

    @Test
    public void execute_sendsEveryStatsLineWithItsCountsFilledIn() {
        when(skillRepository.getActiveSkills()).thenReturn(new HashSet<>(Arrays.asList(mining, fishing, farming)));
        when(playerRecordRepository.getPlayerRecords()).thenReturn(new HashSet<>(Arrays.asList(alice, bob)));
        when(alice.isKnown(mining)).thenReturn(true);
        when(bob.isKnown(fishing)).thenReturn(true);

        boolean result = statsCommand.execute(commandSender);

        assertTrue(result);
        InOrder inOrder = inOrder(commandSender);
        inOrder.verify(commandSender).sendMessage("&9=== SimpleSkills Stats ===");
        inOrder.verify(commandSender).sendMessage("&bNumber of skills: 3");
        inOrder.verify(commandSender).sendMessage("&bNumber of player records: 2");
        inOrder.verify(commandSender).sendMessage("&bUnknown skills: 1");
    }

    @Test
    public void execute_ignoresArguments() {
        when(skillRepository.getActiveSkills()).thenReturn(new HashSet<>());
        when(playerRecordRepository.getPlayerRecords()).thenReturn(new HashSet<>());

        boolean result = statsCommand.execute(commandSender, new String[]{"anything"});

        assertTrue(result);
        InOrder inOrder = inOrder(commandSender);
        inOrder.verify(commandSender).sendMessage("&9=== SimpleSkills Stats ===");
        inOrder.verify(commandSender).sendMessage("&bNumber of skills: 0");
        inOrder.verify(commandSender).sendMessage("&bNumber of player records: 0");
        inOrder.verify(commandSender).sendMessage("&bUnknown skills: 0");
    }
}
