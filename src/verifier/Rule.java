package verifier;

import verifier.transformation.Transformation;
import verifier.widget.HeaderSet;

public class Rule {
    int priority;
    Edge edge;
    HeaderSet match;
    Transformation modify;

    NetworkVerifier nv;

    boolean isPrefix = false;
    long ip;
    public Rule(int p, Edge e, HeaderSet hs, Transformation t){
        this.priority = p;
        this.edge = e;
        this.match = hs;
        this.modify = t;
        this.nv = hs.getNv();
    }

    public Rule(int p, Edge e, HeaderSet hs, Transformation t, long ip){
        this(p, e, hs, t);
        this.ip = ip;
        isPrefix = true;
    }

    public Edge getEdge() {
        return edge;
    }

    public Node getTarget(){
        return edge.tgt();
    }

    public HeaderSet getMatch() {
        return match;
    }

    public int getPriority() {
        return priority;
    }

    public Transformation getModify() {
        return modify;
    }
}
