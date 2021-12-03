package dansplugins.simpleskills.objects.benefits;

import dansplugins.simpleskills.enums.SupportedBenefit;
import dansplugins.simpleskills.objects.abs.Benefit;

public class DoubleDrop extends Benefit {
    public DoubleDrop() {
        super(SupportedBenefit.DOUBLE_DROP.ordinal(), "Double Drop");
    }
}