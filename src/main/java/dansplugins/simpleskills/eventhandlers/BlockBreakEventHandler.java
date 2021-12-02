package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.eventhandlers.abs.SkillHandler;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.objects.skills.abs.BlockBreakingSkill;
import dansplugins.simpleskills.objects.skills.abs.Skill;
import dansplugins.simpleskills.utils.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

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
                    playerRecord.incrementExperience(blockBreakingSkill.getID());
                }
            }
        }
    }

}