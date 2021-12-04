package dansplugins.simpleskills.objects.benefits;

import dansplugins.simpleskills.enums.SupportedBenefit;
import dansplugins.simpleskills.objects.abs.Benefit;

/**
 * @author Daniel Stephenson
 */
public class DamageNegation extends Benefit {
    public DamageNegation() {
        super(SupportedBenefit.DAMAGE_NEGATION.ordinal(), "Damage Negation");
    }
}