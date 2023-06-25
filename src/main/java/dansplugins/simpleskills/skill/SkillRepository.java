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
        return skills.add(skill);
    }

    public boolean removeSkill(AbstractSkill skill) {
        return skills.remove(skill);
    }

}
