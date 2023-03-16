package verifier.util;

import verifier.Node;

import java.util.ArrayList;

public class Behaviors {
    private int hash;
    public ArrayList<Behavior> behaviors;

    public Behaviors(ArrayList<Behavior> behaviors){
        this.behaviors = new ArrayList<>(behaviors);
        this.hash = getHash();
    }

    public void replace(int i, Behavior newPort) {
        this.hash ^= behaviors.get(i).hashCode();
        behaviors.set(i, newPort);
        this.hash ^= newPort.hashCode();
    }

    private int getHash() {
        int hashCode = 0;
        for (Behavior b : this.behaviors)
            hashCode ^= ( b == null ? 0 : b.hashCode() );
        return hashCode;
    }

    public Behavior get(int i) {
        return behaviors.get(i);
    }

    public void reverseArray(ArrayList<Pair<Behavior, Behavior>> changes) {
        for(Pair<Behavior, Behavior> entry: changes) {
            this.hash ^= entry.getFirst().hashCode() ^ entry.getSecond().hashCode();
            int i =  entry.getSecond().getNode().uid;
            assert behaviors.get(i) == entry.getSecond();
            behaviors.set(i, entry.getFirst());
        }
    }

    public Behaviors replaceArray(ArrayList<Pair<Behavior, Behavior>> changes) {
        for(Pair<Behavior, Behavior> entry: changes) {
            this.hash ^= entry.getFirst().hashCode() ^ entry.getSecond().hashCode();
            int i =  entry.getFirst().getNode().uid;
            assert behaviors.get(i) == entry.getFirst();
            behaviors.set(i, entry.getSecond());
        }
        return this;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Behaviors) {
            Behaviors t = (Behaviors) o;
            for (int i = 0; i < Node.cnt; i ++) {
                if (this.get(i) != t.get(i)) return false;
            }
            return true;
        }
        return false;
    }

}
