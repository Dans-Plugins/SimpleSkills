package dansplugins.simpleskills.objects;

import preponderous.ponder.modifiers.Cacheable;
import preponderous.ponder.modifiers.Savable;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class SkillRecord implements Savable, Cacheable {
    private UUID playerUUID;
    private HashSet<Integer> knownSkills = new HashSet<>();

    public SkillRecord(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public Object getKey() {
        return playerUUID;
    }

    public HashSet<Integer> getKnownSkills() {
        return knownSkills;
    }

    public void setKnownSkills(HashSet<Integer> knownSkills) {
        this.knownSkills = knownSkills;
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