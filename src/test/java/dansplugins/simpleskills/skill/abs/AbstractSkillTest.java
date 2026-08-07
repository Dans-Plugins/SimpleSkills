package dansplugins.simpleskills.skill.abs;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link AbstractSkill#handle(org.bukkit.event.Event)}'s reporting of a trigger
 * failure, which is raised through reflection and therefore hides the real failure unless the
 * cause is carried over to the rethrown exception.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class AbstractSkillTest {

    @Mock
    private ConfigService configService;
    @Mock
    private Log log;
    @Mock
    private PlayerRecordRepository playerRecordRepository;
    @Mock
    private SimpleSkills simpleSkills;
    @Mock
    private MessageService messageService;
    @Mock
    private FileConfiguration fileConfiguration;
    @Mock
    private Player player;
    @Mock
    private Block block;

    private ThrowingSkill skill;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        skill = new ThrowingSkill(configService, log, playerRecordRepository, simpleSkills, messageService);
    }

    @Test
    public void handle_carriesTheUnderlyingFailureAsTheCause_whenATriggerThrows() {
        try {
            skill.handle((org.bukkit.event.Event) new BlockBreakEvent(block, player));
            fail("Expected the failing trigger to be reported as an IllegalStateException");
        } catch (IllegalStateException exception) {
            assertEquals("Failed to trigger 'Throwing' with event 'BlockBreakEvent'!", exception.getMessage());
            assertSame(skill.thrown, exception.getCause());
        }
    }

    /**
     * Minimal concrete {@link AbstractSkill} whose only trigger fails, standing in for any
     * skill whose reward path throws (for example on a message key its message.yml predates).
     */
    public static class ThrowingSkill extends AbstractSkill {
        final RuntimeException thrown = new NullPointerException("missing message key");

        ThrowingSkill(ConfigService configService, Log log, PlayerRecordRepository playerRecordRepository,
                      SimpleSkills simpleSkills, MessageService messageService) {
            super(configService, log, playerRecordRepository, simpleSkills, messageService, "Throwing",
                    BlockBreakEvent.class);
        }

        public void handleBlockBreak(BlockBreakEvent event) {
            throw thrown;
        }

        @Override
        public double getChance() {
            return 0;
        }

        @Override
        public boolean randomExpGainChance() {
            return false;
        }

        @Override
        public void executeReward(@NotNull Player player, Object... skillData) {
        }
    }
}
