package dansplugins.simpleskills.managers;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.Skill;
import dansplugins.simpleskills.objects.SkillRecord;

public class SkillManager {
    private static SkillManager instance;

    private SkillManager() {

    }

    public static SkillManager getInstance() {
        if (instance == null) {
            instance = new SkillManager();
        }
        return instance;
    }

    public void initializeSkills() {
        // mining
        PersistentData.getInstance().addSkill(new Skill(0, "Mining"));

        // crafting
        PersistentData.getInstance().addSkill(new Skill(1, "Crafting"));

        // woodcutting
        PersistentData.getInstance().addSkill(new Skill(2, "Woodcutting"));

        // farming
        PersistentData.getInstance().addSkill(new Skill(3, "Farming"));
    }
}