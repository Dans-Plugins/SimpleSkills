package dansplugins.simpleskills.objects;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.skills.Skill;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.modifiers.Cacheable;
import preponderous.ponder.modifiers.Savable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class PlayerRecord implements Savable, Cacheable {
    private UUID playerUUID;
    private HashSet<Integer> knownSkills = new HashSet<>();
    private HashMap<Integer, Integer> skillLevels = new HashMap<>(); // TODO: implement methods for this field

    public PlayerRecord(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public HashSet<Integer> getKnownSkills() {
        return knownSkills;
    }

    public void setKnownSkills(HashSet<Integer> knownSkills) {
        this.knownSkills = knownSkills;
    }

    public boolean addKnownSkill(Skill skill) {
        return knownSkills.add(skill.getID());
    }

    public boolean isKnown(Skill skill) {
        return knownSkills.contains(skill.getID());
    }

    public HashMap<Integer, Integer> getSkillLevels() {
        return skillLevels;
    }

    public void setSkillLevels(HashMap<Integer, Integer> skillLevels) {
        this.skillLevels = skillLevels;
    }

    public void getSkillLevel(int ID) {
        // TODO: implement
    }

    // ---

    @Override
    public Object getKey() {
        return getPlayerUUID();
    }

    public void sendInfo(CommandSender commandSender) {
        if (knownSkills.size() == 0) {
            commandSender.sendMessage(ChatColor.RED + "No skills known.");
            return;
        }
        commandSender.sendMessage(ChatColor.AQUA + "=== Skills of" + SimpleSkills.getInstance().getToolbox().getUUIDChecker().findPlayerNameBasedOnUUID(playerUUID) + " === ");
        for (int skillID : knownSkills) {
            Skill skill = PersistentData.getInstance().getSkill(skillID);
            commandSender.sendMessage(ChatColor.AQUA + skill.getName());
        }
    }

    @Override
    public Map<String, String> save() {
        // TODO: implement
        return null;
    }

    @Override
    public void load(Map<String, String> map) {
        // TODO: implement
    }
}