package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.skill.abs.AbstractMovementSkill;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Characterizes which moves level {@link Riding} and the guards in front of its benefit roll.
 * <p>
 * The distance tally itself is covered by {@code AbstractMovementSkillTest}; these tests check
 * that Riding is wired to the riding tally and dispatches through {@link Riding#handle}.
 * Every move is one block along the X axis, so the tenth move on a creature is the rewarded one.
 * The reward itself (a Speed potion effect on the creature, a sound and a message) is not
 * exercised: the potion effect type is only registered by a running server, so the tests either
 * keep the benefit roll failing or let it pass for a player who is not riding a creature, which
 * returns before the effect is looked up.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class RidingTest {

    private static final UUID PLAYER_ID = UUID.fromString("77777777-7777-7777-7777-777777777777");

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
    private PlayerRecord playerRecord;
    @Mock
    private World world;
    @Mock
    private Creature horse;
    @Mock
    private Boat boat;

    private Riding riding;
    private double x;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        riding = new Riding(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(playerRecord);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(false);
    }

    @Test
    public void getSkillType_isRiding() {
        assertEquals(AbstractMovementSkill.MovementSkillType.RIDING, riding.getSkillType());
    }

    @Test
    public void handle_grantsExperienceAndRollsTheBenefit_onTheTenthMoveOnACreature() {
        ride(horse);

        moveTimes(9);
        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());

        moveTimes(1);
        verify(playerRecord, times(1)).incrementExperience(riding.getId());
        verify(chanceCalculator, times(1)).roll(eq(playerRecord), eq(riding), eq(0.10));
    }

    @Test
    public void handle_ignoresMovesInABoat_whichLevelBoatingInstead() {
        ride(boat);

        moveTimes(30);

        verify(playerRecord, never()).incrementExperience(anyInt());
    }

    @Test
    public void handle_ignoresMovesOnFoot() {
        when(player.isSprinting()).thenReturn(true);

        moveTimes(30);

        verify(playerRecord, never()).incrementExperience(anyInt());
    }

    @Test
    public void handle_ignoresMovesOnACreature_whenTheSkillIsInactive() {
        ride(horse);
        riding.setActive(false);

        moveTimes(30);

        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresMovesOnACreature_whenTheEventIsCancelled() {
        ride(horse);

        for (int i = 0; i < 30; i++) {
            final PlayerMoveEvent event = nextMove();
            event.setCancelled(true);
            riding.handle(event);
        }

        verify(playerRecord, never()).incrementExperience(anyInt());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("ridingBenefitEnabled"), anyBoolean())).thenReturn(false);

        riding.executeReward(player);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        riding.executeReward(player);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_doesNotTouchTheCreatureOrPlayer_whenTheRollFails() {
        ride(horse);

        riding.executeReward(player);

        verify(chanceCalculator).roll(eq(playerRecord), eq(riding), eq(0.10));
        verifyNoInteractions(horse);
        verify(player, never()).playSound(any(Location.class), any(Sound.class), anyFloat(), anyFloat());
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void executeReward_doesNothingAfterASuccessfulRoll_whenTheVehicleIsNotACreature() {
        ride(boat);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(true);

        riding.executeReward(player);

        verify(chanceCalculator).roll(eq(playerRecord), eq(riding), eq(0.10));
        verify(player, never()).playSound(any(Location.class), any(Sound.class), anyFloat(), anyFloat());
        verify(player, never()).sendMessage(anyString());
    }

    private void ride(org.bukkit.entity.Entity vehicle) {
        when(player.isInsideVehicle()).thenReturn(true);
        when(player.getVehicle()).thenReturn(vehicle);
    }

    /**
     * Sends {@link #player} one block along the X axis the given number of times.
     */
    private void moveTimes(int times) {
        for (int i = 0; i < times; i++) {
            riding.handle(nextMove());
        }
    }

    private PlayerMoveEvent nextMove() {
        final Location from = new Location(world, x, 64, 0);
        x += 1;
        return new PlayerMoveEvent(player, from, new Location(world, x, 64, 0));
    }
}
