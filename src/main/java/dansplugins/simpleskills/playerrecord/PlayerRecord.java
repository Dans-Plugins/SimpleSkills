package dansplugins.simpleskills.playerrecord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.SkillRepository;
import dansplugins.simpleskills.skill.abs.AbstractSkill;
import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.services.StorageService;
import dansplugins.simpleskills.utils.Cacheable;
import dansplugins.simpleskills.utils.Savable;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Daniel Stephenson
 */
public class PlayerRecord implements Savable, Cacheable {
    private final SkillRepository skillRepository;
    private final MessageService messageService;
    private final ConfigService configService;
    private final ExperienceCalculator experienceCalculator;
    private final Log log;
    private final StorageService storageService;

    private UUID playerUUID;
    private HashMap<Integer, Integer> skillLevels = new HashMap<>();
    private HashMap<Integer, Integer> experience = new HashMap<>();
    
    // Throttling mechanism to prevent excessive saves
    private long lastSaveTime = 0;
    private static final long SAVE_COOLDOWN_MS = 5000; // Save at most once every 5 seconds per player

    public PlayerRecord(SkillRepository skillRepository, MessageService messageService, ConfigService configService, ExperienceCalculator experienceCalculator, Log log, StorageService storageService, UUID playerUUID) {
        this.skillRepository = skillRepository;
        this.messageService = messageService;
        this.configService = configService;
        this.experienceCalculator = experienceCalculator;
        this.log = log;
        this.storageService = storageService;
        this.playerUUID = playerUUID;
    }

    public PlayerRecord(Map<String, String> data, SkillRepository skillRepository, MessageService messageService, ConfigService configService, ExperienceCalculator experienceCalculator, Log log, StorageService storageService) {
        this.skillRepository = skillRepository;
        this.messageService = messageService;
        this.configService = configService;
        this.experienceCalculator = experienceCalculator;
        this.log = log;
        this.storageService = storageService;
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
        saveDataIfNeeded();
    }

    public int getSkillLevel(int ID, boolean learnSkillIfUnknown) {
        if (!skillLevels.containsKey(ID)) {
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
        saveDataIfNeeded();
    }

    public void incrementSkillLevel(int ID) {
        setSkillLevel(ID, getSkillLevel(ID, true) + 1);
    }

    public HashMap<Integer, Integer> getExperience() {
        return experience;
    }

    public void setExperience(HashMap<Integer, Integer> experience) {
        this.experience = experience;
        saveDataIfNeeded();
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
        saveDataIfNeeded();
    }

    public void incrementExperience(int ID) {
        setExperience(ID, getExperience(ID) + 1);
        AbstractSkill skill = skillRepository.getSkill(ID);
        if (skill == null || (getSkillLevel(ID, true) >= configService
                .getConfig().getInt("defaultMaxLevel"))) { // TODO fix max level
            return;
        }
        checkForLevelUp(ID);
    }

    /**
     * Saves player data immediately if enough time has passed since the last save
     * to prevent data loss during server crashes while avoiding excessive I/O
     */
    private void saveDataIfNeeded() {
        if (storageService == null) {
            return; // No storage service available (should not happen in normal operation)
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSaveTime >= SAVE_COOLDOWN_MS) {
            lastSaveTime = currentTime;
            // Run save asynchronously to avoid blocking the main thread
            Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("SimpleSkills"), () -> {
                try {
                    storageService.save();
                    log.debug("Auto-saved player data for " + new UUIDChecker().findPlayerNameBasedOnUUID(playerUUID));
                } catch (Exception e) {
                    log.error("Failed to auto-save player data: " + e.getMessage());
                }
            });
        }
    }

    // ---

    @Override
    public Object getKey() {
        return getPlayerUUID();
    }

    public void sendInfo(CommandSender commandSender) {
        if (getKnownSkills().size() == 0) {
            commandSender.sendMessage(messageService
                    .convert(Objects.requireNonNull(messageService.getlang()
                            .getString("SkillNotFound"))));
            return;
        }
        for (String s : messageService.getlang().getStringList("SendInfo-Header")) {
            commandSender.sendMessage(messageService.convert(s)
                    .replaceAll("%player%", new UUIDChecker()
                            .findPlayerNameBasedOnUUID(playerUUID)));
        }
        for (int skillID : getKnownSkills()) {
            int currentLevel = getSkillLevel(skillID, true);
            int currentExperience = getExperience(skillID);
            AbstractSkill skill = skillRepository.getSkill(skillID);
            if (skill == null) {
                continue;
            }
            int experienceRequired = experienceCalculator
                    .getExperienceRequiredForLevelUp(
                            getSkillLevel(skillID, true), skill.getExpRequirement(), skill.getExpFactor()
                    );

            for (String sendInfo : messageService.getlang().getStringList("SendInfo-Body"))
                commandSender.sendMessage(messageService
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
        AbstractSkill skill = skillRepository.getSkill(ID);
        int experienceRequiredForLevelUp = experienceCalculator
                .getExperienceRequiredForLevelUp(level, skill.getExpRequirement(), skill.getExpFactor());
        if (experience >= experienceRequiredForLevelUp) {
            levelUp(ID, experienceRequiredForLevelUp);
        }
    }

    public boolean learnSkill(int skillID) {
        AbstractSkill skill = skillRepository.getSkill(skillID);
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
            player.sendMessage(messageService.convert(messageService.getlang().getString("LearnedSkill").replaceAll("%skill%", skill.getName())));
        }
        log.info(new UUIDChecker().findPlayerNameBasedOnUUID(playerUUID) + " learned the " + skill.getName() + " skill.");
        
        // Data was already saved by setSkillLevel and setExperience calls above
    }

    private void levelUp(int ID, int experienceRequiredForLevelUp) {
        // level up
        incrementSkillLevel(ID);
        setExperience(ID, getExperience(ID) - experienceRequiredForLevelUp);

        // attempt to inform player
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            AbstractSkill skill = skillRepository.getSkill(ID);
            if (configService.getConfig().getBoolean("levelUpAlert")) {
                player.sendMessage(messageService
                        .convert(messageService.getlang().getString("LevelUp")
                                .replaceAll("%skill%", skill.getName())
                                .replaceAll("%level%", String.valueOf(getSkillLevel(ID, true)))));

            }

        }
        
        // Data was already saved by incrementSkillLevel and setExperience calls above
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