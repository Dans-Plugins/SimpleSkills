package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.Skill;

public class Strength extends Skill {
    public Strength() {
        super(SupportedSkill.STRENGTH.ordinal(), "Strength");
    }
}