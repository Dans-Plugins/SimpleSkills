package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.Skill;

/**
 * @author Daniel Stephenson
 */
public class Enchanting extends Skill {
    public Enchanting() {
        super(SupportedSkill.ENCHANTING.ordinal(), "Enchanting");
    }
}