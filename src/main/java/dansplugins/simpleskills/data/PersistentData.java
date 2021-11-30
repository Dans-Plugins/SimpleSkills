package dansplugins.simpleskills.data;

import dansplugins.simpleskills.objects.skills.Skill;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.objects.skills.Woodcutting;
import dansplugins.simpleskills.utils.Logger;

import java.util.HashSet;
import java.util.UUID;

public class PersistentData {
    private static PersistentData instance;
    private HashSet<Skill> skills = new HashSet<>();
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

    public boolean addSkill(Skill skill) {
        Logger.getInstance().log("Added skill: " + skill.getName());
        return skills.add(skill);
    }

    public HashSet<PlayerRecord> getPlayerRecords() {
        return playerRecords;
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
        return null;
    }

    private void initializeSkills() {
        // woodcutting
        addSkill(new Woodcutting());
    }
}