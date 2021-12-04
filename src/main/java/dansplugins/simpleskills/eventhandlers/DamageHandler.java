package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.enums.SupportedBenefit;
import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.eventhandlers.abs.SkillHandler;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.objects.abs.Skill;
import dansplugins.simpleskills.objects.benefits.ResourceExtraction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Daniel Stephenson
 */
public class DamageHandler extends SkillHandler {

    @EventHandler()
    public void handle(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;
        
        PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(player.getUniqueId());

        Skill hardinessSkill = PersistentData.getInstance().getSkill(SupportedSkill.HARDINESS.ordinal());

        // handle damage negation benefit
        if (hardinessSkill.hasBenefit(SupportedBenefit.DAMAGE_NEGATION.ordinal()) && ResourceExtraction.roll(playerRecord, hardinessSkill, 0.01)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.GREEN + "Due to your " + hardinessSkill.getName() + " skill, you manage to avoid taking damage.");
            return;
        }

        // handle damage reduction benefit
        if (hardinessSkill.hasBenefit(SupportedBenefit.DAMAGE_REDUCTION.ordinal()) && ResourceExtraction.roll(playerRecord, hardinessSkill, 0.05)) {
            event.setDamage(event.getDamage() / 2);
            player.sendMessage(ChatColor.GREEN + "Due to your " + hardinessSkill.getName() + " skill, you manage to take reduced damage.");
            return;
        }
        
        incrementExperience(player, SupportedSkill.HARDINESS.ordinal());
    }

    @EventHandler()
    public void handle(EntityDamageByEntityEvent event) {
        Entity damagedEntity = event.getEntity();
        Entity damagingEntity = event.getDamager();
        if (damagingEntity instanceof Player && damagedEntity instanceof Player) {
            if (damagedEntity.isDead()) {
                Player player = (Player) damagingEntity;
                incrementExperience(player, SupportedSkill.DUELING.ordinal());
            }
        }
    }

}