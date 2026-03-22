package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the Boating skill fix.
 * These tests prove that the momentum loss issue is fixed by using potion effects
 * instead of direct velocity manipulation.
 */
@RunWith(MockitoJUnitRunner.class)
public class BoatingTest {

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
    private ChanceCalculator chanceCalculator;
    
    @Mock
    private Player player;
    
    @Mock
    private Boat boat;
    
    @Mock
    private PlayerRecord playerRecord;
    
    @Mock
    private Location location;
    
    @Mock
    private YamlConfiguration yamlConfiguration;
    
    private Boating boatingSkill;

    @Before
    public void setUp() {
        boatingSkill = new Boating(configService, log, playerRecordRepository, simpleSkills, messageService, chanceCalculator);
        
        // Setup default mocks
        when(playerRecordRepository.getPlayerRecord(any())).thenReturn(playerRecord);
        when(messageService.getlang()).thenReturn(yamlConfiguration);
        when(yamlConfiguration.getString("Skills.Boating")).thenReturn("&bWhoosh! Your boating skill has trained you in the ways of the water!");
        when(messageService.convert(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(player.getLocation()).thenReturn(location);
    }

    /**
     * Test that the executeReward method applies a Speed potion effect
     * instead of manipulating boat velocity (proving the fix).
     */
    @Test
    public void testExecuteReward_AppliesPotionEffect_NotVelocityManipulation() {
        // Arrange
        when(chanceCalculator.roll(any(PlayerRecord.class), any(Boating.class), eq(0.10))).thenReturn(true);
        when(player.getVehicle()).thenReturn(boat);
        when(player.hasPotionEffect(PotionEffectType.SPEED)).thenReturn(false);
        
        // Act
        boatingSkill.executeReward(player);
        
        // Assert - Verify potion effect is applied
        ArgumentCaptor<PotionEffect> potionCaptor = ArgumentCaptor.forClass(PotionEffect.class);
        verify(player).addPotionEffect(potionCaptor.capture());
        
        PotionEffect appliedEffect = potionCaptor.getValue();
        assertEquals("Speed effect should be applied", PotionEffectType.SPEED, appliedEffect.getType());
        assertEquals("Effect duration should be 600 ticks (30 seconds)", 600, appliedEffect.getDuration());
        assertEquals("Effect amplifier should be 1 (Speed II)", 1, appliedEffect.getAmplifier());
        assertTrue("Effect should be ambient", appliedEffect.isAmbient());
        assertFalse("Effect should not show particles", appliedEffect.hasParticles());
        
        // Assert - Verify boat velocity is NOT manipulated (proving the fix)
        verify(boat, never()).setVelocity(any(Vector.class));
        verify(boat, never()).getVelocity();
    }

    /**
     * Test that existing Speed effects are removed before applying new ones.
     */
    @Test
    public void testExecuteReward_RemovesExistingSpeedEffect() {
        // Arrange
        when(chanceCalculator.roll(any(PlayerRecord.class), any(Boating.class), eq(0.10))).thenReturn(true);
        when(player.getVehicle()).thenReturn(boat);
        when(player.hasPotionEffect(PotionEffectType.SPEED)).thenReturn(true);
        
        // Act
        boatingSkill.executeReward(player);
        
        // Assert
        verify(player).removePotionEffect(PotionEffectType.SPEED);
        verify(player).addPotionEffect(any(PotionEffect.class));
    }

    /**
     * Test that no reward is given when player record is null.
     */
    @Test
    public void testExecuteReward_NoReward_WhenPlayerRecordIsNull() {
        // Arrange
        when(playerRecordRepository.getPlayerRecord(any())).thenReturn(null);
        
        // Act
        boatingSkill.executeReward(player);
        
        // Assert
        verify(player, never()).addPotionEffect(any(PotionEffect.class));
        verify(player, never()).sendMessage(anyString());
        verify(boat, never()).setVelocity(any(Vector.class));
    }

    /**
     * Test that no reward is given when chance roll fails.
     */
    @Test
    public void testExecuteReward_NoReward_WhenChanceRollFails() {
        // Arrange
        when(chanceCalculator.roll(any(PlayerRecord.class), any(Boating.class), eq(0.10))).thenReturn(false);
        when(player.getVehicle()).thenReturn(boat);
        
        // Act
        boatingSkill.executeReward(player);
        
        // Assert
        verify(player, never()).addPotionEffect(any(PotionEffect.class));
        verify(player, never()).sendMessage(anyString());
        verify(boat, never()).setVelocity(any(Vector.class));
    }

    /**
     * Test that no reward is given when player is not in a boat.
     */
    @Test
    public void testExecuteReward_NoReward_WhenPlayerNotInBoat() {
        // Arrange
        when(chanceCalculator.roll(any(PlayerRecord.class), any(Boating.class), eq(0.10))).thenReturn(true);
        when(player.getVehicle()).thenReturn(null); // Not in a vehicle
        
        // Act
        boatingSkill.executeReward(player);
        
        // Assert
        verify(player, never()).addPotionEffect(any(PotionEffect.class));
        verify(player, never()).sendMessage(anyString());
    }

    /**
     * Test that the reward includes message and sound effects.
     */
    @Test
    public void testExecuteReward_SendsMessageAndPlaysSound() {
        // Arrange
        when(chanceCalculator.roll(any(PlayerRecord.class), any(Boating.class), eq(0.10))).thenReturn(true);
        when(player.getVehicle()).thenReturn(boat);
        when(player.hasPotionEffect(PotionEffectType.SPEED)).thenReturn(false);
        
        // Act
        boatingSkill.executeReward(player);
        
        // Assert
        verify(player).sendMessage(anyString());
        verify(player).playSound(eq(location), eq(Sound.ENTITY_BOAT_PADDLE_WATER), eq(5f), eq(2f));
    }

    /**
     * Test that the skill type is correctly identified as BOATING.
     */
    @Test
    public void testGetSkillType_ReturnsBoating() {
        // Act
        var skillType = boatingSkill.getSkillType();
        
        // Assert
        assertEquals("Skill type should be BOATING", 
                     Boating.MovementSkillType.BOATING, skillType);
    }

    /**
     * Test that chance-based experience gain is not used (returns false).
     */
    @Test
    public void testRandomExpGainChance_ReturnsFalse() {
        // Act
        boolean result = boatingSkill.randomExpGainChance();
        
        // Assert
        assertFalse("Random exp gain chance should be false", result);
    }

    /**
     * Test that getChance returns 0 (no random chance).
     */
    @Test
    public void testGetChance_ReturnsZero() {
        // Act
        double chance = boatingSkill.getChance();
        
        // Assert
        assertEquals("Chance should be 0", 0.0, chance, 0.001);
    }

    /**
     * Integration test: Verify the complete flow matches Cardio skill pattern.
     * This proves the fix aligns with the suggested solution in the issue.
     */
    @Test
    public void testExecuteReward_MatchesCardioSkillPattern() {
        // Arrange
        when(chanceCalculator.roll(any(PlayerRecord.class), any(Boating.class), eq(0.10))).thenReturn(true);
        when(player.getVehicle()).thenReturn(boat);
        when(player.hasPotionEffect(PotionEffectType.SPEED)).thenReturn(false);
        
        // Act
        boatingSkill.executeReward(player);
        
        // Assert - Verify it uses the same pattern as Cardio skill:
        // 1. Check for existing Speed effect
        verify(player).hasPotionEffect(PotionEffectType.SPEED);
        
        // 2. Apply Speed potion effect with same parameters as Cardio
        ArgumentCaptor<PotionEffect> potionCaptor = ArgumentCaptor.forClass(PotionEffect.class);
        verify(player).addPotionEffect(potionCaptor.capture());
        
        PotionEffect effect = potionCaptor.getValue();
        assertEquals("Should use Speed effect like Cardio", PotionEffectType.SPEED, effect.getType());
        assertEquals("Should use same duration as Cardio (600 ticks)", 600, effect.getDuration());
        assertEquals("Should use same amplifier as Cardio (level 1)", 1, effect.getAmplifier());
        
        // 3. Verify no direct velocity manipulation (the key fix)
        verify(boat, never()).setVelocity(any(Vector.class));
    }
}
