package verifier.util;

import verifier.Edge;
import verifier.Node;

import java.util.ArrayList;

public class Edges {
    private int hash;
    public ArrayList<Edge> ports;

    public Edges(ArrayList<Edge> ports) {
        this.ports = (ArrayList<Edge>) ports.clone();
        this.hash = this.getHash();
    }

    public Edge get(int i) {
        return ports.get(i);
    }

    public void replace(int i, Edge newPort) {
        this.hash ^= ports.get(i).hashCode();
        ports.set(i, newPort);
        this.hash ^= newPort.hashCode();
    }

//    public void reverseArray(ArrayList<Change> changes) {
//        for (Change change : changes) {
//            this.hash ^= change.oldEdge.hashCode() ^ change.newEdge.hashCode();
//            int i = change.newEdge.src().uid;
//            assert ports.get(i) == change.oldEdge;
//            ports.set(i, change.oldEdge);
//        }
//    }
//
//    public Edges replaceArray(ArrayList<Change> changes) {
//        for (Change change : changes) {
//            this.hash ^= change.oldEdge.hashCode() ^ change.newEdge.hashCode();
//            int i = change.oldEdge.src().uid;
//            assert ports.get(i) == change.oldEdge;
//            ports.set(i, change.newEdge);
//        }
//        return this;
//    }

    private int getHash() {
        int hashCode = 0;
        for (Edge e : this.ports)
            hashCode ^= ( e == null ? 0 : e.hashCode() );
        return hashCode;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Edge) {
            Edges t = (Edges) o;
            for (int i = 0; i < Node.cnt; i ++) {
                if (this.get(i) != t.get(i)) return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        ports.forEach(p->res.append(p.toString()));
        return res.toString();
    }
}
