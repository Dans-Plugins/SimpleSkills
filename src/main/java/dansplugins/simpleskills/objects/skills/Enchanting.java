package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.SupportedSkill;
import dansplugins.simpleskills.objects.skills.abs.Skill;

/**
 * @author Daniel Stephenson
 */
public class Enchanting extends Skill {
    public Enchanting() {
        super(SupportedSkill.ENCHANTING.ordinal(), "Enchanting", 100, 10, 2);
    }
}