package verifier;

import java.util.Objects;

public class Edge {
    Node begin;
    Node end;
    NetworkVerifier nv;

    public Edge(Node begin, Node end){
        this.begin = begin;
        this.end = end;
        this.nv = begin.getNv();
    }

    public Node src(){
        return begin;
    }

    public Node tgt(){
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(begin, edge.begin) && Objects.equals(end, edge.end) && Objects.equals(nv, edge.nv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(begin, end, nv);
    }
}
