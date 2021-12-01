package dansplugins.simpleskills.objects.skills.abs;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author Daniel Stephenson
 */
public abstract class Skill {
    private int ID;
    private String name;
    private int maxLevel;
    private boolean active;
    private int baseExperienceRequirement;
    private double experienceIncreaseFactor;

    public Skill(int ID, String name, int maxLevel, int baseExperienceRequirement, int experienceIncreaseFactor) {
        this.ID = ID;
        this.name = name;
        this.maxLevel = maxLevel;
        this.active = true;
        this.baseExperienceRequirement = baseExperienceRequirement;
        this.experienceIncreaseFactor = experienceIncreaseFactor;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getBaseExperienceRequirement() {
        return baseExperienceRequirement;
    }

    public void setBaseExperienceRequirement(int baseExperienceRequirement) {
        this.baseExperienceRequirement = baseExperienceRequirement;
    }

    public double getExperienceIncreaseFactor() {
        return experienceIncreaseFactor;
    }

    public void setExperienceIncreaseFactor(double experienceIncreaseFactor) {
        this.experienceIncreaseFactor = experienceIncreaseFactor;
    }

    // ---

    public void sendInfo(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "=== " + getName() + " ===");
        commandSender.sendMessage(ChatColor.AQUA + "Max Level: " + getMaxLevel());
        commandSender.sendMessage(ChatColor.AQUA + "Active: " + isActive());
        commandSender.sendMessage(ChatColor.AQUA + "Base Experience Requirement: " + getBaseExperienceRequirement());
        commandSender.sendMessage(ChatColor.AQUA + "Experience Increase Factor: " + getExperienceIncreaseFactor());
    }
}