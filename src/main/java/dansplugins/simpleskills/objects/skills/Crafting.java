package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.SupportedSkill;
import dansplugins.simpleskills.objects.skills.abs.Skill;

/**
 * @author Daniel Stephenson
 */
public class Crafting extends Skill {
    public Crafting() {
        super(SupportedSkill.CRAFTING.ordinal(), "Crafting", 100, 10, 2);
    }
}