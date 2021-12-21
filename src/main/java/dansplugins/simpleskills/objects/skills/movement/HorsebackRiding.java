package dansplugins.simpleskills.objects.skills.movement;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.Skill;

/**
 * @author Daniel Stephenson
 */
public class HorsebackRiding extends Skill {
    public HorsebackRiding() {
        super(SupportedSkill.HORSEBACK_RIDING.ordinal(), "Horseback_Riding");
    }
}