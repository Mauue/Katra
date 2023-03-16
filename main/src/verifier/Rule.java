package verifier;

import verifier.transformation.Transformation;
import verifier.util.Behavior;
import verifier.util.IPPrefix;
import verifier.util.PacketSet;

import java.util.Objects;

public class Rule {
    int priority;
    Edge edge;
    PacketSet match;
    PacketSet notMatch;
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
        this.notMatch = match.not();
    }
    public Rule(int priority, Edge e, long ip, int prefix, Transformation t){
        this.ip = ip;
        this.prefix = prefix;
        isPrefix = true;
        this.edge = e;
        this.modify = t;
        this.behavior = new Behavior(e, t);
    }
    public Rule(int priority, Edge e, IPPrefix ipPrefix, Transformation t){
        this.ip = ipPrefix.getIP();
        this.prefix = ipPrefix.getPrefix();
        isPrefix = true;
        this.edge = e;
        this.modify = t;
        this.behavior = new Behavior(e, t);
    }
    public Rule(int p, Edge e, PacketSet match, Transformation t, long ip){
        this(p, e, match, t);
        this.ip = ip;
        isPrefix = true;
    }

    public void updatePacketSet(PacketSet match){
        this.match = match;
        this.hit = match;
        this.notMatch = match.not();
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
    public PacketSet getNotMatch() {
        return notMatch;
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

    public IPPrefix getIPPrefix(){
        return new IPPrefix(ip, prefix);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return ip == rule.ip && prefix == rule.prefix && edge.equals(rule.edge) && modify.equals(rule.modify);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, prefix);
    }
}
