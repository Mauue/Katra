package verifier;

import verifier.transformation.Transformation;
import verifier.util.PacketSet;

public class Rule {
    int priority;
    Edge edge;
    PacketSet match;
    PacketSet hit;
    Transformation modify;

    NetworkVerifier nv;

    boolean isPrefix = false;
    long ip;
    public Rule(int p, Edge e, PacketSet match, Transformation t){
        this.priority = p;
        this.edge = e;
        this.match = match;
        this.modify = t;
        this.nv = e.nv;
    }

    public Rule(int p, Edge e, PacketSet match, Transformation t, long ip){
        this(p, e, match, t);
        this.ip = ip;
        isPrefix = true;
    }

    public Edge getEdge() {
        return edge;
    }

    public Node getNode() {
        return edge.src();
    }

    public Node getTarget(){
        return edge.tgt();
    }

    public PacketSet getMatch() {
        return match;
    }

    public PacketSet getHit() {
        return hit;
    }

    public void setHit(PacketSet hit) {
        this.hit = hit;
    }

    public int getPriority() {
        return priority;
    }

    public Transformation getModify() {
        return modify;
    }

    public boolean hasSameForwardingBehavior(Rule rule2){
        return this.edge.equals(rule2.edge) && this.modify.equals(rule2.modify);
    }
}
