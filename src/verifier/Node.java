package verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
    String name;
    NetworkVerifier nv;
    List<Edge> in;
    List<Edge> out;

    private Trie rules;
    public Node(String name, NetworkVerifier nv){
        this.name = name;
        this.nv = nv;
        in = new ArrayList<>();
        out = new ArrayList<>();
        rules = new Trie();
    }

    public String getName() {
        return name;
    }

    public NetworkVerifier getNv() {
        return nv;
    }

    public void addEdgeIn(Edge edge){
        in.add(edge);
    }

    public void addEdgeOut(Edge edge){
        out.add(edge);
    }

    public ArrayList<Rule> addAndGetAllUntil(Rule rule) {
        return this.rules.addAndGetAllOverlappingWith(rule);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(name, node.name) && Objects.equals(nv, node.nv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nv);
    }
}
