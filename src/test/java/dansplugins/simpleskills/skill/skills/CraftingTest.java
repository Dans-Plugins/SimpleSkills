package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link Crafting#executeReward(Player, Object...)}'s handling of a
 * craft with no formed recipe, which {@code CraftItemEvent#getRecipe()} can legitimately
 * report (e.g. certain merge/repair crafts) and previously threw an {@link IllegalArgumentException}.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class CraftingTest {

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
    private PlayerInventory playerInventory;
    @Mock
    private PlayerRecord playerRecord;

    private Crafting crafting;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        crafting = new Crafting(configService, log, playerRecordRepository, simpleSkills, messageService, chanceCalculator);

        when(player.getUniqueId()).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        when(player.getInventory()).thenReturn(playerInventory);
        when(playerRecordRepository.getPlayerRecord(any())).thenReturn(playerRecord);
    }

    @Test
    public void executeReward_skipsReward_whenRecipeIsNull() {
        crafting.executeReward(player, (Object) null);

        verify(chanceCalculator, never()).roll(any(), any(), anyDouble());
        verify(playerInventory, never()).addItem(any());
    }

    @Test
    public void executeReward_skipsReward_whenSkillDataIsNotARecipe() {
        crafting.executeReward(player, "not a recipe");

        verify(chanceCalculator, never()).roll(any(), any(), anyDouble());
        verify(playerInventory, never()).addItem(any());
    }
}
