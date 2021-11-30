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
    private HashMap<Integer, Integer> skillLevels = new HashMap<>();

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
        boolean success = knownSkills.add(skill.getID());
        setSkillLevel(skill.getID(), 0);
        return success;
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

    public int getSkillLevel(int ID) {
        return skillLevels.get(ID);
    }

    public void setSkillLevel(int ID, int value) {
        if (skillLevels.containsKey(ID)) {
            skillLevels.replace(ID, value);
        }
        else {
            skillLevels.put(ID, value);
        }
    }

    public void incrementSkillLevel(int ID) {
        setSkillLevel(ID, getSkillLevel(ID) + 1);
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
        commandSender.sendMessage(ChatColor.AQUA + "=== Skills of " + SimpleSkills.getInstance().getToolbox().getUUIDChecker().findPlayerNameBasedOnUUID(playerUUID) + " === ");
        for (int skillID : knownSkills) {
            Skill skill = PersistentData.getInstance().getSkill(skillID);
            commandSender.sendMessage(ChatColor.AQUA + skill.getName() + ": " + getSkillLevel(skill.getID()));
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