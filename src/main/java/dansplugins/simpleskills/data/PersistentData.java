package dansplugins.simpleskills.data;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.objects.abs.Skill;
import dansplugins.simpleskills.objects.skills.movement.Boating;
import dansplugins.simpleskills.objects.skills.movement.Cardio;
import dansplugins.simpleskills.objects.skills.*;
import dansplugins.simpleskills.objects.skills.blockbreaking.*;
import dansplugins.simpleskills.objects.skills.movement.HorsebackRiding;
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

    // ---

    public int getNumUnknownSkills() {
        return skills.size() - getNumKnownSkills();
    }

    public int getNumKnownSkills() {
        int knownSkills = 0;
        for (Skill skill : skills) {
            for (PlayerRecord record : playerRecords) {
                if (record.isKnown(skill)) {
                    knownSkills++;
                    break;
                }
            }
        }
        return knownSkills;
    }

    private void initializeSkills() {
        addSkill(new Woodcutting());
        addSkill(new Quarrying());
        addSkill(new Mining());
        addSkill(new Digging());
        addSkill(new Harvesting());
        addSkill(new Foraging());
        addSkill(new Floriculture());
        addSkill(new Crafting());
        addSkill(new Fishing());
        addSkill(new Hardiness());
        addSkill(new Enchanting());
        addSkill(new Dueling());
        addSkill(new Cardio());
        addSkill(new Boating());
        addSkill(new HorsebackRiding());
    }
}