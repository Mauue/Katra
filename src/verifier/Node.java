package verifier;

import java.util.ArrayList;
import java.util.List;

public class Node {
    String name;
    NetworkVerifier nv;
    List<Edge> in;
    List<Edge> out;
    public Node(String name, NetworkVerifier nv){
        this.name = name;
        this.nv = nv;
        in = new ArrayList<>();
        out = new ArrayList<>();
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

}
