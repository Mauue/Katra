package verifier;

import verifier.transformation.Transformation;
import verifier.util.Behavior;
import verifier.util.IPPrefix;
import verifier.util.PacketSet;

public class Rule {
    int priority;
    Edge edge;
    PacketSet match;
    PacketSet hit;
    Transformation modify;

//    NetworkVerifier nv;

    Behavior behavior;
    boolean isPrefix = false;
    long ip;
    int prefix;

    public void setPrefixRule(long ip, int prefix){
        this.isPrefix = true;
        this.ip = ip;
        this.prefix = prefix;
    }

    public void setPrefixRule(IPPrefix ipPrefix){
        this.isPrefix = true;
        this.ip = ipPrefix.getIP();
        this.prefix = ipPrefix.getPrefix();
    }
    public Rule(int p, Edge e, PacketSet match, Transformation t){
        this.priority = p;
        this.edge = e;
        this.match = match;
        this.hit = new PacketSet(match);
        this.modify = t;
//        this.nv = e.nv;

        this.behavior = new Behavior(e, t);
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

    public long getDstIp(){
        if(isPrefix){
            return ip;
        }
        return 0;
    }
    public boolean isPriorityThan(Rule r){
        return priority < r.priority;
    }

    public Transformation getModify() {
        return modify;
    }

    public boolean hasSameForwardingBehavior(Rule rule2){
        return this.edge.equals(rule2.edge) && this.modify.equals(rule2.modify);
    }

    public Behavior getBehavior() {
        return behavior;
    }

    @Override
    public String toString() {
        return "Rule{" + + priority +
                ", " + edge +
                ", " + match +
                ", " + modify +
                '}';
    }
}
