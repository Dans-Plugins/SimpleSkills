package dansplugins.simpleskills.objects.abs;

/**
 * @author Daniel Stephenson
 */
public abstract class Benefit {
    private int ID;
    private String name;
    private boolean active;

    public Benefit(int ID, String name) {
        this.ID = ID;
        this.name = name;
        active = true;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}