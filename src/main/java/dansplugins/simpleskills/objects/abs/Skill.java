package dansplugins.simpleskills.objects.abs;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 */
public abstract class Skill {
    private int ID;
    private String name;
    private int maxLevel;
    private int baseExperienceRequirement;
    private double experienceIncreaseFactor;
    private boolean active;

    private HashSet<Benefit> benefits = new HashSet<>();

    public Skill(int ID, String name, int maxLevel, int baseExperienceRequirement, double experienceIncreaseFactor) {
        this.ID = ID;
        this.name = name;
        this.maxLevel = maxLevel;
        this.baseExperienceRequirement = baseExperienceRequirement;
        this.experienceIncreaseFactor = experienceIncreaseFactor;
        active = true;
    }

    public Skill(int ID, String name) {
        this(ID, name, getDefaultMaxLevel(), getDefaultBaseExperienceRequirement(), getDefaultExperienceIncreaseFactor());
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

    public HashSet<Benefit> getBenefits() {
        return benefits;
    }

    public void setBenefits(HashSet<Benefit> benefits) {
        this.benefits = benefits;
    }

    public void addBenefit(Benefit benefit) {
        benefits.add(benefit);
    }

    public void removeBenefit(Benefit benefit) {
        benefits.remove(benefit);
    }

    public Benefit getBenefit(int benefitID) {
        for (Benefit benefit : benefits) {
            if (benefit.getID() == benefitID) {
                return benefit;
            }
        }
        return null;
    }

    public boolean hasBenefit(int benefitID) {
        return (getBenefit(benefitID) != null);
    }

    // ---

    public void sendInfo(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "=== " + getName() + " ===");
        commandSender.sendMessage(ChatColor.AQUA + "Active: " + isActive());
        commandSender.sendMessage(ChatColor.AQUA + "Number of benefits: " + getBenefits().size());
        commandSender.sendMessage(ChatColor.AQUA + "Max Level: " + getMaxLevel());
        commandSender.sendMessage(ChatColor.AQUA + "Base Experience Requirement: " + getBaseExperienceRequirement());
        commandSender.sendMessage(ChatColor.AQUA + "Experience Increase Factor: " + getExperienceIncreaseFactor());
    }

    private static int getDefaultMaxLevel() {
        int toReturn = SimpleSkills.getInstance().getPonderAPI().getConfigService().getInt("defaultMaxLevel");
        if (toReturn == 0) {
            toReturn = 100;
            Logger.getInstance().log("DefaultMaxLevel was not found. Using the value: " + toReturn);
        }
        return toReturn;
    }

    private static int getDefaultBaseExperienceRequirement() {
        int toReturn = SimpleSkills.getInstance().getPonderAPI().getConfigService().getInt("defaultBaseExperienceRequirement");
        if (toReturn == 0) {
            toReturn = 10;
            Logger.getInstance().log("DefaultBaseExperienceRequirement was not found. Using the value: " + toReturn);
        }
        return toReturn;
    }

    private static double getDefaultExperienceIncreaseFactor() {
        double toReturn = SimpleSkills.getInstance().getPonderAPI().getConfigService().getDouble("defaultDefaultExperienceIncreaseFactor");
        if (toReturn == 0) {
            toReturn = 1.2;
            Logger.getInstance().log("DefaultExperienceIncreaseFactor was not found. Using the value: " + toReturn);
        }
        return toReturn;
    }
}