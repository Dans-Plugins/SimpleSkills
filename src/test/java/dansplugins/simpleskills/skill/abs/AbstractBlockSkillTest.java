package dansplugins.simpleskills.skill.abs;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link AbstractBlockSkill#handle(BlockBreakEvent)}'s player-placed-block
 * check, which prevents the exploit of placing a Silk Touch-harvested (or otherwise
 * obtained) skill-tracked block back down and re-mining it for repeated experience.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class AbstractBlockSkillTest {

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
    private PlayerRecord playerRecord;

    private TestBlockSkill skill;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        skill = new TestBlockSkill(configService, log, playerRecordRepository, simpleSkills, messageService);

        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getUniqueId()).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        when(playerRecordRepository.getPlayerRecord(any())).thenReturn(playerRecord);
    }

    @Test
    public void handle_grantsExperienceAndReward_whenBlockWasNotPlayerPlaced() {
        when(block.hasMetadata("simpleskills-player-placed")).thenReturn(false);
        final BlockBreakEvent event = new BlockBreakEvent(block, player);

        skill.handle(event);

        verify(playerRecord).incrementExperience(anyInt());
        assertTrue(skill.rewardExecuted);
    }

    @Test
    public void handle_skipsExperienceAndReward_whenBlockWasPlayerPlaced() {
        when(block.hasMetadata("simpleskills-player-placed")).thenReturn(true);
        final BlockBreakEvent event = new BlockBreakEvent(block, player);

        skill.handle(event);

        verify(playerRecord, never()).incrementExperience(anyInt());
        assertFalse(skill.rewardExecuted);
    }

    /**
     * Minimal concrete {@link AbstractBlockSkill} that always matches, so tests exercise
     * only the player-placed-block check rather than any single real skill's own filtering.
     */
    private static class TestBlockSkill extends AbstractBlockSkill {
        boolean rewardExecuted = false;

        TestBlockSkill(ConfigService configService, Log log, PlayerRecordRepository playerRecordRepository,
                        SimpleSkills simpleSkills, MessageService messageService) {
            super(configService, log, playerRecordRepository, simpleSkills, messageService, "TestBlock");
        }

        @Override
        public boolean isRequiredItem(@NotNull ItemStack item, @NotNull Block targetBlock, @NotNull String context) {
            return true;
        }

        @Override
        public boolean isItemRequired() {
            return false;
        }

        @Override
        public @NotNull BlockSkillType getBlockSkillType() {
            return BlockSkillType.BREAK_SPECIFIC;
        }

        @Override
        public boolean isValidMaterial(@NotNull Material material) {
            return true;
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
            rewardExecuted = true;
        }
    }
}
