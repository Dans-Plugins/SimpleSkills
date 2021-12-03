package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.Skill;

/**
 * @author Daniel Stephenson
 */
public class Fishing extends Skill {
    public Fishing() {
        super(SupportedSkill.FISHING.ordinal(), "Fishing");
    }
}