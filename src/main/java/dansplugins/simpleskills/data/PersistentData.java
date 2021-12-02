package dansplugins.simpleskills.data;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.SupportedSkill;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.objects.skills.*;
import dansplugins.simpleskills.objects.skills.abs.Skill;
import dansplugins.simpleskills.utils.Logger;

import java.util.HashSet;
import java.util.UUID;

/**
 * @author Daniel Stephenson
 */
public class PersistentData {
    private static PersistentData instance;
    private final HashSet<Skill> skills = new HashSet<>();
    private HashSet<PlayerRecord> playerRecords = new HashSet<>();

    private PersistentData() {
        initializeSkills();
    }

    public static PersistentData getInstance() {
        if (instance == null) {
            instance = new PersistentData();
        }
        return instance;
    }

    public HashSet<Skill> getSkills() {
        return skills;
    }

    public Skill getSkill(int ID) {
        for (Skill skill : skills) {
            if (skill.getID() == ID) {
                return skill;
            }
        }
        return null;
    }

    public Skill getSkill(String skillName) {
        for (Skill skill : skills) {
            if (skill.getName().equalsIgnoreCase(skillName)) {
                return skill;
            }
        }
        return null;
    }

    public boolean addSkill(Skill skill) {
        Logger.getInstance().log("Added skill: " + skill.getName());
        return skills.add(skill);
    }

    public HashSet<PlayerRecord> getPlayerRecords() {
        return playerRecords;
    }

    public void setPlayerRecords(HashSet<PlayerRecord> playerRecords) {
        this.playerRecords = playerRecords;
    }

    public boolean addPlayerRecord(PlayerRecord playerRecord) {
        return playerRecords.add(playerRecord);
    }

    public PlayerRecord getPlayerRecord(UUID playerUUID) {
        for (PlayerRecord record : playerRecords) {
            if (record.getPlayerUUID().equals(playerUUID)) {
                return record;
            }
        }
        Logger.getInstance().log("A player record wasn't found for " + SimpleSkills.getInstance().getToolbox().getUUIDChecker().findPlayerNameBasedOnUUID(playerUUID) + " wasn't found. One is being created for them.");
        PlayerRecord record = new PlayerRecord(playerUUID);
        addPlayerRecord(record);
        return record;
    }

    private void initializeSkills() {
        addSkill(new Woodcutting(SupportedSkill.WOODCUTTING.ordinal()));
        addSkill(new Quarrying(SupportedSkill.QUARRYING.ordinal()));
        addSkill(new Mining(SupportedSkill.MINING.ordinal()));
        addSkill(new Digging(SupportedSkill.DIGGING.ordinal()));
        addSkill(new Farming(SupportedSkill.FARMING.ordinal()));
        addSkill(new Foraging(SupportedSkill.FORAGING.ordinal()));
        addSkill(new Floriculture(SupportedSkill.FLORICULTURE.ordinal()));
        addSkill(new Crafting(SupportedSkill.CRAFTING.ordinal()));
        addSkill(new Fishing(SupportedSkill.FISHING.ordinal()));
        addSkill(new Hardiness(SupportedSkill.HARDINESS.ordinal()));
        addSkill(new Enchanting(SupportedSkill.ENCHANTING.ordinal()));
    }
}