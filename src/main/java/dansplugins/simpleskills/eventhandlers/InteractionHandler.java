package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.enums.SupportedBenefit;
import dansplugins.simpleskills.eventhandlers.abs.SkillHandler;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.objects.abs.*;
import dansplugins.simpleskills.utils.ChanceCalculator;
import dansplugins.simpleskills.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Stephenson
 */
public class InteractionHandler extends SkillHandler {

    @EventHandler()
    public void handle(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(player.getUniqueId());

        if (playerRecord == null) {
            Logger.getInstance().log("A player record wasn't found for " + player.getName() + ".");
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Material material = block.getType();
        if (material == Material.AIR) {
            return;
        }

        for (Skill skill : PersistentData.getInstance().getSkills()) {
            if (skill instanceof BlockInteractingSkill) {
                BlockSkill blockSkill = (BlockSkill) skill;

                // check if material is associated
                if (blockSkill.isMaterialAssociated(material)) {
                    // increment experience
                    playerRecord.incrementExperience(blockSkill.getID());
                }
            }
        }

    }

    @EventHandler()
    public void handle(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(player.getUniqueId());

        if (playerRecord == null) {
            Logger.getInstance().log("A player record wasn't found for " + player.getName() + ".");
            return;
        }

        Block block = event.getBlock();
        Material material = block.getType();

        for (Skill skill : PersistentData.getInstance().getSkills()) {
            if (skill instanceof BlockBreakingSkill) {
                BlockSkill blockSkill = (BlockSkill) skill;

                // check if material is associated
                if (blockSkill.isMaterialAssociated(material)) {
                    // increment experience
                    playerRecord.incrementExperience(blockSkill.getID());

                    // handle double drop benefit
                    if (blockSkill.hasBenefit(SupportedBenefit.RESOURCE_EXTRACTION.ordinal())) {
                        if (ChanceCalculator.getInstance().roll(playerRecord, blockSkill, 0.10)) {
                            player.getInventory().addItem(new ItemStack(material));
                            player.sendMessage(ChatColor.GREEN + "Due to your " + blockSkill.getName() + " skill, you manage to extract more resources than usual.");
                        }
                    }
                }
            }
        }
    }

    @EventHandler()
    public void handle(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(player.getUniqueId());

        if (playerRecord == null) {
            Logger.getInstance().log("A player record wasn't found for " + player.getName() + ".");
            return;
        }

        Block block = event.getBlock();
        Material material = block.getType();

        for (Skill skill : PersistentData.getInstance().getSkills()) {
            if (skill instanceof BlockPlacingSkill) {
                BlockSkill blockSkill = (BlockSkill) skill;

                // check if material is associated
                if (blockSkill.isMaterialAssociated(material)) {
                    // increment experience
                    playerRecord.incrementExperience(blockSkill.getID());
                }
            }
        }
    }

}