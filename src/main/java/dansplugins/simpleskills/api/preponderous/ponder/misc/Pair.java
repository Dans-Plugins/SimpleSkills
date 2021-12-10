package dansplugins.simpleskills.api.preponderous.ponder.misc;

// Source: https://stackoverflow.com/a/521235 from user Paul Brinkly and edited by Dave Jarvis
public class Pair<L,R> {

    private final L left;
    private final R right;

    public Pair(L left, R right) {
        assert left != null;
        assert right != null;

        this.left = left;
        this.right = right;
    }

    public L getLeft() { return left; }

    public R getRight() { return right; }

    public int hashCode() { return left.hashCode() ^ right.hashCode(); }

    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.left.equals(pairo.getLeft()) &&
                this.right.equals(pairo.getRight());
    }
}