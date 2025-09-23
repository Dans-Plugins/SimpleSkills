package dansplugins.simpleskills.skill;

import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.skill.abs.AbstractSkill;
import dansplugins.simpleskills.skill.skills.*;

import java.util.HashSet;

public class SkillRepository {
    private final HashSet<AbstractSkill> skills = new HashSet<>();

    public HashSet<AbstractSkill> getSkills() {
        return skills;
    }

    /**
     * Get all active skills
     * @return HashSet of active skills
     */
    public HashSet<AbstractSkill> getActiveSkills() {
        HashSet<AbstractSkill> activeSkills = new HashSet<>();
        for (AbstractSkill skill : skills) {
            if (skill.isActive()) {
                activeSkills.add(skill);
            }
        }
        return activeSkills;
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

    /**
     * Get an active skill by name
     * @param skillName name of the skill
     * @return skill if found and active, null otherwise
     */
    public AbstractSkill getActiveSkill(String skillName) {
        for (AbstractSkill skill : skills) {
            if (skill.getName().equalsIgnoreCase(skillName) && skill.isActive()) {
                return skill;
            }
        }
        return null;
    }

    public boolean addSkill(AbstractSkill skill) {
        return skills.add(skill);
    }

    public boolean removeSkill(AbstractSkill skill) {
        return skills.remove(skill);
    }

}
