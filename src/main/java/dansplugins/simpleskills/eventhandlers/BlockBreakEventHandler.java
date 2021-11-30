package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.objects.skills.Skill;
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

        Block block = event.getBlock();
        Material material = block.getType();

        Skill woodcutting = PersistentData.getInstance().getSkill(0);
        if (woodcutting.isMaterialAssociated(material)) {
            if (!playerRecord.isKnown(woodcutting)) {
                playerRecord.addKnownSkill(woodcutting);
                player.sendMessage(ChatColor.GREEN + "You've learned the " + woodcutting.getName() + " skill.");
            }
        }
    }

}