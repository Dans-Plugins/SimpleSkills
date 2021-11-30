package dansplugins.simpleskills.objects.skills;

import org.bukkit.Material;

import java.util.HashSet;

public abstract class Skill {
    private int ID;
    private String name;
    private int maxLevel;
    private boolean active;
    private HashSet<Material> associatedMaterials = new HashSet<>();

    public Skill(int ID, String name, int maxLevel) {
        this.ID = ID;
        this.name = name;
        this.maxLevel = maxLevel;
        this.active = true;
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

    public void initializeAssociatedMaterials(HashSet<Material> associatedMaterials) {
        this.associatedMaterials = associatedMaterials;
    }

    public boolean isMaterialAssociated(Material material) {
        return associatedMaterials.contains(material);
    }
}