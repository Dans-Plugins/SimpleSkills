package dansplugins.simpleskills.objects.skills;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.objects.abs.Skill;

public class MonsterHunting extends Skill {
    public MonsterHunting() {
        super(SupportedSkill.MONSTER_HUNTING.ordinal(), "Monster Hunting");
    }
}