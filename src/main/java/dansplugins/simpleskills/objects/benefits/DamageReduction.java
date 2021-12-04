package dansplugins.simpleskills.objects.benefits;

import dansplugins.simpleskills.enums.SupportedBenefit;
import dansplugins.simpleskills.objects.abs.Benefit;

/**
 * @author Daniel Stephenson
 */
public class DamageReduction extends Benefit {
    public DamageReduction() {
        super(SupportedBenefit.DAMAGE_REDUCTION.ordinal(), "Damage Reduction");
    }
}