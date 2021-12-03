package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.SupportedSkill;
import dansplugins.simpleskills.objects.skills.abs.Skill;

/**
 * @author Daniel Stephenson
 */
public class Fishing extends Skill {
    public Fishing() {
        super(SupportedSkill.FISHING.ordinal(), "Fishing", 100, 10, 2);
    }
}