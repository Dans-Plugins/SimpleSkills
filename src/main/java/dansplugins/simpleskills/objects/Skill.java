package dansplugins.simpleskills.objects;

public class Skill {
    private int ID;
    private String name;
    private int maxLevel;

    public Skill(int ID, String name, int maxLevel) {
        this.ID = ID;
        this.name = name;
        this.maxLevel = maxLevel;
    }
}