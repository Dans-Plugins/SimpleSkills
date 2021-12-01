package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.objects.skills.abs.Skill;
import dansplugins.simpleskills.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakEventHandler implements Listener {

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
            if (skill.isMaterialAssociated(material)) {
                if (!playerRecord.isKnown(skill)) {
                    playerRecord.addKnownSkill(skill);
                }
                else {
                    playerRecord.incrementExperience(skill.getID());
                }
            }
        }
    }

}