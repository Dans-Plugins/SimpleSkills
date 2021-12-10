/*
 * Decompiled with CFR 0.150.
 */
package preponderous.ponder.misc;

public class Pair<L, R> {
    private final L left;
    private final R right;

    public Pair(L left, R right) {
        assert (left != null);
        assert (right != null);
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return this.left;
    }

    public R getRight() {
        return this.right;
    }

    public int hashCode() {
        return this.left.hashCode() ^ this.right.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair pairo = (Pair)o;
        return this.left.equals(pairo.getLeft()) && this.right.equals(pairo.getRight());
    }
}

