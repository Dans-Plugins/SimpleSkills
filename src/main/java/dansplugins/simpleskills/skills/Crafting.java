package dansplugins.simpleskills.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.data.objects.PlayerRecord;
import dansplugins.simpleskills.services.ConfigService;
import dansplugins.simpleskills.services.MessageService;
import dansplugins.simpleskills.skills.abs.AbstractSkill;
import dansplugins.simpleskills.utils.ChanceCalculator;
import dansplugins.simpleskills.utils.Logger;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Callum Johnson
 * @since 09/01/2022 - 17:04
 */
public class Crafting extends AbstractSkill {
    private final ChanceCalculator chanceCalculator;

    /**
     * The crafting skill is levelled through the usage of the crafting windows.
     */
    public Crafting(ConfigService configService, Logger logger, PersistentData persistentData, SimpleSkills simpleSkills, MessageService messageService, ChanceCalculator chanceCalculator) {
        super(configService, logger, persistentData, simpleSkills, messageService, "Crafting", CraftItemEvent.class);
        this.chanceCalculator = chanceCalculator;
    }

    /**
     * Method to get the chance of a skill incrementing or levelling.
     *
     * @return double chance (1-100).
     * @see #randomExpGainChance()
     */
    @Override
    public double getChance() {
        return 0;
    }

    /**
     * Method to determine if a skill is chance-incremented/levelled.
     *
     * @return {@code true} or {@code false}.
     */
    @Override
    public boolean randomExpGainChance() {
        return false;
    }

    /**
     * Method to track the craft event.
     *
     * @param event to track and handle.
     */
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        incrementExperience(((Player) event.getWhoClicked()));
        executeReward(((Player) event.getWhoClicked()), event.getRecipe());
    }

    /**
     * Method to reward the player at their level.
     *
     * @param player    to reward.
     * @param skillData assigned data to the skill reward, 'Block' for 'BlockSkills' etc.
     */
    @Override
    public void executeReward(@NotNull Player player, Object... skillData) {
        final PlayerRecord record = getRecord(player);
        if (record == null) return;
        if (skillData.length != 1) throw new IllegalArgumentException("Skill Data is not of length '1'");
        final Object createdData = skillData[0];
        if (!(createdData instanceof Recipe)) throw new IllegalArgumentException("SkillData[0] is not Recipe");
        final Recipe created = (Recipe) createdData;
        if (!(created instanceof ShapedRecipe) && !(created instanceof ShapelessRecipe)) return;
        if (!chanceCalculator.roll(record, this, 0.10)) return;
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
        final List<RecipeChoice> choices;
        if (created instanceof ShapedRecipe) {
            choices = new ArrayList<>(((ShapedRecipe) created).getChoiceMap().values());
        } else { // Shapeless
            choices = new ArrayList<>(((ShapelessRecipe) created).getChoiceList());
        }
        Collections.shuffle(choices);
        final RecipeChoice recipeChoice = choices.get(0);
        List<Material> materialChoices;
        if (recipeChoice instanceof RecipeChoice.MaterialChoice) {
            materialChoices = ((RecipeChoice.MaterialChoice) recipeChoice).getChoices();
        } else {
            materialChoices = ((RecipeChoice.ExactChoice) recipeChoice).getChoices().stream()
                    .map(ItemStack::getType)
                    .collect(Collectors.toList());
        }
        materialChoices = materialChoices.stream().filter(m -> !m.isAir()).collect(Collectors.toList());
        Collections.shuffle(materialChoices);
        final Material material = materialChoices.get(0);
        player.getInventory().addItem(new ItemStack(material, Math.random() > 0.5 ? 2 : 1));
        final String typeName = WordUtils.capitalizeFully(material.name().toLowerCase().replaceAll("_", " "));
        final boolean nRequired = "aeiou".contains(String.valueOf(typeName.toLowerCase().charAt(0)));
        player.sendMessage(messageService.convert(Objects.requireNonNull(Objects.requireNonNull(messageService.getlang().getString("Skills.Crafting"))
                .replaceAll("%typeName%", ((nRequired ? "n" : "") + "§a" + typeName)))));
    }

}
