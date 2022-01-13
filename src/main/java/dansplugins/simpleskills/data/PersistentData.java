package dansplugins.simpleskills.data;

import dansplugins.simpleskills.AbstractSkill;
import dansplugins.simpleskills.skills.*;
import dansplugins.simpleskills.utils.Logger;
import preponderous.ponder.minecraft.spigot.tools.UUIDChecker;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Daniel Stephenson
 */
public class PersistentData {
    private static PersistentData instance;
    private final HashSet<AbstractSkill> skills = new HashSet<>();
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

    public HashSet<AbstractSkill> getSkills() {
        return skills;
    }

    public AbstractSkill getSkill(int Id) {
        for (AbstractSkill skill : skills) {
            if (skill.getId() == Id) {
                return skill;
            }
        }
        return null;
    }

    public AbstractSkill getSkill(String skillName) {
        for (AbstractSkill skill : skills) {
            if (skill.getName().equalsIgnoreCase(skillName)) {
                return skill;
            }
        }
        return null;
    }

    public boolean addSkill(AbstractSkill skill) {
        if (!skill.isActive()) {
            return false;
        }
        Logger.getInstance().log("Added skill: " + skill.getName());
        return skills.add(skill);
    }

    public boolean removeSkill(AbstractSkill skill) {
        Logger.getInstance().log("Removed skill: " + skill.getName());
        return skills.remove(skill);
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
        Logger.getInstance().log("A player record wasn't found for " + new UUIDChecker().findPlayerNameBasedOnUUID(playerUUID) + " wasn't found. One is being created for them.");
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
        for (AbstractSkill skill : skills) {
            for (PlayerRecord record : playerRecords) {
                if (record.isKnown(skill)) {
                    knownSkills++;
                    break;
                }
            }
        }
        return knownSkills;
    }

    @Deprecated
    public int getNumUselessSkills() {
        return 0;
    }

    @Deprecated
    public int getNumUsefulSkills() {
        return 0;
    }

    public List<PlayerRecord> getTopPlayers() {
        final Comparator<PlayerRecord> recordComparator = (o1, o2) ->
                Integer.compare(o2.getOverallSkillLevel(), o1.getOverallSkillLevel());
        final List<PlayerRecord> playerRecords = new ArrayList<>(this.playerRecords);
        playerRecords.sort(recordComparator);
        return playerRecords.subList(0, Math.min(playerRecords.size(), 9));
    }

    public List<PlayerRecord> getTopPlayerRecords(int skillID) {
        final Comparator<PlayerRecord> recordComparator = (o1, o2) ->
                Integer.compare(o2.getSkillLevel(skillID, false), o1.getSkillLevel(skillID, false));
        return new ArrayList<>(this.playerRecords)
                .stream().sorted(recordComparator)
                .filter(record -> record.getSkillLevel(skillID, false) != -1)
                .limit(11)
                .collect(Collectors.toList());
    }

    private void initializeSkills() {
        addSkill(new Athlete());
        addSkill(new Boating());
        addSkill(new Breeding());
        addSkill(new Cardio());
        addSkill(new Crafting());
        addSkill(new Digging());
        addSkill(new Dueling());
        addSkill(new Enchanting());
        addSkill(new Farming());
        addSkill(new Fishing());
        addSkill(new Floriculture());
        addSkill(new Gliding());
        addSkill(new Hardiness());
        addSkill(new LumberJack());
        addSkill(new Mining());
        addSkill(new MonsterHunting());
        addSkill(new Pyromaniac());
        addSkill(new Quarrying());
        addSkill(new Riding());
        addSkill(new Strength());
    }

}