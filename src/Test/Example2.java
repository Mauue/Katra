package Test;

import verifier.*;
import verifier.check.LoopCheck;
import verifier.transformation.Transformation;
import verifier.util.IPPrefix;
import verifier.util.PacketSet;
import verifier.util.Utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Test Partial equivalence classes in Figure3
public class Example2 {
    public static void main(String[] args) {
        Map<String, Integer> headerSettings = new HashMap<>();
        headerSettings.put("dstip", 32); headerSettings.put("ttl", 8);

        HeaderType headerType = new HeaderType(headerSettings);
        NetworkVerifier nv = new NetworkVerifier(headerType);

        // build the network topology
        List<Node> nodes = nv.getOrAddNodes("v1", "v2", "v3");
        Node v1 = nodes.get(0);
        Node v2 = nodes.get(1);
        Node v3 = nodes.get(2);
        List<Edge> edges = nv.getOrAddBiEdge(v1, v2);
        Edge e12 = edges.get(0);
        Edge e21 = edges.get(1);

        edges = nv.getOrAddBiEdge(v2, v3);
        Edge e23 = edges.get(0);
        Edge e32 = edges.get(1);

        edges = nv.getOrAddBiEdge(v1, v1);
        Edge e11 = edges.get(0);

        edges = nv.getOrAddBiEdge(v2, v2);
        Edge e22 = edges.get(0);

        edges = nv.getOrAddBiEdge(v3, v3);
        Edge e33 = edges.get(0);

        nv.addCheck(new LoopCheck(nv.allHeaders()));

        Map<String, IPPrefix> ipPrefixMap = new HashMap<>();
        ipPrefixMap.put("dstip", new IPPrefix(Utility.ip2Int("10.7.1.0"), 24));

        PacketSet dstM = nv.createPrefix(ipPrefixMap);

        Map<String, Integer> singleMap = new HashMap<>();
        singleMap.put("ttl", 0);

        PacketSet ttlM = nv.createSingle(singleMap);

        PacketSet all = nv.allHeaders();

        Transformation drop = nv.getTDrop();
        Transformation _push = nv.getTPush();
        Transformation ttl = nv.getTTtl();
        Transformation _pop = nv.getTPop();
        Transformation delv = nv.getTDelv();

        Transformation push = nv.getTSeq(_push, ttl);
        Transformation pop = nv.getTSeq(_pop, ttl);

        Rule r0 = new Rule(100, e11, ttlM, drop);
        Rule r1 = new Rule(200, e12, dstM, push);
        Rule r2 = new Rule(300, e12, all, drop);

        Rule r3 = new Rule(100, e22, ttlM, drop);
        Rule r4 = new Rule(200, e23, dstM, pop);
        Rule r5 = new Rule(300, e22, all, drop);

        Rule r6 = new Rule(100, e33, ttlM, drop);
        Rule r7 = new Rule(200, e33, all, delv);

        nv.addRules(r0, r1, r2, r3, r4, r5, r6, r7);

        nv.calInitPEC();

    }
}
