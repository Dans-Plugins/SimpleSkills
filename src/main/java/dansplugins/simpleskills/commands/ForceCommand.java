package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.abs.Skill;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class ForceCommand extends AbstractCommand {
    private final ArrayList<String> names = new ArrayList<>(Collections.singletonList("force"));
    private final ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("ss.force"));

    @Override
    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return permissions;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.RED + "Sub-commands: wipe, activate, deactivate");
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        String subCommand = args[0];

        switch(subCommand) {
            case "wipe":
                return forceWipe(commandSender);
            case "activate":
                if (args.length < 2) {
                    commandSender.sendMessage(ChatColor.RED + "Usage: /ss force activate (skillName)");
                    return false;
                }
                return forceActivate(commandSender, args[1]);
            case "deactivate":
                if (args.length < 2) {
                    commandSender.sendMessage(ChatColor.RED + "Usage: /ss force deactivate (skillName)");
                    return false;
                }
                return forceDeactivate(commandSender, args[1]);
            default:
                return execute(commandSender);
        }
    }

    private boolean forceWipe(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            commandSender.sendMessage(ChatColor.RED + "This command can only be used from the console.");
            return false;
        }
        if (!commandSender.hasPermission("ss.force.wipe")) {
            commandSender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return false;
        }
        PersistentData.getInstance().getPlayerRecords().clear();
        commandSender.sendMessage("Player records have been cleared.");
        return true;
    }

    private boolean forceActivate(CommandSender commandSender, String skillName) {
        Skill skill = PersistentData.getInstance().getSkill(skillName);
        if (skill == null) {
            commandSender.sendMessage(ChatColor.RED + "That skill wasn't found.");
            return false;
        }
        if (!commandSender.hasPermission("ss.force.activate")) {
            commandSender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return false;
        }
        skill.setActive(true);
        commandSender.sendMessage(ChatColor.RED + "The '" + skill.getName() + "' is now active.");
        return true;
    }

    private boolean forceDeactivate(CommandSender commandSender, String skillName) {
        Skill skill = PersistentData.getInstance().getSkill(skillName);
        if (skill == null) {
            commandSender.sendMessage(ChatColor.RED + "That skill wasn't found.");
            return false;
        }
        if (!commandSender.hasPermission("ss.force.deactivate")) {
            commandSender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return false;
        }
        skill.setActive(false);
        commandSender.sendMessage(ChatColor.RED + "The '" + skill.getName() + "' is now inactive.");
        return true;
    }
}