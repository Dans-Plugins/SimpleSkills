package dansplugins.simpleskills.playerrecord;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Daniel Stephenson
 */
public class PlayerRecordRepository {
    private HashSet<PlayerRecord> playerRecords = new HashSet<>();

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
        return null;
    }

    // ---

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

}