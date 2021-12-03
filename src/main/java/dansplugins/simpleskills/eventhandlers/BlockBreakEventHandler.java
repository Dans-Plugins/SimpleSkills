package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.enums.SupportedBenefit;
import dansplugins.simpleskills.eventhandlers.abs.SkillHandler;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.objects.abs.BlockBreakingSkill;
import dansplugins.simpleskills.objects.abs.Skill;
import dansplugins.simpleskills.objects.benefits.DoubleDrop;
import dansplugins.simpleskills.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Stephenson
 */
public class BlockBreakEventHandler extends SkillHandler {

    @EventHandler()
    public void handle(BlockBreakEvent event) {
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
                BlockBreakingSkill blockBreakingSkill = (BlockBreakingSkill) skill;

                // check if material is associated
                if (blockBreakingSkill.isMaterialAssociated(material)) {
                    // increment experience
                    playerRecord.incrementExperience(blockBreakingSkill.getID());

                    // handle double drop benefit
                    if (blockBreakingSkill.hasBenefit(SupportedBenefit.DOUBLE_DROP.ordinal())) {
                        if (DoubleDrop.roll(playerRecord, blockBreakingSkill)) {
                            player.getWorld().dropItem(player.getLocation(), new ItemStack(material));
                            player.sendMessage(ChatColor.GREEN + "You've experienced a double drop.");
                        }
                    }
                }
            }
        }
    }

}