package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.Skill;

/**
 * @author Daniel Stephenson
 */
public class Crafting extends Skill {
    public Crafting() {
        super(SupportedSkill.CRAFTING.ordinal(), "Crafting");
    }
}