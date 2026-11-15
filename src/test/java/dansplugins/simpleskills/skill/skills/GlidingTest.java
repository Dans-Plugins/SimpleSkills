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
import static org.mockito.Mockito.when;

/**
 * Characterizes which moves level {@link Gliding} and the guards in front of its benefit roll.
 * <p>
 * The distance tally itself is covered by {@code AbstractMovementSkillTest}; these tests check
 * that Gliding is wired to the gliding tally and dispatches through {@link Gliding#handle}.
 * Every move is one block along the X axis, so the eighteenth gliding move is the rewarded one.
 * The reward itself (a firework rocket, a sound and a message) is not exercised: the rocket is
 * built through {@code XMaterial}, which needs a running server, so every test here keeps the
 * benefit roll failing and treats reaching the roll as the sign that the reward was attempted.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class GlidingTest {

    private static final UUID PLAYER_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");

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

    private Gliding gliding;
    private double x;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        gliding = new Gliding(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(playerRecord);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(false);
    }

    @Test
    public void getSkillType_isGliding() {
        assertEquals(AbstractMovementSkill.MovementSkillType.GLIDING, gliding.getSkillType());
    }

    @Test
    public void handle_grantsExperienceAndRollsTheBenefit_onTheEighteenthGlidingMove() {
        when(player.isGliding()).thenReturn(true);

        moveTimes(17);
        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());

        moveTimes(1);
        verify(playerRecord, times(1)).incrementExperience(gliding.getId());
        verify(chanceCalculator, times(1)).roll(eq(playerRecord), eq(gliding), eq(0.10));
    }

    @Test
    public void handle_countsGlidingMoves_evenWhileThePlayerIsSprinting() {
        when(player.isGliding()).thenReturn(true);
        when(player.isSprinting()).thenReturn(true);

        moveTimes(18);

        verify(playerRecord, times(1)).incrementExperience(gliding.getId());
    }

    @Test
    public void handle_ignoresSprinting_whichLevelsCardioInstead() {
        when(player.isSprinting()).thenReturn(true);

        moveTimes(40);

        verify(playerRecord, never()).incrementExperience(anyInt());
    }

    @Test
    public void handle_ignoresGlidingMoves_whenTheSkillIsInactive() {
        when(player.isGliding()).thenReturn(true);
        gliding.setActive(false);

        moveTimes(40);

        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresGlidingMoves_whenTheEventIsCancelled() {
        when(player.isGliding()).thenReturn(true);

        for (int i = 0; i < 40; i++) {
            final PlayerMoveEvent event = nextMove();
            event.setCancelled(true);
            gliding.handle(event);
        }

        verify(playerRecord, never()).incrementExperience(anyInt());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("glidingBenefitEnabled"), anyBoolean())).thenReturn(false);

        gliding.executeReward(player);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        gliding.executeReward(player);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_doesNotTouchThePlayer_whenTheRollFails() {
        gliding.executeReward(player);

        verify(chanceCalculator).roll(eq(playerRecord), eq(gliding), eq(0.10));
        verify(player, never()).playSound(any(Location.class), any(Sound.class), anyFloat(), anyFloat());
        verify(player, never()).getInventory();
        verify(player, never()).sendMessage(anyString());
    }

    /**
     * Sends {@link #player} one block along the X axis the given number of times.
     */
    private void moveTimes(int times) {
        for (int i = 0; i < times; i++) {
            gliding.handle(nextMove());
        }
    }

    private PlayerMoveEvent nextMove() {
        final Location from = new Location(world, x, 64, 0);
        x += 1;
        return new PlayerMoveEvent(player, from, new Location(world, x, 64, 0));
    }
}
