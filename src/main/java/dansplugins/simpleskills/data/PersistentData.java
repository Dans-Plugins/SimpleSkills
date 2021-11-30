package dansplugins.simpleskills.data;

import dansplugins.simpleskills.managers.SkillManager;
import dansplugins.simpleskills.objects.Skill;
import dansplugins.simpleskills.objects.PlayerRecord;

import java.util.HashSet;
import java.util.UUID;

public class PersistentData {
    private static PersistentData instance;
    private HashSet<Skill> skills = new HashSet<>();
    private HashSet<PlayerRecord> playerRecords = new HashSet<>();

    private PersistentData() {
        SkillManager.getInstance().initializeSkills();
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

    public boolean addSkill(Skill skill) {
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
}