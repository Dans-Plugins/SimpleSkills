package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes which hits level {@link Strength} and the guards in front of its benefit roll.
 * <p>
 * The reward itself (a Strength potion effect, a sound and a message) is not exercised: the
 * potion effect type is only registered by a running server, so every test here keeps the
 * benefit roll failing and treats reaching the roll as the sign that the reward was attempted.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class StrengthTest {

    private static final UUID PLAYER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

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
    private FileConfiguration fileConfiguration;
    @Mock
    private Player player;
    @Mock
    private Entity zombie;
    @Mock
    private Arrow arrow;
    @Mock
    private PlayerRecord playerRecord;

    private Strength strength;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        strength = new Strength(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(player.getType()).thenReturn(EntityType.PLAYER);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);
        when(arrow.getType()).thenReturn(EntityType.ARROW);
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(playerRecord);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(false);
    }

    @Test
    public void handle_grantsExperienceAndRollsTheBenefit_whenAPlayerHitsAnEntity() {
        strength.handle(new EntityDamageByEntityEvent(player, zombie,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 4.0));

        verify(playerRecord).incrementExperience(strength.getId());
        verify(chanceCalculator).roll(eq(playerRecord), eq(strength), eq(0.05));
    }

    @Test
    public void handle_ignoresTheHit_whenTheDamagerIsNotAPlayer() {
        strength.handle(new EntityDamageByEntityEvent(zombie, player,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 4.0));

        verify(playerRecordRepository, never()).getPlayerRecord(any());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresTheHit_whenThePlayerShotAProjectile() {
        // The damager of a ranged hit is the projectile, not the player who shot it.
        when(arrow.getShooter()).thenReturn(player);

        strength.handle(new EntityDamageByEntityEvent(arrow, zombie,
                EntityDamageEvent.DamageCause.PROJECTILE, 4.0));

        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresTheHit_whenTheEventIsCancelled() {
        final EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(player, zombie,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 4.0);
        event.setCancelled(true);

        strength.handle(event);

        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("strengthBenefitEnabled"), anyBoolean())).thenReturn(false);

        strength.executeReward(player, zombie);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        strength.executeReward(player, zombie);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheSkillDataIsNotAnEntity() {
        strength.executeReward(player, "not an entity");

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataOfAnotherLength() {
        strength.executeReward(player, zombie, zombie);
    }

    @Test
    public void executeReward_returnsBeforeCheckingTheSkillDataLength_whenThePlayerHasNoRecord() {
        // The record and benefit checks run first, so a malformed call is only rejected when
        // the reward would otherwise have been attempted.
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        strength.executeReward(player, zombie, zombie);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }
}
