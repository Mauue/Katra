package verifier;

import verifier.util.IPPrefix;
import verifier.util.PacketSet;

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

    PacketSet space;
    List<IPPrefix> spaceList;
    Collection<PacketSet> spacePEC;
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
        space = nv.zeroHeaders();
        spacePEC = new LinkedList<>();
        spaceList = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public NetworkVerifier getNv() {
        return nv;
    }

    public Collection<PacketSet> getSpacePEC() {
        return spacePEC;
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
//        Edge e = getSelfEdge();
//        addEdgeOut(port, new Edge(this, port, null, port));
        return null;
    }

    public void clearSpace(){
        this.space = nv.zeroHeaders();
    }
    public void addSpace(IPPrefix ip){
        this.spaceList.add(ip);
    }

    public void updateSpace(){
        for(IPPrefix ipPrefix:spaceList){
            PacketSet s = nv.createPrefix("dstip", ipPrefix);
            this.space = this.space.or(s);
            s.release();
        }
    }

    public PacketSet getSpace() {
        return space;
    }

    public void updateSpacePEC(){
        for(PacketSet pec: nv.pecs){
            if(pec.hasOverlap(space)){
                this.spacePEC.add(pec);
            }
        }
//        System.out.println(name + getSpacePEC());
    }
    public ArrayList<Rule> addAndGetAllUntil(Rule rule) {
        ArrayList<Rule> res = this.rules.addAndGetAllOverlappingWith(rule);
        return res;
    }

    public ArrayList<Rule> getAllUntil(Rule rule) {
        ArrayList<Rule> res = this.rules.getAllOverlappingWith(rule);
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
