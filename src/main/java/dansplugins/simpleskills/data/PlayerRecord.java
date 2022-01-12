package dansplugins.simpleskills.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansplugins.simpleskills.AbstractSkill;
import dansplugins.simpleskills.services.LocalConfigService;
import dansplugins.simpleskills.services.LocalMessageService;
import dansplugins.simpleskills.utils.ExperienceCalculator;
import dansplugins.simpleskills.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.spigot.tools.UUIDChecker;
import preponderous.ponder.misc.abs.Cacheable;
import preponderous.ponder.misc.abs.Savable;

import java.lang.reflect.Type;
import java.util.*;

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

    public boolean isKnown(AbstractSkill skill) {
        return getKnownSkills().contains(skill.getId());
    }

    public HashMap<Integer, Integer> getSkillLevels() {
        return skillLevels;
    }

    public void setSkillLevels(HashMap<Integer, Integer> skillLevels) {
        this.skillLevels = skillLevels;
    }

    public int getSkillLevel(int ID, boolean learnSkillIfUnknown) {
        if (!skillLevels.containsKey(ID) ) {
            if (learnSkillIfUnknown) {
                learnSkill(ID);
                return 0;
            } else return -1;
        }
        return skillLevels.get(ID);
    }

    public int getOverallSkillLevel() {
        return skillLevels.values().stream().mapToInt(value -> value).sum();
    }

    public void setSkillLevel(int ID, int value) {
        if (skillLevels.containsKey(ID)) {
            skillLevels.replace(ID, value);
        } else {
            skillLevels.put(ID, value);
        }
    }

    public void incrementSkillLevel(int ID) {
        setSkillLevel(ID, getSkillLevel(ID, true) + 1);
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
        } else {
            experience.put(ID, value);
        }
    }

    public void incrementExperience(int ID) {
        setExperience(ID, getExperience(ID) + 1);
        AbstractSkill skill = PersistentData.getInstance().getSkill(ID);
        if (skill == null || (getSkillLevel(ID, true) >= LocalConfigService.getInstance()
                .getconfig().getInt("defaultMaxLevel"))) { // TODO fix max level
            return;
        }
        checkForLevelUp(ID);
    }

    // ---

    @Override
    public Object getKey() {
        return getPlayerUUID();
    }

    public void sendInfo(CommandSender commandSender) {
        if (getKnownSkills().size() == 0) {
            commandSender.sendMessage(LocalMessageService.getInstance()
                    .convert(Objects.requireNonNull(LocalMessageService.getInstance().getlang()
                            .getString("SkillNotFound"))));
            return;
        }
        for (String s : LocalMessageService.getInstance().getlang().getStringList("SendInfo-Header")) {
            commandSender.sendMessage(LocalMessageService.getInstance().convert(s)
                    .replaceAll("%player%", new UUIDChecker()
                            .findPlayerNameBasedOnUUID(playerUUID)));
        }
        for (int skillID : getKnownSkills()) {
            int currentLevel = getSkillLevel(skillID, true);
            int currentExperience = getExperience(skillID);
            AbstractSkill skill = PersistentData.getInstance().getSkill(skillID);
            if (skill == null) {
                continue;
            }
            int experienceRequired = ExperienceCalculator.getInstance()
                    .getExperienceRequiredForLevelUp(
                            getSkillLevel(skillID, true), skill.getExpRequirement(), skill.getExpFactor()
                    );

            for (String sendInfo : LocalMessageService.getInstance().getlang().getStringList("SendInfo-Body"))
                commandSender.sendMessage(LocalMessageService.getInstance()
                        .convert(sendInfo)
                        .replaceAll("%player%", new UUIDChecker().findPlayerNameBasedOnUUID(playerUUID))
                        .replaceAll("%skill%", skill.getName())
                        .replaceAll("%level%", String.valueOf(currentLevel))
                        .replaceAll("%min%", String.valueOf(currentExperience))
                        .replaceAll("%max%", String.valueOf(experienceRequired)));

        }
    }

    public void checkForLevelUp(int ID) {
        int level = getSkillLevel(ID, true);
        int experience = getExperience(ID);
        AbstractSkill skill = PersistentData.getInstance().getSkill(ID);
        int experienceRequiredForLevelUp = ExperienceCalculator.getInstance()
                .getExperienceRequiredForLevelUp(level, skill.getExpRequirement(), skill.getExpFactor());
        if (experience >= experienceRequiredForLevelUp) {
            levelUp(ID, experienceRequiredForLevelUp);
        }
    }

    public boolean learnSkill(int skillID) {
        AbstractSkill skill = PersistentData.getInstance().getSkill(skillID);
        if (skill != null) {
            learnSkill(skill);
            return true;
        }
        return false;
    }

    public void learnSkill(AbstractSkill skill) {
        setSkillLevel(skill.getId(), 0);
        setExperience(skill.getId(), 1);

        // attempt to inform player
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("LearnedSkill").replaceAll("%skill%", skill.getName())));
        }
        Logger.getInstance().log(new UUIDChecker().findPlayerNameBasedOnUUID(playerUUID) + " learned the " + skill.getName() + " skill.");
    }

    private void levelUp(int ID, int experienceRequiredForLevelUp) {
        // level up
        incrementSkillLevel(ID);
        setExperience(ID, getExperience(ID) - experienceRequiredForLevelUp);

        // attempt to inform player
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            AbstractSkill skill = PersistentData.getInstance().getSkill(ID);
            if (LocalConfigService.getInstance().getconfig().getBoolean("levelUpAlert")) {
                player.sendMessage(LocalMessageService.getInstance()
                        .convert(LocalMessageService.getInstance().getlang().getString("LevelUp")
                                .replaceAll("%skill%", skill.getName())
                                .replaceAll("%level%", String.valueOf(getSkillLevel(ID, true)))));

            }

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

        Type integerToIntegerMapType = new TypeToken<HashMap<Integer, Integer>>() {
        }.getType();

        playerUUID = UUID.fromString(gson.fromJson(data.get("playerUUID"), String.class));
        skillLevels = gson.fromJson(data.get("skillLevels"), integerToIntegerMapType);
        experience = gson.fromJson(data.get("experience"), integerToIntegerMapType);
    }

}