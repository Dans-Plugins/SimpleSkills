package dansplugins.simpleskills.commands.tab;

import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Characterizes {@link TabCommand#onTabComplete}, the tab completer set on each {@code plugin.yml}
 * command, which completes the first argument from the list of {@code /ss} subcommands.
 * <p>
 * Whether {@code force} is offered is decided on every completion from the sender completing, so
 * it does not depend on who completed first on the same instance (see #209).
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class TabCommandTest {

    @Mock
    private Player player;

    @Mock
    private ConsoleCommandSender console;

    @Mock
    private Command command;

    private TabCommand tabCommand;

    @Before
    public void setUp() {
        tabCommand = new TabCommand();
    }

    @Test
    public void onTabComplete_offersEverySubcommandExceptForceToAPlayerForAnEmptyArgument() {
        assertEquals(
                Arrays.asList("help", "info", "skill", "top", "stats", "reload"),
                tabCommand.onTabComplete(player, command, "ss", new String[]{""}));
    }

    @Test
    public void onTabComplete_offersForceToTheConsole() {
        assertEquals(
                Arrays.asList("help", "info", "skill", "top", "stats", "reload", "force"),
                tabCommand.onTabComplete(console, command, "ss", new String[]{""}));
    }

    @Test
    public void onTabComplete_keepsOnlySubcommandsStartingWithThePrefix() {
        assertEquals(
                Arrays.asList("skill", "stats"),
                tabCommand.onTabComplete(player, command, "ss", new String[]{"s"}));
    }

    @Test
    public void onTabComplete_matchesThePrefixIgnoringCase() {
        assertEquals(
                Collections.singletonList("reload"),
                tabCommand.onTabComplete(player, command, "ss", new String[]{"REL"}));
    }

    @Test
    public void onTabComplete_returnsAnEmptyListWhenNoSubcommandMatches() {
        assertEquals(
                Collections.emptyList(),
                tabCommand.onTabComplete(player, command, "ss", new String[]{"xyz"}));
    }

    @Test
    public void onTabComplete_returnsNullBeyondTheFirstArgument() {
        assertNull(tabCommand.onTabComplete(player, command, "ss", new String[]{"skill", ""}));
    }

    @Test
    public void onTabComplete_hidesForceFromAPlayerEvenAfterTheConsoleCompletedFirst() {
        tabCommand.onTabComplete(console, command, "ss", new String[]{""});

        assertEquals(
                Collections.emptyList(),
                tabCommand.onTabComplete(player, command, "ss", new String[]{"f"}));
    }

    @Test
    public void onTabComplete_offersForceToTheConsoleEvenAfterAPlayerCompletedFirst() {
        tabCommand.onTabComplete(player, command, "ss", new String[]{""});

        assertEquals(
                Collections.singletonList("force"),
                tabCommand.onTabComplete(console, command, "ss", new String[]{"f"}));
    }
}
