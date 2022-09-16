package verifier;

import java.util.Objects;

public class Edge {
    Node begin;
    Node end;
    NetworkVerifier nv;

    String name;

    boolean isSelf;
    public Edge(Node begin, Node end){
        this.begin = begin;
        this.end = end;
        this.nv = begin.getNv();
        if(end == null)
            this.name = begin.getName() + "->null";
        else
            this.name = begin.getName() + "->" + end.name;
        this.isSelf = begin == end;
    }

    public Edge(Node begin, String end){
        this.begin = begin;
        this.end = null;
        this.nv = begin.getNv();
        this.name = begin.getName() + "->" + end;

        this.isSelf = false;
    }

    public Edge(Node begin, String port1, Node end, String port2){
        this.begin = begin;
        this.end = end;
        this.nv = begin.getNv();
        if(end == null)
            this.name = begin.getName() + "," + port1 + "->null";
        else
            this.name = begin.getName() + "," + port1 + "->" + end.name + "," + port2;
        this.isSelf = begin == end;
    }

    public Node src(){
        return begin;
    }

    public Node tgt(){
        return end;
    }

    @Override
    public String toString() {
        return "<" + name + '>';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return edge.name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
