package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.Skill;

public class Cardio extends Skill {
    public Cardio() {
        super(SupportedSkill.CARDIO.ordinal(), "Cardio");
    }
}
