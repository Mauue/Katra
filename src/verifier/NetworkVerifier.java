package verifier;

import verifier.check.Check;
import verifier.transformation.*;
import verifier.transformation.TSet;
import verifier.widget.HeaderSet;
import verifier.widget.IPPrefix;
import verifier.widget.LocatedPacket;
import verifier.widget.Range;

import java.util.*;

public class NetworkVerifier {
    HeaderType headerType;
    Map<String, Node> nodes;
    List<Check> checks;
    List<Rule> rules;
    public NetworkVerifier(HeaderType ht){
        headerType = ht;
        headerType.setNv(this);
        checks = new ArrayList<>();
        nodes = new HashMap<>();
        rules = new LinkedList<>();
    }

    public HeaderSet createRange(Map<String, Range> r){
        return headerType.createRange(r);
    }
    public HeaderSet createPrefix(Map<String, IPPrefix> p){
        return headerType.createPrefix(p);
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

    public HeaderSet allHeaders(){
        return new HeaderSet(headerType, 1);
    }

    public void addCheck(Check check){
        this.checks.add(check);
    }

    public Transformation getTPop(){
        return new TPop(this);
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

    public Rule match(Node u, HeaderSet h){
        // TODO
        return null;
    }

    public Rule match(Node u, PEC pec){
        // TODO
        return null;
    }

    public Violation addRule(Rule rule){
        Violation v = new Violation(rule);
        rules.add(rule);
        return v;
    }

    public Trace checkProperty(PEC pec, Collection<Node> source){
        List<VNode> visited = new LinkedList<>();
        for(Node s: source){
            VNode u = new VNode(s, pec, new Sequence(this, s, pec));
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
        Rule r = match(u.getLoc(), u.getEc());
        Edge e = r.getEdge();
        Transformation t = r.getModify();
        Sequence s = t.transform(u.getStack());

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
            Sequence sp = s.bot().link(s.top().and(pec));
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
        Sequence sequence;

        Hop previous;
        VNode(Node u, PEC pec, Sequence sequence){
            this.u = u;
            this.pec = pec;
            this.sequence = sequence;
        }

        Node getLoc(){
            return u;
        }

        PEC getEc(){
            return pec;
        }

        Sequence getStack(){
            return sequence;
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
