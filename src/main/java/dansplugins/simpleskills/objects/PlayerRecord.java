package dansplugins.simpleskills.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.abs.Skill;
import dansplugins.simpleskills.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.modifiers.Cacheable;
import preponderous.ponder.modifiers.Savable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Daniel Stephenson
 */
public class PlayerRecord implements Savable, Cacheable {
    private UUID playerUUID;
    private HashMap<Integer, Integer> skillLevels = new HashMap<>();
    private HashMap<Integer, Integer> experience = new HashMap<>();

    public PlayerRecord(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public PlayerRecord(Map<String, String> data) {
        this.load(data);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Set<Integer> getKnownSkills() {
        return skillLevels.keySet();
    }

    public boolean isKnown(Skill skill) {
        return getKnownSkills().contains(skill.getID());
    }

    public HashMap<Integer, Integer> getSkillLevels() {
        return skillLevels;
    }

    public void setSkillLevels(HashMap<Integer, Integer> skillLevels) {
        this.skillLevels = skillLevels;
    }

    public int getSkillLevel(int ID) {
        if (!skillLevels.containsKey(ID)) {
            learnSkill(ID);
            return 0;
        }
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

    public HashMap<Integer, Integer> getExperience() {
        return experience;
    }

    public void setExperience(HashMap<Integer, Integer> experience) {
        this.experience = experience;
    }

    public int getExperience(int ID) {
        if (!experience.containsKey(ID)) {
            experience.put(ID, 0);
            return 0;
        }
        return experience.get(ID);
    }

    public void setExperience(int ID, int value) {
        if (experience.containsKey(ID)) {
            experience.replace(ID, value);
        }
        else {
            experience.put(ID, value);
        }
    }

    public void incrementExperience(int ID) {
        setExperience(ID, getExperience(ID) + 1);
        checkForLevelUp(ID);
    }

    // ---

    @Override
    public Object getKey() {
        return getPlayerUUID();
    }

    public void sendInfo(CommandSender commandSender) {
        if (getKnownSkills().size() == 0) {
            commandSender.sendMessage(ChatColor.RED + "No skills known.");
            return;
        }
        commandSender.sendMessage(ChatColor.AQUA + "=== Skills of " + SimpleSkills.getInstance().getToolbox().getUUIDChecker().findPlayerNameBasedOnUUID(playerUUID) + " === ");
        for (int skillID : getKnownSkills()) {
            int currentLevel = getSkillLevel(skillID);
            int currentExperience = getExperience(skillID);
            Skill skill = PersistentData.getInstance().getSkill(skillID);
            int experienceRequired = getExperienceRequired(getSkillLevel(skillID), skill.getBaseExperienceRequirement(), skill.getExperienceIncreaseFactor());
            commandSender.sendMessage(ChatColor.AQUA + skill.getName() + " - LVL: " + currentLevel + " - EXP: " + currentExperience + "/" + experienceRequired);
        }
    }

    public void checkForLevelUp(int ID) {
        int level = getSkillLevel(ID);
        int experience = getExperience(ID);
        Skill skill = PersistentData.getInstance().getSkill(ID);
        int experienceRequiredForLevelUp = getExperienceRequired(level, skill.getBaseExperienceRequirement(), skill.getExperienceIncreaseFactor());
        if (experience >= experienceRequiredForLevelUp) {
            levelUp(ID, experienceRequiredForLevelUp);
        }
    }

    public boolean learnSkill(int skillID) {
        Skill skill = PersistentData.getInstance().getSkill(skillID);
        if (skill != null) {
            learnSkill(skill);
            return true;
        }
        return false;
    }

    public void learnSkill(Skill skill) {
        setSkillLevel(skill.getID(), 0);
        setExperience(skill.getID(), 1);

        // attempt to inform player
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.sendMessage(ChatColor.GREEN + "You've learned the " + skill.getName() + " skill. Type /ss info to view your skills.");
        }
        Logger.getInstance().log(SimpleSkills.getInstance().getToolbox().getUUIDChecker().findPlayerNameBasedOnUUID(playerUUID) + " learned the " + skill.getName() + " skill.");
    }

    private void levelUp(int ID, int experienceRequiredForLevelUp) {
        // level up
        incrementSkillLevel(ID);
        setExperience(ID, getExperience(ID) - experienceRequiredForLevelUp);

        // attempt to inform player
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            Skill skill = PersistentData.getInstance().getSkill(ID);
            player.sendMessage(ChatColor.GREEN + "You've leveled up your " + skill.getName() + " skill to " + getSkillLevel(ID));
        }
    }

    @Override()
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("playerUUID", gson.toJson(playerUUID));
        saveMap.put("skillLevels", gson.toJson(skillLevels));
        saveMap.put("experience", gson.toJson(experience));

        return saveMap;
    }

    @Override()
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type integerToIntegerMapType = new TypeToken<HashMap<Integer, Integer>>(){}.getType();

        playerUUID = UUID.fromString(gson.fromJson(data.get("playerUUID"), String.class));
        skillLevels = gson.fromJson(data.get("skillLevels"), integerToIntegerMapType);
        experience = gson.fromJson(data.get("experience"), integerToIntegerMapType);
    }

    private int getExperienceRequired(int currentLevel, int baseExperienceRequirement, double experienceIncreaseFactor) {
        return (int) (baseExperienceRequirement * Math.pow(experienceIncreaseFactor, currentLevel));
    }
}