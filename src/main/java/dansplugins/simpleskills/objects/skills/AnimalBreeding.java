package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.Skill;

/**
 * @author Daniel Stephenson
 */
public class AnimalBreeding extends Skill {
    public AnimalBreeding() {
        super(SupportedSkill.ANIMAL_BREEDING.ordinal(), "Animal_Breeding");
    }
}