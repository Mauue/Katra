package verifier;

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
}
