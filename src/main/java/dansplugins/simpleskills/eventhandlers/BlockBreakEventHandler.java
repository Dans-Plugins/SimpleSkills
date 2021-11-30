package dansplugins.simpleskills.eventhandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakEventHandler implements Listener {

    @EventHandler()
    public void handle(BlockBreakEvent event) {
        Player player = event.getPlayer();
        // TODO: implement
    }

}