package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.objects.abs.Skill;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class TopCommand extends AbstractCommand {
    private final ArrayList<String> names = new ArrayList<>(Collections.singletonList("top"));
    private final ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("ss.top"));

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
        String playerName = SimpleSkills.getInstance().getToolbox().getUUIDChecker().findPlayerNameBasedOnUUID(topPlayerRecord.getPlayerUUID());
        commandSender.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("SkillNotFound").replaceAll("%skill%", skill.getName()).replaceAll("%player%", playerName).replaceAll("%top%", String.valueOf(topPlayerRecord.getSkillLevel(skill.getID())))));

        return true;
    }
}