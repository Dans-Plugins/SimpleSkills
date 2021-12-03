package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.SupportedSkill;
import dansplugins.simpleskills.objects.skills.abs.Skill;

/**
 * @author Daniel Stephenson
 */
public class Dueling extends Skill {
    public Dueling() {
        super(SupportedSkill.DUELING.ordinal(), "Dueling", 100, 10, 2);
    }
}