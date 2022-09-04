package verifier;

import java.util.*;

public class Node {
    public static int cnt = 0;
    String name;
    NetworkVerifier nv;
    List<Edge> in;
    List<Edge> out;

    Map<String, Edge> edgeMap;

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
        edgeMap = new HashMap<>();
        selfEdge = new Edge(this, this);
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
//        if(edge.end.equals(this)) selfEdge = edge;
        edgeMap.put(edge.tgt().name, edge);
    }

    public void addEdgeOut(String port, Edge edge){
        out.add(edge);
//        if(edge.end.equals(this)) selfEdge = edge;
        edgeMap.put(port, edge);
    }

    public Edge getEdge(String port){
        if(edgeMap.containsKey(port)) return edgeMap.get(port);
        Edge e = new Edge(this, null);
        addEdgeOut(port, new Edge(this, port, null, port));
        return e;
    }


    public ArrayList<Rule> addAndGetAllUntil(Rule rule) {
        ArrayList<Rule> res = this.rules.addAndGetAllOverlappingWith(rule);
//        System.out.println("========================================");
//        System.out.println("insert:" + rule);
//        System.out.println("overlap:" + res);
//        System.out.println("========================================");
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
