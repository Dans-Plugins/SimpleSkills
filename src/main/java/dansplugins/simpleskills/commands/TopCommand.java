package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.objects.abs.Skill;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.spigot.tools.UUIDChecker;

import java.util.ArrayList;
import java.util.List;

public class TopCommand extends AbstractPluginCommand {

    public TopCommand() {
        super(new ArrayList<>(List.of("top")), new ArrayList<>(List.of("ss.top")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("TopHUsage")));
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        String skillName = args[0];
        Skill skill = PersistentData.getInstance().getSkill(skillName);
        if (skill == null) {
            commandSender.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("SkillNotFound")));
            return false;
        }
        PlayerRecord topPlayerRecord = PersistentData.getInstance().getTopPlayerRecord(skill.getID());
        if (topPlayerRecord == null) {
            commandSender.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("NoTop").replaceAll("%skill%", skill.getName())));

            return false;
        }
        UUIDChecker uuidChecker = new UUIDChecker();
        String playerName = uuidChecker.findPlayerNameBasedOnUUID(topPlayerRecord.getPlayerUUID());
        commandSender.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("SkillNotFound").replaceAll("%skill%", skill.getName()).replaceAll("%player%", playerName).replaceAll("%top%", String.valueOf(topPlayerRecord.getSkillLevel(skill.getID())))));

        return true;
    }
}