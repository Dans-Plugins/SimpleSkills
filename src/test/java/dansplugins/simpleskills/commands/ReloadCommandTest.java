package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.message.MessageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

/**
 * Characterizes {@link ReloadCommand}, {@code /ss reload}, which reloads {@code message.yml} and
 * {@code config.yml} and then confirms to the sender.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ReloadCommandTest {

    @Mock
    private MessageService messageService;

    @Mock
    private ConfigService configService;

    @Mock
    private CommandSender sender;

    private ReloadCommand reloadCommand;

    @Before
    public void setUp() {
        reloadCommand = new ReloadCommand(messageService, configService);
    }

    @Test
    public void execute_reloadsMessagesThenConfigThenConfirms() {
        assertTrue(reloadCommand.execute(sender));

        InOrder inOrder = inOrder(messageService, configService, sender);
        inOrder.verify(messageService).reloadlang();
        inOrder.verify(configService).reloadConfig();
        inOrder.verify(sender).sendMessage(ChatColor.GREEN + "Reloaded");
    }

    @Test
    public void execute_withArguments_ignoresThemAndReloads() {
        assertTrue(reloadCommand.execute(sender, new String[]{"anything"}));

        verify(messageService).reloadlang();
        verify(configService).reloadConfig();
        verify(sender).sendMessage(ChatColor.GREEN + "Reloaded");
    }
}
