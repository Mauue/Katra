package test;

import verifier.*;
import verifier.check.Check;
import verifier.check.ReachabilityCheck;
import verifier.transformation.Transformation;
import verifier.util.PacketSet;
import verifier.util.Pair;

import java.util.*;

// Test Partial equivalence classes in Figure3
public class Example2 {
    public static void main(String[] args) {
        Map<String, Integer> headerSettings = new HashMap<>();
        headerSettings.put("dstip", 32);
//        headerSettings.put("ttl", 8);
        HeaderType headerType = new HeaderType();
        headerType.update(headerSettings);
        NetworkVerifier nv = new NetworkVerifier();

        // build the network topology
        List<Node> nodes = nv.getOrAddNodes("v1", "v2", "v3");
        Node v1 = nodes.get(0);
        Node v2 = nodes.get(1);
        Node v3 = nodes.get(2);
        Pair<Edge, Edge> edges = nv.getOrAddBiEdge(v1, v2);
        Edge e12 = edges.getFirst();
        Edge e21 = edges.getSecond();

        edges = nv.getOrAddBiEdge(v2, v3);
        Edge e23 = edges.getFirst();
        Edge e32 = edges.getSecond();

        edges = nv.getOrAddBiEdge(v1, v1);
        Edge e11 = edges.getFirst();

        edges = nv.getOrAddBiEdge(v2, v2);
        Edge e22= edges.getFirst();

        edges = nv.getOrAddBiEdge(v3, v3);
        Edge e33 = edges.getFirst();

//        nv.addCheck(new LoopCheck(nv.allHeaders()));


        PacketSet dstM = nv.createPrefix("dstip", 0x0000000fL, 30);
        PacketSet tunnel = nv.createPrefix("dstip", 0x000000ffL, 32);

        PacketSet all = nv.allHeaders();

        Transformation drop = nv.getTDrop();
        Transformation _push = nv.getTPush();
        Transformation set = nv.getTSet(tunnel);
        Transformation pop = nv.getTPop();
        Transformation delv = nv.getTDelv();

        Transformation push = nv.getTSeq(_push, set);

        Rule r1 = new Rule(100, e12, dstM, push);
        r1.setPrefixRule(0x0000000fL, 30);
        Rule r2 = new Rule(300, e11, all, drop);
        r2.setPrefixRule(0, 0);

        Rule r3 = new Rule(100, e23, tunnel, nv.getTID());
        r3.setPrefixRule(0x000000ffL, 32);
        Rule r4 = new Rule(300, e22, all, drop);
        r4.setPrefixRule(0, 0);


        Rule r5 = new Rule(100, e33, tunnel, pop);
        r5.setPrefixRule(0x000000ffL, 32);
        Rule r6 = new Rule(200, e33, dstM, delv);
        r6.setPrefixRule(0x0000000fL, 30);
        Rule r7 = new Rule(300, e33, all, drop);
        r7.setPrefixRule(0, 0);

        nv.addRules(r1, r2, r3, r4, r5, r6, r7);
//
        nv.calInitPEC();

        List<Check> checks = new LinkedList<>();
        checks.add(new ReachabilityCheck(all, v3, nv));

        for(PacketSet p: nv.getPecs()) {
            System.out.println("check pec:" +p);
            List<Trace> trace = nv.checkProperty(p, Collections.singletonList(v1), checks);
            if (trace != null) trace.forEach(Trace::print);
        }

    }
}
