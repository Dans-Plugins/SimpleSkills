package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.Skill;

/**
 * @author Daniel Stephenson
 */
public class Hardiness extends Skill {
    public Hardiness() {
        super(SupportedSkill.HARDINESS.ordinal(), "Hardiness", 100, 10, 2);
    }
}
