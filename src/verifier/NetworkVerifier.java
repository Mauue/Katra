package verifier;

import verifier.check.Check;
import verifier.check.ReachabilityCheck;
import verifier.transformation.*;
import verifier.transformation.TSet;
import verifier.util.*;

import java.util.*;

public class NetworkVerifier {
    public HeaderType headerType;
    public Map<String, Node> nodes;
    List<Edge> edges;
    List<Rule> rules;

//    Set<Rule> rulesSet;
    Collection<PacketSet> pecs;
    Map<Behaviors, PacketSet> predMap;
    Map<PacketSet, Behaviors> behaviorMap;

    public List<String[]> sequences;
    public NetworkVerifier(){
        nodes = new HashMap<>();
        rules = new LinkedList<>();
        predMap = new HashMap<>();
        behaviorMap = new HashMap<>();
        pecs = new HashSet<>();
        edges = new LinkedList<>();
        sequences = new ArrayList<>();

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

    public PacketSet createPrefix(String name, IPPrefix ip){
        Map<String, IPPrefix> map = new HashMap<>();
        map.put(name, ip);
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
        return headerType.allHeader();
    }

    public PacketSet zeroHeaders(){
        return headerType.zeroHeader();
    }


    public Transformation getTID() {return new TId(this);}
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

    public Transformation getTSet(PacketSet p){
        return new TSet(this, p);
    }

    public Transformation getTSet(IPPrefix p){
        return new TSet(this, p);
    }

    public Transformation getTSeq(Transformation... transformations){
        return new TSeq(this, transformations);
    }

    public Behavior forward(Node u, PacketSet pec){
        Behaviors behaviors = behaviorMap.get(pec);

        return behaviors.get(u.uid);
    }

    public void addDefaultRule(Rule rule){
//        rules.add(rule);
        Changes changes = new Changes();
        this.identifyChangesInsert(rule, changes);
    }

    public void addRules(Rule... rule){
        rules.addAll(Arrays.asList(rule));
    }
//    public void addURules(List<URule> rs){
//        uRules.addAll(rs);
//    }
    public void addRules(List<Rule> rule){
//        Violation v = new Violation(rule);
        rules.addAll(rule);
//        for(Rule r: rules)
//            System.out.println(r);
    }
    public void checkAllReachability(){
        List<Check> checks = new LinkedList<>();
        for(Node src: nodes.values()) {
            for(Node dst: nodes.values()) {
                if(src.equals(dst)) continue;
                for(PacketSet pec: dst.getSpacePEC()) {
                    checks.clear();
                    checks.add(new ReachabilityCheck(pec, dst, this));
                    checkProperty(pec, Collections.singletonList(src), checks);
                    break;
                }
            }
        }
    }
    static Comparator<Rule> comp = (Rule lhs, Rule rhs) -> {
        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
        if (lhs.getPriority() > rhs.getPriority()) return 1;
        if (lhs.getPriority() < rhs.getPriority()) return -1;

        if (lhs.getDstIp() > rhs.getDstIp()) return -1;
        if (lhs.getDstIp() < rhs.getDstIp()) return 1;

        if (lhs.getEdge().hashCode() > rhs.getEdge().hashCode()) return -1;
        if (lhs.getEdge().hashCode() < rhs.getEdge().hashCode()) return 1;

        if (lhs.getNode().hashCode() ==  lhs.getNode().hashCode()) return 0;
        if (lhs.getNode().hashCode() <  lhs.getNode().hashCode()) {
            return -1;
        } else {
            return 1;
        }
    };
    private void updateSpace(){
        for(Node node:nodes.values()){
            node.updateSpace();
        }
    }
    private void updateRule(){
        if(!rules.isEmpty()){
            for(Rule rule: rules){
                rule.updatePacketSet(createPrefix("dstip", rule.getIPPrefix()));
            }
        }
    }
    public void calInitPEC(){
        long t1 = System.nanoTime();
        updateSpace();
        Transformation.updateAll();
        updateRule();

        initializeModelAndRules();
        Collections.sort(rules, comp);
        Changes changes = new Changes();
        for (Rule rule : rules) {
            identifyChangesInsert(rule, changes);
        }
        long t2 = System.nanoTime();
        update2(changes);
        long t3 = System.nanoTime();
//        System.out.println("model rule time " + (t2-t1)/1000000.0 + " ms");
//        System.out.println("update time " + (t3-t2)/1000000.0 + " ms");

    }

    public void initializeModelAndRules() {
        ArrayList<Behavior> ports = new ArrayList<>();
        for (int i = 0; i < Node.cnt; i ++) ports.add(null);
        // Initialize each device with default rule sending packets to default device (i.e., black-hole)
        for (Node node : nodes.values()) {
            Behavior defaultBehavior = new Behavior(node.selfEdge, getTDrop());
            Rule r = new Rule(Integer.MAX_VALUE, node.selfEdge, allHeaders(), getTDrop());
            r.setPrefixRule(0, 0);
            this.addDefaultRule(r);
            // initializing default rules w/o generating changes
            ports.set(node.uid, defaultBehavior);
        }
        Behaviors key = new Behaviors(ports);
        predMap.put(key, allHeaders());
    }

    public void update2(Changes changes){
        changes.aggrBDDs();
        for (Map.Entry<PacketSet, ArrayList<Pair<Behavior, Behavior>>> entryI : changes.getAll().entrySet()) {
//            System.out.println(change);
            Set<Behaviors> deletion = new HashSet<>();
            Behavior filterBehavior = entryI.getValue().get(0).getFirst();

            HashMap<Behaviors, PacketSet> oldPred = (HashMap<Behaviors, PacketSet>)((HashMap<Behaviors, PacketSet>) predMap).clone();
            for(Map.Entry<Behaviors, PacketSet> entry: oldPred.entrySet()){
                Behaviors from = entry.getKey();

                if(!from.get(filterBehavior.getNode().uid).equals(filterBehavior)) continue;

                PacketSet oldPs = predMap.get(from);
                PacketSet intersection = oldPs.and(entryI.getKey());
                if(intersection.isEmpty()) continue;

                PacketSet newPs = oldPs.xor(intersection);
                newPs.increase();
                predMap.replace(from, newPs);
                if (newPs.isEmpty()) deletion.add(from);

                Behaviors to = from.replaceArray(entryI.getValue());
                if (predMap.containsKey(to)) {
                    oldPs = predMap.get(to);
                    newPs = oldPs.xor(intersection);
                    oldPs.release();
                    predMap.replace(to, newPs);
                } else {
                    intersection.increase();
                    predMap.put(new Behaviors(to.behaviors), intersection);
                }

                from.reverseArray(entryI.getValue());
                intersection.release();
            }

            for (Behaviors t : deletion) {
                if (    predMap.get(t).isEmpty()) predMap.remove(t);
            }
        }
        changes.releaseBdd();
        predMap.forEach((k, v)-> {behaviorMap.put(v, k); pecs.add(v);});
//        System.out.println(predMap.size());
    }

    public List<Trace> checkProperty(PacketSet pec, Collection<Node> source, List<Check> checks){
        List<Trace> traces = new LinkedList<>();
        for(Node s: source){
            List<Node> visited = new LinkedList<>();
            VNode u = new VNode(s, pec, new HeaderStack(this, pec));
            u.previous = null;
            dfs(visited, u, 0, checks, traces);

        }
        return traces;
    }

    private void dfs(List<Node> visited, VNode u, int i, List<Check> checks, List<Trace> traces){
        Behavior b = forward(u.getLoc(), u.getEc());
        Edge e = b.e;
        Transformation t = b.t;
        HeaderStack s = t.transform(u.getStack());

        if (s == null || e.tgt() == null) {
//            if(satisfyProperty(u, t, checks)){
//                traces.add(u.getTrace());
//            }
            return;
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
        Set<Node> old = new HashSet<>(visited);
        visited.add(u.u);

        for(Hop hop: nextHops){
            VNode v = hop.u;
            v.setPrevious(new Hop(t, u));
            if(hasLoop2(v, old))
                continue;

//            if(!visited.contains(v)){
            dfs(visited, v, i+1, checks, traces);
//            }
        }
    }

    public void identifyChangesInsert(Rule rule, Changes changes) {
//        int entryBdd = this.model.bddEngine.encodeDstIPPrefix(rule.getDstIp(), rule.getPriority());
//        rule.setNMatch(bdd.ref(bdd.not(entryBdd)));
        Node node = rule.getNode();
        List<Rule> rules = node.addAndGetAllUntil(rule);

        for (Rule r : rules) {
            if (r.isPriorityThan(rule)) {
                PacketSet newHit = rule.getHit().and(r.getNotMatch());
                rule.getHit().release();
                rule.setHit(newHit);
            }

            if (rule.getHit().isEmpty()) return;

            if (rule.isPriorityThan(r)) {
                PacketSet intersection = r.getHit().and(rule.getHit());
                if(intersection.isEmpty()) continue;
                if (!r.hasSameForwardingBehavior(rule))
                    changes.add(intersection, r.getBehavior(), rule.getBehavior());

                PacketSet tmp = intersection.not();
                PacketSet newHit = r.getHit().and(tmp);
                tmp.release();
                intersection.release();

                r.getHit().release();
                r.setHit(newHit);
            }
        }
    }
    public void insertRuleAndUpdate(Rule rule){
        int index = rules.indexOf(rule);
        if(index != -1)
            return;
        rule.updatePacketSet(createPrefix("dstip", rule.getIPPrefix()));
        rules.add(rule);
        Changes changes = new Changes();
        identifyChangesInsert(rule, changes);
        update2(changes);
    }
    public void removeRuleAndUpdate(Rule rule){
        int index = rules.indexOf(rule);
        if(index == -1)
            return;
        rule = rules.get(index);
        Changes changes = new Changes();
        identifyChangesRemove(rule, changes);
        update2(changes);
    }
    public void identifyChangesRemove(Rule rule, Changes changes) {
//        Trie targetNode = (TrieRules)this.deviceToRules.get(rule.getDevice());
        Node targetNode = rule.getNode();
        ArrayList<Rule> sorted = targetNode.getAllUntil(rule);
        Comparator<Rule> comp = (lhs, rhs) -> rhs.getPriority() - lhs.getPriority();
        sorted.sort(comp);
        Iterator var5 = sorted.iterator();

        while(var5.hasNext()) {
            Rule r = (Rule)var5.next();
            if (r.getPriority() < rule.getPriority()) {
                PacketSet intersection = r.getMatch().and(rule.getHit());
                PacketSet newHit = r.getHit().or(intersection);
                r.getHit().release();
                r.setHit(newHit);
                newHit = rule.getHit().diff(intersection);
                rule.getHit().release();
                rule.setHit(newHit);
                if (!intersection.isEmpty() && r.getBehavior() != rule.getBehavior()) {
                    changes.add(intersection, rule.getBehavior(), r.getBehavior());
                } else {
                    intersection.release();
                }
            }

            if (rule.getHit().isEmpty()) {
                break;
            }
        }

        targetNode.removeRule(rule);
        this.rules.remove(rule);
        rule.getMatch().release();
        rule.getHit().release();
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

    private boolean hasLoop2(VNode u, Collection<Node> visited){
        if(!visited.contains(u.u)) return false;
        return !u.previous.u.u.equals(u.u);
//        List<Node> seq = new LinkedList<>();
//        VNode c = u;
//
//        while( c != null){
//            seq.add(c.source);
//            if(c.previous != null) {
//                c = c.previous.u;
//            }else{
//                break;
//            }
//        }
//        boolean flag = true;
//        for(int i=seq.size()-1;i>=0;i--){
//            Node n = seq.get(i);
//            if(flag&&n.equals(u.source)) continue;
//            flag = false;
//            if(n.equals(u.source)) return false;
//        }
//        return true;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VNode vNode = (VNode) o;
            return Objects.equals(u, vNode.u) && Objects.equals(pec, vNode.pec) && Objects.equals(headerStack, vNode.headerStack);
        }

        @Override
        public int hashCode() {
            return Objects.hash(u, pec, headerStack);
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
