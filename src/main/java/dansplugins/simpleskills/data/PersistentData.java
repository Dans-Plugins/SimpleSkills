package dansplugins.simpleskills.data;

import dansplugins.simpleskills.managers.SkillManager;
import dansplugins.simpleskills.objects.Skill;

import java.util.HashSet;

public class PersistentData {
    private static PersistentData instance;
    private HashSet<Skill> skills = new HashSet<>();

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
}