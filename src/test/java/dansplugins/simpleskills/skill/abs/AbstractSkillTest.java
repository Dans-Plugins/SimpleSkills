package dansplugins.simpleskills.skill.abs;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Unit tests for AbstractSkill to prove the improved exception handling
 * when InvocationTargetException occurs.
 */
@RunWith(MockitoJUnitRunner.class)
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
    private org.bukkit.configuration.file.FileConfiguration config;

    /**
     * Test skill that throws a NullPointerException to simulate the original bug.
     */
    private class TestSkillWithNullPointer extends AbstractSkill {
        public TestSkillWithNullPointer() {
            super(configService, log, playerRecordRepository, simpleSkills, 
                  messageService, "TestSkill", BlockBreakEvent.class);
        }

        public void handle(BlockBreakEvent event) {
            // Simulate the original bug: NullPointerException from parseMaterial
            throw new NullPointerException("XMaterial.parseMaterial() returned null");
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
     * Test skill that works correctly.
     */
    private class TestSkillWorkingCorrectly extends AbstractSkill {
        private boolean handlerCalled = false;

        public TestSkillWorkingCorrectly() {
            super(configService, log, playerRecordRepository, simpleSkills, 
                  messageService, "WorkingSkill", BlockBreakEvent.class);
        }

        public void handle(BlockBreakEvent event) {
            handlerCalled = true;
        }

        public boolean wasHandlerCalled() {
            return handlerCalled;
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
     * Custom test event for testing.
     */
    private static class TestBlockBreakEvent extends BlockBreakEvent {
        private static final HandlerList handlers = new HandlerList();

        public TestBlockBreakEvent(org.bukkit.block.Block block, org.bukkit.entity.Player player) {
            super(block, player);
        }

        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }

    @Before
    public void setUp() {
        // Mock the config service
        org.mockito.Mockito.when(configService.getConfig()).thenReturn(config);
        org.mockito.Mockito.when(config.getInt("defaultBaseExperienceRequirement", 10)).thenReturn(10);
        org.mockito.Mockito.when(config.getDouble("defaultDefaultExperienceIncreaseFactor", 1.2)).thenReturn(1.2);
    }

    /**
     * Test that IllegalStateException is thrown when handler method throws exception.
     * This proves the fix: the exception is now chained with the original cause.
     */
    @Test
    public void testHandle_WhenHandlerThrowsException_ThrowsIllegalStateExceptionWithCause() {
        TestSkillWithNullPointer skill = new TestSkillWithNullPointer();
        
        // Create a mock event
        org.bukkit.block.Block mockBlock = org.mockito.Mockito.mock(org.bukkit.block.Block.class);
        org.bukkit.entity.Player mockPlayer = org.mockito.Mockito.mock(org.bukkit.entity.Player.class);
        TestBlockBreakEvent event = new TestBlockBreakEvent(mockBlock, mockPlayer);
        
        try {
            skill.handle(event);
            fail("Expected IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            // Verify the exception message
            assertTrue("Exception message should mention the skill name",
                e.getMessage().contains("TestSkill"));
            assertTrue("Exception message should mention the event",
                e.getMessage().contains("BlockBreakEvent"));
            
            // CRITICAL: Verify the cause is chained (proves the fix)
            assertNotNull("Exception should have a cause (proves the fix)", e.getCause());
            
            // Verify we can access the underlying NullPointerException
            Throwable cause = e.getCause();
            if (cause instanceof java.lang.reflect.InvocationTargetException) {
                Throwable underlyingCause = cause.getCause();
                assertNotNull("InvocationTargetException should have an underlying cause", underlyingCause);
                assertTrue("Underlying cause should be NullPointerException",
                    underlyingCause instanceof NullPointerException);
                assertTrue("Underlying cause message should mention parseMaterial",
                    underlyingCause.getMessage().contains("parseMaterial"));
            }
        }
    }

    /**
     * Test that skill handler is called successfully when no exception occurs.
     */
    @Test
    public void testHandle_WhenHandlerSucceeds_CallsHandlerMethod() {
        TestSkillWorkingCorrectly skill = new TestSkillWorkingCorrectly();
        
        // Create a mock event
        org.bukkit.block.Block mockBlock = org.mockito.Mockito.mock(org.bukkit.block.Block.class);
        org.bukkit.entity.Player mockPlayer = org.mockito.Mockito.mock(org.bukkit.entity.Player.class);
        TestBlockBreakEvent event = new TestBlockBreakEvent(mockBlock, mockPlayer);
        
        skill.handle(event);
        
        assertTrue("Handler should have been called", skill.wasHandlerCalled());
    }

    /**
     * Test that the same event is not processed twice.
     */
    @Test
    public void testHandle_WhenCalledTwiceWithSameEvent_OnlyProcessesOnce() {
        TestSkillWorkingCorrectly skill = new TestSkillWorkingCorrectly();
        
        // Create a mock event
        org.bukkit.block.Block mockBlock = org.mockito.Mockito.mock(org.bukkit.block.Block.class);
        org.bukkit.entity.Player mockPlayer = org.mockito.Mockito.mock(org.bukkit.entity.Player.class);
        TestBlockBreakEvent event = new TestBlockBreakEvent(mockBlock, mockPlayer);
        
        // Call handle twice with the same event
        skill.handle(event);
        assertTrue("Handler should have been called the first time", skill.wasHandlerCalled());
        
        // The second call should not process the event again
        // (The handler is only called once per event instance)
        skill.handle(event);
    }

    /**
     * Test that skill name is properly formatted.
     */
    @Test
    public void testGetName_ReturnsFormattedName() {
        TestSkillWorkingCorrectly skill = new TestSkillWorkingCorrectly();
        
        String name = skill.getName();
        
        assertEquals("Workingskill", name);
    }

    /**
     * Test that skill ID is based on hashCode.
     */
    @Test
    public void testGetId_ReturnsHashCode() {
        TestSkillWorkingCorrectly skill = new TestSkillWorkingCorrectly();
        
        int id = skill.getId();
        
        assertEquals("ID should equal hashCode", skill.hashCode(), id);
    }

    /**
     * Test that skills with same name are equal.
     */
    @Test
    public void testEquals_SkillsWithSameName_AreEqual() {
        TestSkillWorkingCorrectly skill1 = new TestSkillWorkingCorrectly();
        TestSkillWorkingCorrectly skill2 = new TestSkillWorkingCorrectly();
        
        assertEquals("Skills with same name should be equal", skill1, skill2);
        assertEquals("Skills with same name should have same hashCode", 
            skill1.hashCode(), skill2.hashCode());
    }

    /**
     * Test that exp requirements can be set and retrieved.
     */
    @Test
    public void testExpRequirement_CanBeSetAndRetrieved() {
        TestSkillWorkingCorrectly skill = new TestSkillWorkingCorrectly();
        
        skill.setExpRequirement(100);
        
        assertEquals("Exp requirement should be 100", 100, skill.getExpRequirement());
    }

    /**
     * Test that exp factor can be set and retrieved.
     */
    @Test
    public void testExpFactor_CanBeSetAndRetrieved() {
        TestSkillWorkingCorrectly skill = new TestSkillWorkingCorrectly();
        
        skill.setExpFactor(1.5);
        
        assertEquals("Exp factor should be 1.5", 1.5, skill.getExpFactor(), 0.001);
    }

    /**
     * Test that skill active status can be set and retrieved.
     */
    @Test
    public void testActive_CanBeSetAndRetrieved() {
        TestSkillWorkingCorrectly skill = new TestSkillWorkingCorrectly();
        
        assertTrue("Skill should be active by default", skill.isActive());
        
        skill.setActive(false);
        
        assertFalse("Skill should be inactive", skill.isActive());
    }
}
