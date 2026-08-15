package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
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
 * Characterizes which damage actually reaches {@link Hardiness}.
 * <p>
 * A skill's trigger is matched on the event's exact runtime class, so declaring only
 * {@link EntityDamageEvent} left damage dealt by an entity or by a block — which arrive as
 * {@link EntityDamageByEntityEvent} and {@link EntityDamageByBlockEvent} — reaching no trigger
 * at all, and being hit by a mob or another player therefore did nothing.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class HardinessTest {

    private static final UUID PLAYER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

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
    private Entity attacker;
    @Mock
    private Entity nonPlayer;
    @Mock
    private Block damagingBlock;
    @Mock
    private PlayerRecord playerRecord;

    private Hardiness hardiness;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        hardiness = new Hardiness(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(player.getType()).thenReturn(EntityType.PLAYER);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(nonPlayer.getType()).thenReturn(EntityType.ZOMBIE);
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(playerRecord);
        // The benefit roll failing keeps the reward off the sound and message path, which needs a
        // live server; that the roll is reached at all is what says the trigger ran.
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(false);
    }

    @Test
    public void handle_rewardsThePlayer_whenDamageHasNoSourceBehindIt() {
        hardiness.handle(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 4.0));

        verify(playerRecord).incrementExperience(hardiness.getId());
        verify(chanceCalculator).roll(eq(playerRecord), eq(hardiness), eq(0.10));
    }

    @Test
    public void handle_rewardsThePlayer_whenDamageIsDealtByAnEntity() {
        hardiness.handle(new EntityDamageByEntityEvent(attacker, player,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 4.0));

        verify(playerRecord).incrementExperience(hardiness.getId());
        verify(chanceCalculator).roll(eq(playerRecord), eq(hardiness), eq(0.10));
    }

    @Test
    public void handle_rewardsThePlayer_whenDamageIsDealtByABlock() {
        hardiness.handle(new EntityDamageByBlockEvent(damagingBlock, player,
                EntityDamageEvent.DamageCause.CONTACT, 1.0));

        verify(playerRecord).incrementExperience(hardiness.getId());
        verify(chanceCalculator).roll(eq(playerRecord), eq(hardiness), eq(0.10));
    }

    @Test
    public void handle_ignoresTheDamage_whenTheDamagedEntityIsNotAPlayer() {
        hardiness.handle(new EntityDamageByEntityEvent(attacker, nonPlayer,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 4.0));

        verify(playerRecordRepository, never()).getPlayerRecord(any());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }
}
