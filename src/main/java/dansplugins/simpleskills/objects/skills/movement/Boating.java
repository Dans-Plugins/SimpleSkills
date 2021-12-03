package dansplugins.simpleskills.objects.skills.movement;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.Skill;

/**
 * @author Daniel Stephenson
 */
public class Boating extends Skill {
    public Boating() {
        super(SupportedSkill.BOATING.ordinal(), "Boating");
    }
}