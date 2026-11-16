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
import org.bukkit.util.Vector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
 * Characterizes which moves level {@link Boating}, the guards in front of its benefit roll, and
 * the reward after a successful roll.
 * <p>
 * The distance tally itself is covered by {@code AbstractMovementSkillTest}; these tests check
 * that Boating is wired to the boating tally and dispatches through {@link Boating#handle}.
 * Every move is one block along the X axis, so the tenth move in a boat is the rewarded one.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class BoatingTest {

    private static final UUID PLAYER_ID = UUID.fromString("88888888-8888-8888-8888-888888888888");

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
    private FileConfiguration lang;
    @Mock
    private Player player;
    @Mock
    private PlayerRecord playerRecord;
    @Mock
    private World world;
    @Mock
    private Location location;
    @Mock
    private Boat boat;
    @Mock
    private Creature horse;

    private Boating boating;
    private double x;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        boating = new Boating(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getLocation()).thenReturn(location);
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(playerRecord);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(false);
    }

    @Test
    public void getSkillType_isBoating() {
        assertEquals(AbstractMovementSkill.MovementSkillType.BOATING, boating.getSkillType());
    }

    @Test
    public void handle_grantsExperienceAndRollsTheBenefit_onTheTenthMoveInABoat() {
        ride(boat);

        moveTimes(9);
        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());

        moveTimes(1);
        verify(playerRecord, times(1)).incrementExperience(boating.getId());
        verify(chanceCalculator, times(1)).roll(eq(playerRecord), eq(boating), eq(0.10));
    }

    @Test
    public void handle_ignoresMovesOnACreature_whichLevelRidingInstead() {
        ride(horse);

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
    public void handle_ignoresMovesInABoat_whenTheSkillIsInactive() {
        ride(boat);
        boating.setActive(false);

        moveTimes(30);

        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresMovesInABoat_whenTheEventIsCancelled() {
        ride(boat);

        for (int i = 0; i < 30; i++) {
            final PlayerMoveEvent event = nextMove();
            event.setCancelled(true);
            boating.handle(event);
        }

        verify(playerRecord, never()).incrementExperience(anyInt());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("boatingBenefitEnabled"), anyBoolean())).thenReturn(false);

        boating.executeReward(player);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        boating.executeReward(player);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_doesNotTouchTheBoatOrPlayer_whenTheRollFails() {
        ride(boat);

        boating.executeReward(player);

        verify(chanceCalculator).roll(eq(playerRecord), eq(boating), eq(0.10));
        verifyNoInteractions(boat);
        verify(player, never()).playSound(any(Location.class), any(Sound.class), anyFloat(), anyFloat());
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void executeReward_doesNothingAfterASuccessfulRoll_whenTheVehicleIsNotABoat() {
        ride(horse);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(true);

        boating.executeReward(player);

        verify(chanceCalculator).roll(eq(playerRecord), eq(boating), eq(0.10));
        verifyNoInteractions(horse);
        verify(player, never()).playSound(any(Location.class), any(Sound.class), anyFloat(), anyFloat());
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void executeReward_quadruplesTheBoatsVelocityAndNotifiesThePlayer_whenTheRollSucceeds() {
        ride(boat);
        when(boat.getVelocity()).thenReturn(new Vector(0.5, 0, -0.25));
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(true);
        when(messageService.getlang()).thenReturn(lang);
        when(messageService.convert(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(lang.getString("Skills.Boating")).thenReturn("boat boost");

        boating.executeReward(player);

        final ArgumentCaptor<Vector> velocity = ArgumentCaptor.forClass(Vector.class);
        verify(boat).setVelocity(velocity.capture());
        assertEquals(new Vector(2, 0, -1), velocity.getValue());
        verify(player).sendMessage("boat boost");
        verify(player).playSound(eq(location), eq(Sound.ENTITY_BOAT_PADDLE_WATER), eq(5f), eq(2f));
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
            boating.handle(nextMove());
        }
    }

    private PlayerMoveEvent nextMove() {
        final Location from = new Location(world, x, 64, 0);
        x += 1;
        return new PlayerMoveEvent(player, from, new Location(world, x, 64, 0));
    }
}
