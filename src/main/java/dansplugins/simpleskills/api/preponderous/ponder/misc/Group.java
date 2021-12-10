/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package preponderous.ponder.misc;

import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Group {
    protected String name = "defaultName";
    protected String description = "defaultDescription";
    protected UUID owner = UUID.randomUUID();
    protected ArrayList<UUID> members = new ArrayList();
    protected ArrayList<UUID> officers = new ArrayList();
    private ArrayList<UUID> invited = new ArrayList();

    public void setName(String newName) {
        this.name = newName;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String newDesc) {
        this.description = newDesc;
    }

    public String getDescription() {
        return this.description;
    }

    public void setOwner(UUID UUID2) {
        this.owner = UUID2;
    }

    public boolean isOwner(UUID UUID2) {
        return this.owner.equals(UUID2);
    }

    public UUID getOwner() {
        return this.owner;
    }

    public void addMember(UUID UUID2) {
        this.members.add(UUID2);
    }

    public void removeMember(UUID UUID2) {
        this.members.remove(UUID2);
    }

    public boolean isMember(UUID uuid) {
        return this.members.contains(uuid);
    }

    public ArrayList<UUID> getMemberList() {
        return this.members;
    }

    public ArrayList<UUID> getMemberArrayList() {
        return this.members;
    }

    public boolean addOfficer(UUID newOfficer) {
        if (!this.officers.contains(newOfficer)) {
            this.officers.add(newOfficer);
            return true;
        }
        return false;
    }

    public boolean removeOfficer(UUID officerToRemove) {
        return this.officers.remove(officerToRemove);
    }

    public boolean isOfficer(UUID uuid) {
        return this.officers.contains(uuid);
    }

    public int getNumOfficers() {
        return this.officers.size();
    }

    public ArrayList<UUID> getOfficerList() {
        return this.officers;
    }

    public int getPopulation() {
        return this.members.size();
    }

    public void invite(UUID playerName) {
        Player player = Bukkit.getServer().getPlayer(playerName);
        if (player != null) {
            UUID playerUUID = Bukkit.getServer().getPlayer(playerName).getUniqueId();
            this.invited.add(playerUUID);
        }
    }

    public void uninvite(UUID player) {
        this.invited.remove(player);
    }

    public boolean isInvited(UUID uuid) {
        return this.invited.contains(uuid);
    }
}

