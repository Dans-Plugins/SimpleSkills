package dansplugins.simpleskills.skill.abs;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link AbstractMovementSkill#onMove(PlayerMoveEvent)}: which moves count
 * towards a movement skill, and how far a player has to travel before one is rewarded.
 * <p>
 * Every move in these tests is one block along the X axis, so each adds a squared distance
 * of {@code 1.0}. A skill's tally is started by the first counted move and grows with each
 * further move while it is at or below the skill type's threshold; the move made once it is
 * above the threshold grants the experience and reward and clears the tally, without its own
 * distance being added.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class AbstractMovementSkillTest {

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
    private PlayerRecord playerRecord;
    @Mock
    private World world;

    private double x;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getUniqueId()).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        when(playerRecordRepository.getPlayerRecord(any())).thenReturn(playerRecord);
    }

    // Sprinting (threshold 4.0)

    @Test
    public void onMove_sprinting_grantsOnSixthMoveOfOneBlock() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.SPRINTING);
        when(player.isSprinting()).thenReturn(true);

        moveTimes(skill, 5);
        assertNotRewarded(skill);

        moveTimes(skill, 1);
        assertRewardedTimes(skill, 1);
    }

    @Test
    public void onMove_sprinting_startsANewTallyAfterRewarding() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.SPRINTING);
        when(player.isSprinting()).thenReturn(true);

        moveTimes(skill, 11);
        assertRewardedTimes(skill, 1);

        moveTimes(skill, 1);
        assertRewardedTimes(skill, 2);
    }

    @Test
    public void onMove_sprinting_ignoresPlayerWhoIsNotSprinting() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.SPRINTING);
        when(player.isSprinting()).thenReturn(false);

        moveTimes(skill, 20);

        assertNotRewarded(skill);
    }

    @Test
    public void onMove_sprinting_ignoresSprintingPlayerInsideAVehicle() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.SPRINTING);
        when(player.isSprinting()).thenReturn(true);
        when(player.isInsideVehicle()).thenReturn(true);

        moveTimes(skill, 20);

        assertNotRewarded(skill);
    }

    @Test
    public void onMove_sprinting_ignoresSprintingPlayerWhoIsSwimmingFlyingOrGliding() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.SPRINTING);
        when(player.isSprinting()).thenReturn(true);

        when(player.isSwimming()).thenReturn(true);
        moveTimes(skill, 20);
        when(player.isSwimming()).thenReturn(false);
        when(player.isFlying()).thenReturn(true);
        moveTimes(skill, 20);
        when(player.isFlying()).thenReturn(false);
        when(player.isGliding()).thenReturn(true);
        moveTimes(skill, 20);

        assertNotRewarded(skill);
    }

    // Swimming (threshold 1.0)

    @Test
    public void onMove_swimming_grantsOnThirdMoveOfOneBlock() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.SWIMMING);
        when(player.isSwimming()).thenReturn(true);

        moveTimes(skill, 2);
        assertNotRewarded(skill);

        moveTimes(skill, 1);
        assertRewardedTimes(skill, 1);
    }

    @Test
    public void onMove_swimming_ignoresPlayerWhoIsNotSwimming() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.SWIMMING);
        when(player.isSwimming()).thenReturn(false);

        moveTimes(skill, 20);

        assertNotRewarded(skill);
    }

    // Gliding (threshold 16.0)

    @Test
    public void onMove_gliding_grantsOnEighteenthMoveOfOneBlock() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.GLIDING);
        when(player.isGliding()).thenReturn(true);

        moveTimes(skill, 17);
        assertNotRewarded(skill);

        moveTimes(skill, 1);
        assertRewardedTimes(skill, 1);
    }

    @Test
    public void onMove_gliding_ignoresPlayerWhoIsNotGliding() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.GLIDING);
        when(player.isGliding()).thenReturn(false);

        moveTimes(skill, 40);

        assertNotRewarded(skill);
    }

    // Riding (threshold 8.0)

    @Test
    public void onMove_riding_grantsOnTenthMoveOfOneBlockOnACreature() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.RIDING);
        when(player.isInsideVehicle()).thenReturn(true);
        when(player.getVehicle()).thenReturn(mock(Creature.class));

        moveTimes(skill, 9);
        assertNotRewarded(skill);

        moveTimes(skill, 1);
        assertRewardedTimes(skill, 1);
    }

    @Test
    public void onMove_riding_ignoresPlayerInABoat() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.RIDING);
        when(player.isInsideVehicle()).thenReturn(true);
        when(player.getVehicle()).thenReturn(mock(Boat.class));

        moveTimes(skill, 20);

        assertNotRewarded(skill);
    }

    // Boating (threshold 8.0)

    @Test
    public void onMove_boating_grantsOnTenthMoveOfOneBlockInABoat() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.BOATING);
        when(player.isInsideVehicle()).thenReturn(true);
        when(player.getVehicle()).thenReturn(mock(Boat.class));

        moveTimes(skill, 9);
        assertNotRewarded(skill);

        moveTimes(skill, 1);
        assertRewardedTimes(skill, 1);
    }

    @Test
    public void onMove_boating_ignoresPlayerOnACreature() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.BOATING);
        when(player.isInsideVehicle()).thenReturn(true);
        when(player.getVehicle()).thenReturn(mock(Creature.class));

        moveTimes(skill, 20);

        assertNotRewarded(skill);
    }

    // Guards shared by every skill type

    @Test
    public void onMove_ignoresPlayerNotInSurvival() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.SPRINTING);
        when(player.isSprinting()).thenReturn(true);
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);

        moveTimes(skill, 20);

        assertNotRewarded(skill);
    }

    @Test
    public void onMove_ignoresEventWithoutDestination() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.SPRINTING);
        when(player.isSprinting()).thenReturn(true);

        for (int i = 0; i < 20; i++) {
            skill.onMove(new PlayerMoveEvent(player, new Location(world, 0, 64, 0), null));
        }

        assertNotRewarded(skill);
    }

    @Test
    public void onMove_ignoresMovesOfLessThanAQuarterBlockOnEveryAxis() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.SWIMMING);
        when(player.isSwimming()).thenReturn(true);

        // Squared distance 0.1728 per move: enough for swimming's tally to pass its threshold
        // within seven moves if counted, but no single axis changes by a quarter block.
        for (int i = 0; i < 20; i++) {
            skill.onMove(new PlayerMoveEvent(player,
                    new Location(world, 0, 64, 0), new Location(world, 0.24, 64.24, 0.24)));
        }

        assertNotRewarded(skill);
    }

    @Test
    public void onMove_countsAMoveOfAQuarterBlockOnOneAxis() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.SWIMMING);
        when(player.isSwimming()).thenReturn(true);

        // Squared distance 0.0625 per move, so the tally passes 1.0 on the seventeenth move
        // and the eighteenth is rewarded.
        for (int i = 0; i < 18; i++) {
            skill.onMove(new PlayerMoveEvent(player,
                    new Location(world, 0, 64, 0), new Location(world, 0.25, 64, 0)));
        }

        assertRewardedTimes(skill, 1);
    }

    @Test
    public void onMove_keepsASeparateTallyForEachPlayer() {
        final TestMovementSkill skill = skillOf(AbstractMovementSkill.MovementSkillType.SWIMMING);
        when(player.isSwimming()).thenReturn(true);
        final Player otherPlayer = mock(Player.class);
        when(otherPlayer.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(otherPlayer.getUniqueId()).thenReturn(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        when(otherPlayer.isSwimming()).thenReturn(true);

        // Alternating players, two moves each: a shared tally would already have rewarded the
        // third event, but each player's own tally is still below the threshold.
        for (int i = 0; i < 2; i++) {
            moveTimes(skill, 1);
            skill.onMove(new PlayerMoveEvent(otherPlayer,
                    new Location(world, 0, 64, 0), new Location(world, 1, 64, 0)));
        }
        assertNotRewarded(skill);

        moveTimes(skill, 1);
        assertRewardedTimes(skill, 1);
    }

    // Helpers

    private TestMovementSkill skillOf(AbstractMovementSkill.MovementSkillType type) {
        return new TestMovementSkill(configService, log, playerRecordRepository, simpleSkills, messageService, type);
    }

    /**
     * Moves {@link #player} one block along the X axis the given number of times.
     */
    private void moveTimes(TestMovementSkill skill, int times) {
        for (int i = 0; i < times; i++) {
            final Location from = new Location(world, x, 64, 0);
            x += 1;
            final Location to = new Location(world, x, 64, 0);
            skill.onMove(new PlayerMoveEvent(player, from, to));
        }
    }

    private void assertNotRewarded(TestMovementSkill skill) {
        verify(playerRecord, never()).incrementExperience(anyInt());
        assertEquals(0, skill.rewardsExecuted);
    }

    private void assertRewardedTimes(TestMovementSkill skill, int times) {
        verify(playerRecord, times(times)).incrementExperience(skill.getId());
        assertEquals(times, skill.rewardsExecuted);
    }

    /**
     * Minimal concrete {@link AbstractMovementSkill} of a chosen type with no random
     * experience chance, so tests exercise only the movement tally.
     */
    private static class TestMovementSkill extends AbstractMovementSkill {
        private final MovementSkillType type;
        int rewardsExecuted = 0;

        TestMovementSkill(ConfigService configService, Log log, PlayerRecordRepository playerRecordRepository,
                          SimpleSkills simpleSkills, MessageService messageService, MovementSkillType type) {
            super(configService, log, playerRecordRepository, simpleSkills, messageService, "TestMovement");
            this.type = type;
        }

        @Override
        public MovementSkillType getSkillType() {
            return type;
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
            rewardsExecuted++;
        }
    }
}
