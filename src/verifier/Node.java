package verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
    public static int cnt = 0;
    String name;
    NetworkVerifier nv;
    List<Edge> in;
    List<Edge> out;

    private Trie rules;
    Edge selfEdge;

    public int uid;
    public Node(String name, NetworkVerifier nv){
        this.name = name;
        this.nv = nv;
        in = new ArrayList<>();
        out = new ArrayList<>();
        rules = new Trie();

        uid = cnt;
        cnt++;
    }

    public String getName() {
        return name;
    }

    public NetworkVerifier getNv() {
        return nv;
    }

    public void addEdgeIn(Edge edge){
        in.add(edge);
        if(edge.begin.equals(this)) selfEdge = edge;
    }

    public void addEdgeOut(Edge edge){
        out.add(edge);
        if(edge.end.equals(this)) selfEdge = edge;
    }

    public ArrayList<Rule> addAndGetAllUntil(Rule rule) {
        ArrayList<Rule> res = this.rules.addAndGetAllOverlappingWith(rule);
        System.out.println("========================================");
        System.out.println("insert:" + rule);
        System.out.println("overlap:" + res);
        System.out.println("========================================");
        return res;
    }

    public Edge getSelfEdge(){
        if(selfEdge == null){
            selfEdge = nv.getOrAddBiEdge(this, this).getFirst();
        }
        return selfEdge;
    }

    @Override
    public String toString() {
        return name;
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
