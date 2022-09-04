package verifier;

import verifier.check.Check;
import verifier.transformation.*;
import verifier.transformation.TSet;
import verifier.util.*;

import java.util.*;

public class NetworkVerifier {
    HeaderType headerType;
    public Map<String, Node> nodes;
    List<Edge> edges;
    List<Rule> rules;

    Collection<PacketSet> pecs;
    Map<Behaviors, PacketSet> predMap;
    Map<PacketSet, Behaviors> behaviorMap;


    public NetworkVerifier(){
        nodes = new HashMap<>();
        rules = new LinkedList<>();
        predMap = new HashMap<>();
        behaviorMap = new HashMap<>();
        pecs = new HashSet<>();
        edges = new LinkedList<>();
        headerType = HeaderType.headerType;
        headerType.setNv(this);

    }

    public PacketSet createRange(Map<String, Range> r){
        return headerType.createRange(r);
    }
    public PacketSet createPrefix(Map<String, IPPrefix> p){
        return headerType.createPrefix(p);
    }

    public PacketSet createPrefix(String name, Long ip, int prefix){
        IPPrefix ipp = new IPPrefix(ip, prefix);
        Map<String, IPPrefix> map = new HashMap<>();
        map.put(name, ipp);
        return createPrefix(map);
    }

    public PacketSet createSingle(Map<String, Integer> r){
        return headerType.createSingle(r);
    }

    public PacketSet createSingle(String name, int value){
        Map<String, Integer> singleMap = new HashMap<>();
        singleMap.put(name, value);
        return headerType.createSingle(singleMap);
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

    public Pair<Edge, Edge> getOrAddBiEdge(Node n1, Node n2){
        Edge edge12 = new Edge(n1, n2);
        n1.addEdgeOut(edge12); n2.addEdgeIn(edge12);
        if(n1.equals(n2)) return new Pair<>(edge12, edge12);
        Edge edge21 = new Edge(n2, n1);
        n2.addEdgeOut(edge21); n1.addEdgeIn(edge21);
        edges.add(edge12);
        edges.add(edge21);
        return new Pair<>(edge12, edge21);
    }

    public Pair<Edge, Edge> getOrAddBiEdge(Node n1, String port1, Node n2, String port2){
        Edge edge12 = new Edge(n1, port1, n2, port2);
        n1.addEdgeOut(port1, edge12); n2.addEdgeIn(edge12);
        if(n1.equals(n2)) return new Pair<>(edge12, edge12);
        Edge edge21 = new Edge(n2, n1);
        n2.addEdgeOut(port2, edge21); n1.addEdgeIn(edge21);
        edges.add(edge12);
        edges.add(edge21);
        return new Pair<>(edge12, edge21);
    }

    public PacketSet allHeaders(){
        return HeaderType.allHeader();
    }

    public PacketSet zeroHeaders(){
        return HeaderType.zeroHeader();
    }


    public Transformation getTID() {return TId.getTId(this);}
    public Transformation getTPop(){
        return TPop.getTPop(this);
    }

    public Transformation getTDrop(){
        return TDrop.getTDrop(this);
    }

    public Transformation getTTtl(){
        return new TTtl(this);
    }

    public Transformation getTDelv(){
        return TDelv.getTDelv(this);
    }
    public Transformation getTPush(){
        return TPush.getTPush(this);
    }

    public Transformation getTSet(PacketSet p){
        return new TSet(this, p);
    }

    public Transformation getTSeq(Transformation... transformations){
        return new TSeq(this, transformations);
    }

    public Behavior forward(Node u, PacketSet pec){
        Behaviors behaviors = behaviorMap.get(pec);

        return behaviors.get(u.uid);
    }

    public Violation addRule(Rule rule){
        Violation v = new Violation(rule);
        rules.add(rule);
        return v;
    }

    public void addRules(Rule... rule){
//        Violation v = new Violation(rule);
        rules.addAll(Arrays.asList(rule));
//        for(Rule r: rules)
//            System.out.println(r);
    }

    public void addRules(List<Rule> rule){
//        Violation v = new Violation(rule);
        rules.addAll(rule);
//        for(Rule r: rules)
//            System.out.println(r);
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

        initializeModelAndRules();
        Collections.sort(rules, comp);

//        List<Behavior> b = new LinkedList<>();
//        rules.forEach(r->{b.add(new Behavior(r.getEdge(), r.modify));});

//        List<Change> changes = new LinkedList<>();
//        for(Node node: nodes.values()){
////            identifyChangesInsert(new Rule(Integer.MAX_VALUE, node.selfEdge, allHeaders(), getTDrop()), changes);
//            Behavior b = new Behavior(node.selfEdge, getTID());
//            predMap.put(b, allHeaders());
//            behaviorMap.get(allHeaders()).add(b);
//        }
        Changes changes = new Changes();
        for (Rule rule : rules) {
            identifyChangesInsert(rule, changes);
        }
        update2(changes);
    }

    public void initializeModelAndRules() {
//        Collection<Behavior> zero = new HashSet<>();
//        for(Behavior behavior: bs){
//            if(behavior.t.getClass() == TDrop.class) continue;
//            predMap.put(behavior, zeroHeaders());
//            zero.add(behavior);
//        }
//        behaviorMap.put(zeroHeaders(), zero);
//
//
//        List<Behavior> behaviors = new LinkedList<>();
//        for(Node n: nodes.values()){
//            behaviors.add(new Behavior(n.getSelfEdge(), getTDrop()));
//        }
//
//        Collection<Behavior> all = new HashSet<>();
//        for(Behavior b: behaviors){
//            predMap.put(b, allHeaders());
//            all.add(b);
//        }
//        behaviorMap.put(allHeaders(), new HashSet<>(all));
//        pecs.add(allHeaders());
        ArrayList<Behavior> ports = new ArrayList<>();
        for (int i = 0; i < Node.cnt; i ++) ports.add(null);
        // Initialize each device with default rule sending packets to default device (i.e., black-hole)
        for (Node node : nodes.values()) {
            Behavior defaultBehavior = new Behavior(node.selfEdge, getTDrop());
            Rule r = new Rule(Integer.MAX_VALUE, node.selfEdge, allHeaders(), getTDrop());
            this.addRule(r);
            // initializing default rules w/o generating changes
            ports.set(node.uid, defaultBehavior);
        }
        Behaviors key = new Behaviors(ports);
        predMap.put(key, allHeaders());

    }

    public void update2(Changes changes){
        changes.aggrBDDs();

        HashMap<Behaviors, PacketSet> oldPred = (HashMap<Behaviors, PacketSet>)((HashMap<Behaviors, PacketSet>) predMap).clone();
        Set<Behaviors> deletion = new HashSet<>();
        for (Map.Entry<PacketSet, ArrayList<Pair<Behavior, Behavior>>> entryI : changes.getAll().entrySet()) {
//            System.out.println(change);
            Behavior filterBehavior = entryI.getValue().get(0).getFirst();
            for(Map.Entry<Behaviors, PacketSet> entry: oldPred.entrySet()){
                Behaviors from = entry.getKey();

                if(!from.get(filterBehavior.getNode().uid).equals(filterBehavior)) continue;

                PacketSet oldPs = predMap.get(from);
                PacketSet intersection = oldPs.and(entryI.getKey());
                if(intersection.isEmpty()) continue;

                PacketSet newPs = oldPs.xor(intersection);
                predMap.replace(from, newPs);
                if (newPs.isEmpty()) deletion.add(from);

                Behaviors to = from.replaceArray(entryI.getValue());
                if (predMap.containsKey(to)) {
                    oldPs = predMap.get(to);
                    newPs = oldPs.xor(intersection);
                    predMap.replace(to, newPs);
                } else {
                    predMap.put(new Behaviors(to.behaviors), intersection);
                }

                from.reverseArray(entryI.getValue());
            }

            for (Behaviors t : deletion) {
                if (predMap.containsKey(t) && predMap.get(t).isEmpty()) predMap.remove(t);
            }
        }
        predMap.forEach((k, v)-> {behaviorMap.put(v, k); pecs.add(v);});
        System.out.println(predMap.size());
        System.out.println(predMap);
    }

    public Trace checkProperty(PacketSet pec, Collection<Node> source, List<Check> checks){
        List<VNode> visited = new LinkedList<>();
        for(Node s: source){
            VNode u = new VNode(s, pec, new HeaderStack(this, pec));
            if(!visited.contains(u)){
                u.previous = null;
                Trace trace = dfs(visited, u, 0, checks);
                if(trace != null)
                    return trace;
            }
        }
        return null;
    }

    private Trace dfs(List<VNode> visited, VNode u, int i, List<Check> checks){
        Behavior b = forward(u.getLoc(), u.getEc());
        Edge e = b.e;
        Transformation t = b.t;
        HeaderStack s = t.transform(u.getStack());

        if (s == null || e.tgt() == null) {
            if(satisfyProperty(u, t, checks)){
                return u.getTrace();
            }else{
                return null;
            }
        }

        List<Hop> nextHops = new LinkedList<>();

        for(PacketSet pec: getOverlappingEcs(s.top())){
            HeaderStack sp = s.bot().push(s.top().and(pec));
            if(!sp.top().equals(s.top()) || s.getLen() != sp.getLen()){
                sp.repair();
            }
            VNode v = new VNode(e.tgt(), pec, sp);
            nextHops.add(new Hop(t, v));
        }

        visited.add(u);

        for(Hop hop: nextHops){
            VNode v = hop.u;
            v.setPrevious(new Hop(t, u));
            if(hasLoop(v, visited))
                return v.getTrace();

            if(!visited.contains(v)){
                Trace trace = dfs(visited, v, i+1, checks);
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
            if (r.isPriorityThan(rule)) {
                PacketSet newHit = rule.getHit().and(r.getMatch().not());

                rule.setHit(newHit);
            }

            if (rule.getHit().isEmpty()) return;

            if (rule.isPriorityThan(r)) {
                PacketSet intersection = r.getHit().and(rule.getHit());
                if(intersection.isEmpty()) continue;
                if (!r.hasSameForwardingBehavior(rule)) changes.add(intersection, r.getBehavior(), rule.getBehavior());

                PacketSet tmp = intersection.not();
                PacketSet newHit = r.getHit().and(tmp);
                r.setHit(newHit);
            }
        }
    }
    private boolean hasLoop(VNode u, Collection<VNode> visited){
        Collection<VNode> collection = new HashSet<>();
        visited.forEach(vNode -> {if(vNode.getLoc() == u.getLoc()) collection.add(vNode);});
        VNode c = u.getPrevious().u;
        int l = u.headerStack.getLen();
        while( c != null && !collection.isEmpty()){
            l = Math.min(l, c.headerStack.getLen());
            if(collection.contains(c)){
                collection.remove(c);
                int gamma = getLongestCommonSuffix(u.headerStack, c.headerStack);
                if(l>c.headerStack.getLen()-gamma){
                    return true;
                }
            }
            c = c.getPrevious().u;
        }
        return false;
    }

    public Collection<PacketSet> getPecs() {
        return pecs;
    }

    private int getLongestCommonSuffix(HeaderStack s1, HeaderStack s2){
        List<PacketSet> sequence1 = s1.sequences;
        List<PacketSet> sequence2 = s2.sequences;
        int res = 0, index1 = sequence1.size()-1, index2 = sequence2.size()-1;
        while (index1>=0 && index2>=0){
            if(sequence1.get(index1).equals(sequence2.get(index2))){
                res++;
                index2--;
                index1--;
            }else break;
        }
        return res;
    }
    private Collection<PacketSet> getOverlappingEcs(PacketSet header){
        Collection<PacketSet> res = new LinkedList<>();
        for(PacketSet pec: pecs){
            if(pec.hasOverlap(header)){
                res.add(pec);
            }
        }
        return res;
    }

//    public void printBV(PacketSet ps){
//        HeaderType.printBV(ps);
//    }
    private boolean satisfyProperty(VNode n, Transformation t, List<Check> checks){
        for(Check check: checks){
            if(!check.isSatisfy(n.u, t)) return false;
        }
        return true;
    }

    static class VNode{
        Node u;
        PacketSet pec;
        HeaderStack headerStack;

        Node source;
        private Hop previous;
        VNode(Node u, PacketSet pec, HeaderStack headerStack){
            this.u = u;
            this.pec = pec;
            this.headerStack = headerStack;
            this.source = u;
        }

        public void setPrevious(Hop previous) {
            this.previous = previous;
            this.source = previous.u.source;
        }

        public Hop getPrevious() {
            return previous;
        }

        Node getLoc(){
            return u;
        }

        PacketSet getEc(){
            return pec;
        }

        HeaderStack getStack(){
            return headerStack;
        }

        Trace getTrace(){
            VNode n = this;
            Trace t = new Trace();
            while (n != null){
                t.add(n.u, n.getEc());
                if(n.getPrevious() != null)
                    n = n.getPrevious().u;
                else
                    n = null;
            }
            return t;
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
