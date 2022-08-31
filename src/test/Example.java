package test;

import verifier.*;
import verifier.check.LoopCheck;
import verifier.transformation.Transformation;
import verifier.util.IPPrefix;
import verifier.util.PacketSet;
import verifier.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Implement example API in Figure 6
public class Example {
    public static void main(String[] args) {
        Map<String, Integer> headerSettings = new HashMap<>();
        headerSettings.put("dstip", 32); headerSettings.put("srcip", 32);

        // instantiate a new network verifier
         HeaderType.update(headerSettings);
        NetworkVerifier nv = new NetworkVerifier();

        // build the network topology
        List<Node> nodes = nv.getOrAddNodes("n1", "n2");
        Node n1 = nodes.get(0);
        Node n2 = nodes.get(1);
        Pair<Edge, Edge> edges = nv.getOrAddBiEdge(n1, n2);
        Edge e12 = edges.getFirst();
        Edge e21 = edges.getSecond();

        // register the properties we want to monitor
        nv.addCheck(new LoopCheck(nv.allHeaders()));

        // create new prioritized forwarding rules
        // TODO: range is not implementation, so use prefix
//        Map<String, Range> rangeMap = new HashMap<>();
//        rangeMap.put("srcip", new Range(0, Integer.MAX_VALUE));
//        rangeMap.put("dstip", new Range(10, 20));
//        int r = nv.createRange(rangeMap);

        Map<String, IPPrefix> ipPrefixMap = new HashMap<>();
        ipPrefixMap.put("dstip", new IPPrefix(10<<24, 8));
        PacketSet r = nv.createPrefix(ipPrefixMap);

//        Transformation t = nv.getTSeq(nv.getTPush(), nv.getTSet("dstip", 10));
//        Rule r1 = new Rule(100, e12, r, t);
//        Rule r2 = new Rule(100, e21, r, nv.getTPop());

        // find violations from adding rules.
//        Violation violation1 = nv.addRule(r1);
//        Violation violation2 = nv.addRule(r2);
//        assert violation2.getCount() == 1;
    }
}
