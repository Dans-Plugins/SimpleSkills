package dansplugins.simpleskills.objects.abs;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.PlayerRecord;

import java.util.Random;
import java.util.UUID;

/**
 * @author Daniel Stephenson
 */
public abstract class Benefit {
    private int ID;
    private String name;
    private boolean active;

    public Benefit(int ID, String name) {
        this.ID = ID;
        this.name = name;
        active = true;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // ---

    public static boolean roll(UUID playerUUID, int skillID, double nerfFactor) {
        PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(playerUUID);
        Skill skill = PersistentData.getInstance().getSkill(skillID);
        return roll(playerRecord, skill, nerfFactor);
    }

    public static boolean roll(PlayerRecord playerRecord, Skill skill, double nerfFactor) {
        Random random = new Random();
        double skillLevel = playerRecord.getSkillLevel(skill.getID());
        double maxLevel = skill.getMaxLevel();
        double chance = skillLevel/maxLevel;
        double result = random.nextDouble() * maxLevel;
        double threshold = maxLevel * chance * nerfFactor;
        return (result < threshold);
    }
}