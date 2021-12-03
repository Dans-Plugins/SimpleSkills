package dansplugins.simpleskills.objects.skills.movement;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.Skill;

/**
 * @author Daniel Stephenson
 */
public class Cardio extends Skill {
    public Cardio() {
        super(SupportedSkill.CARDIO.ordinal(), "Cardio");
    }
}
