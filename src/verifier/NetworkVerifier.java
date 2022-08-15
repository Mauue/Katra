package verifier;

import verifier.check.Check;
import verifier.transformation.*;
import verifier.transformation.TSet;
import verifier.util.*;

import java.util.*;

public class NetworkVerifier {
    HeaderType headerType;
    Map<String, Node> nodes;
    List<Check> checks;
    List<Rule> rules;

    Collection<PEC> pecs;

    public NetworkVerifier(HeaderType ht){
        headerType = ht;
        headerType.setNv(this);
        checks = new ArrayList<>();
        nodes = new HashMap<>();
        rules = new LinkedList<>();
    }

    public PacketSet createRange(Map<String, Range> r){
        return headerType.createRange(r);
    }
    public PacketSet createPrefix(Map<String, IPPrefix> p){
        return headerType.createPrefix(p);
    }

    public PacketSet createSingle(Map<String, Integer> r){
        return headerType.createSingle(r);
    }

    public List<Node> getOrAddNodes(String... nodes){
        List<Node> result = new ArrayList<>(nodes.length);
        for(String name: nodes){
            if(this.nodes.containsKey(name)){
                result.add(this.nodes.get(name));
            }else{
                Node n = new Node(name, this);
                result.add(n);
                this.nodes.put(name, n);
            }
        }
        return result;
    }

    public List<Edge> getOrAddBiEdge(Node n1, Node n2){
        Edge edge12 = new Edge(n1, n2);
        n1.addEdgeOut(edge12); n2.addEdgeIn(edge12);

        Edge edge21 = new Edge(n2, n1);
        n2.addEdgeOut(edge21); n1.addEdgeIn(edge21);

        return Arrays.asList(edge12, edge21);
    }

    public PacketSet allHeaders(){
        return new PacketSet(headerType, 1);
    }

    public void addCheck(Check check){
        this.checks.add(check);
    }

    public Transformation getTPop(){
        return new TPop(this);
    }

    public Transformation getTDrop(){
        return new TDrop(this);
    }

    public Transformation getTTtl(){
        return new TTtl(this);
    }

    public Transformation getTDelv(){
        return new TDelv(this);
    }
    public Transformation getTPush(){
        return new TPush(this);
    }

    public Transformation getTSet(String name, int value){
        return new TSet(this, name, value);
    }

    public Transformation getTSeq(Transformation... transformations){
        return new TSeq(this, transformations);
    }

    public Rule forward(Node u, PEC pec){
        // todo
        return null;
    }

    public Violation addRule(Rule rule){
        Violation v = new Violation(rule);
        rules.add(rule);
        return v;
    }

    public void addRules(Rule... rule){
//        Violation v = new Violation(rule);
        rules.addAll(Arrays.asList(rule));
    }

    public void calInitPEC(){
        Comparator<Rule> comp = (Rule lhs, Rule rhs) -> {
            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
            if (lhs.getPriority() > rhs.getPriority()) return -1;
            if (lhs.getPriority() < rhs.getPriority()) return 1;

//            if (lhs.getDstIp() > rhs.getDstIp()) return -1;
//            if (lhs.getDstIp() < rhs.getDstIp()) return 1;

            if (lhs.getEdge().hashCode() > rhs.getEdge().hashCode()) return -1;
            if (lhs.getEdge().hashCode() < rhs.getEdge().hashCode()) return 1;

            if (lhs.getNode().hashCode() ==  lhs.getNode().hashCode()) return 0;
            if (lhs.getNode().hashCode() <  lhs.getNode().hashCode()) {
                return -1;
            } else {
                return 1;
            }
        };

        Collections.sort(rules, comp);
        // WARNING: the delta of a mini-batch is based on the very initial state,
        // i.e., change of any rule in the batch is not allowed;
        // Although sorting in descending order of priority can achieve the goal,
        // this may not be the best implementation.
        Changes changes = new Changes(headerType);
        for (Rule rule : rules) {
            identifyChangesInsert(rule, changes);
        }
        // todo

    }
    public Trace checkProperty(PEC pec, Collection<Node> source){
        List<VNode> visited = new LinkedList<>();
        for(Node s: source){
            VNode u = new VNode(s, pec, new HeaderStack(this, s, pec));
            if(!visited.contains(u)){
                u.previous = null;
                Trace trace = dfs(visited, u, 0);
                if(trace != null)
                    return trace;
            }
        }
        return null;
    }

    private Trace dfs(List<VNode> visited, VNode u, int i){
        Rule r = forward(u.getLoc(), u.getEc());
        Edge e = r.getEdge();
        Transformation t = r.getModify();
        HeaderStack s = t.transform(u.getStack());

        if (s == null) {
            if(checkProperty()){
                return null;
            }else{
                return u.getTrace();
            }
        }

        List<Hop> nextHops = new LinkedList<>();
        Collection<PEC> overlappingEcs = getOverlappingEcs(s.top());
        for(PEC pec: overlappingEcs){
            HeaderStack sp = s.bot().link(s.top().and(pec));
            if(!sp.top().equals(s.top()) || s.getLen() != sp.getLen()){
                sp.repair();
            }
            VNode v = new VNode(e.tgt(), pec, sp);
            nextHops.add(new Hop(t, v));
        }

        visited.add(u);

        for(Hop hop: nextHops){
            VNode v = hop.u;
            v.previous = new Hop(t, u);
            if(hasLoop(v, visited))
                return v.getTrace();

            if(!visited.contains(v)){
                Trace trace = dfs(visited, v, i+1);
                if (trace != null){
                    return trace;
                }
            }
        }

        return null;
    }

    public void identifyChangesInsert(Rule rule, Changes changes) {
//        int entryBdd = this.model.bddEngine.encodeDstIPPrefix(rule.getDstIp(), rule.getPriority());
//        rule.setNMatch(bdd.ref(bdd.not(entryBdd)));
//        rule.setHit(entryBdd);

        Node node = rule.getNode();
        for (Rule r : node.addAndGetAllUntil(rule)) {
            if (r.getPriority() > rule.getPriority()) {
                PacketSet newHit =rule.getHit().and(r.getMatch());

                rule.setHit(newHit);
            }

            if (rule.getHit().isEmpty()) return;

            if (r.getPriority() < rule.getPriority()) {
                PacketSet intersection = r.getHit().and(rule.getHit());
                if (!r.hasSameForwardingBehavior(rule)) changes.add(intersection, new Behavior(r.getEdge(), r.modify), new Behavior(rule.getEdge(), rule.modify));

                PacketSet tmp = intersection.not();
                PacketSet newHit = r.getHit().and(tmp);
                r.setHit(newHit);
            }
        }
    }
    private boolean hasLoop(VNode u, Collection<VNode> visited){
        //todo
        return false;
    }
    private Collection<PEC> getOverlappingEcs(LocatedPacket lp){
        // todo
        return new LinkedList<>();
    }

    private boolean checkProperty(){
        // todo
        return true;
    }

    static class VNode{
        Node u;
        PEC pec;
        HeaderStack headerStack;

        Hop previous;
        VNode(Node u, PEC pec, HeaderStack headerStack){
            this.u = u;
            this.pec = pec;
            this.headerStack = headerStack;
        }

        Node getLoc(){
            return u;
        }

        PEC getEc(){
            return pec;
        }

        HeaderStack getStack(){
            return headerStack;
        }

        Trace getTrace(){
            // todo
            return null;
        }
    }

    static class Hop {
        Transformation t;
        VNode u;
        public Hop(Transformation t, VNode u){
            this.t = t;
            this.u = u;
        }
    }

}
