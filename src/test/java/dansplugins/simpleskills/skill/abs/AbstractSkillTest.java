package dansplugins.simpleskills.skill.abs;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link AbstractSkill}'s event plumbing: the reporting of a trigger failure,
 * which is raised through reflection and therefore hides the real failure unless the cause is
 * carried over to the rethrown exception, and the listener registration, which decides both
 * which events reach a skill and how many times each one does.
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
    @Mock
    private PluginManager pluginManager;
    @Mock
    private Entity damager;
    @Mock
    private Entity damagee;

    private ThrowingSkill skill;
    private CountingSkill countingSkill;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        skill = new ThrowingSkill(configService, log, playerRecordRepository, simpleSkills, messageService);
        countingSkill = new CountingSkill(configService, log, playerRecordRepository, simpleSkills, messageService,
                pluginManager);
    }

    @Test
    public void handle_carriesTheUnderlyingFailureAsTheCause_whenATriggerThrows() {
        try {
            skill.handle(new BlockBreakEvent(block, player));
            fail("Expected the failing trigger to be reported as an IllegalStateException");
        } catch (IllegalStateException exception) {
            assertEquals("Failed to trigger 'Throwing' with event 'BlockBreakEvent'!", exception.getMessage());
            assertSame(skill.thrown, exception.getCause());
        }
    }

    @Test
    public void handle_doesNotRetainTheEventItHandled() throws IllegalAccessException {
        final BlockBreakEvent event = new BlockBreakEvent(block, player);

        countingSkill.handle(event);

        assertEquals(1, countingSkill.blockBreaks);
        // A skill that remembers the events it has handled keeps every one of them — and
        // everything each one references, such as a block and a player — alive for as long as
        // the server runs, so no field of the skill may hold on to the event once it is handled.
        for (Field field : AbstractSkill.class.getDeclaredFields()) {
            field.setAccessible(true);
            final Object value = field.get(countingSkill);
            final String retained = "Field '" + field.getName() + "' retains the handled event";
            if (value instanceof Collection) {
                assertFalse(retained, ((Collection<?>) value).contains(event));
            }
            if (value instanceof Map) {
                assertFalse(retained, ((Map<?, ?>) value).containsKey(event));
                assertFalse(retained, ((Map<?, ?>) value).containsValue(event));
            }
        }
    }

    @Test
    public void register_registersOnlyTheEventsTheSkillHasATriggerMethodFor() {
        countingSkill.register();

        verify(pluginManager).registerEvent(eq(BlockBreakEvent.class), eq(countingSkill),
                eq(EventPriority.MONITOR), any(EventExecutor.class), eq(simpleSkills));
        verify(pluginManager).registerEvent(eq(EntityDamageEvent.class), eq(countingSkill),
                eq(EventPriority.MONITOR), any(EventExecutor.class), eq(simpleSkills));
        verify(pluginManager).registerEvent(eq(EntityDamageByEntityEvent.class), eq(countingSkill),
                eq(EventPriority.MONITOR), any(EventExecutor.class), eq(simpleSkills));
        verifyNoMoreInteractions(pluginManager);
    }

    @Test
    public void register_deliversAnEventToItsTriggerMethodOnlyOnce() throws EventException {
        countingSkill.register();
        final ArgumentCaptor<EventExecutor> executors = ArgumentCaptor.forClass(EventExecutor.class);
        verify(pluginManager, times(3)).registerEvent(any(), any(), any(), executors.capture(), any());

        // Bukkit keys its handler lists on whichever class declares one, so every registration
        // this skill made for a class sharing a handler list receives the same event. Passing the
        // event to all of them stands in for that delivery.
        final EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, damagee,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0);
        for (EventExecutor executor : executors.getAllValues()) {
            executor.execute(countingSkill, event);
        }

        assertEquals(1, countingSkill.attacks);
        assertEquals("A trigger declared for a superclass of the event must not be called",
                0, countingSkill.damages);
    }

    /**
     * Minimal concrete {@link AbstractSkill} counting the events its triggers receive, standing in
     * for any skill declaring triggers for event classes that share a handler list.
     */
    public static class CountingSkill extends AbstractSkill {
        final PluginManager pluginManager;
        int blockBreaks;
        int damages;
        int attacks;

        CountingSkill(ConfigService configService, Log log, PlayerRecordRepository playerRecordRepository,
                      SimpleSkills simpleSkills, MessageService messageService, PluginManager pluginManager) {
            super(configService, log, playerRecordRepository, simpleSkills, messageService, "Counting",
                    BlockBreakEvent.class, EntityDamageEvent.class, EntityDamageByEntityEvent.class);
            this.pluginManager = pluginManager;
        }

        public void handleBlockBreak(BlockBreakEvent event) {
            blockBreaks++;
        }

        public void handleDamage(EntityDamageEvent event) {
            damages++;
        }

        public void handleAttack(EntityDamageByEntityEvent event) {
            attacks++;
        }

        @Override
        PluginManager getPluginManager() {
            return pluginManager;
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
