package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.Skill;
import dansplugins.simpleskills.objects.benefits.DamageNegation;
import dansplugins.simpleskills.objects.benefits.DamageReduction;

/**
 * @author Daniel Stephenson
 */
public class Hardiness extends Skill {
    public Hardiness() {
        super(SupportedSkill.HARDINESS.ordinal(), "Hardiness");
        initialize();
    }

    private void initialize() {
        addBenefit(new DamageNegation());
        addBenefit(new DamageReduction());
    }
}
