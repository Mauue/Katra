package test;

import verifier.HeaderType;
import verifier.NetworkVerifier;
import verifier.Node;
import verifier.Trace;
import verifier.check.Check;
import verifier.check.ReachabilityCheck;
import verifier.util.PacketSet;

import java.util.*;

public class GreenStartTest {
    public static void main(String[] args) {
        Map<String, Integer> headerSettings = new HashMap<>();
        headerSettings.put("dstip", 32);
//        headerSettings.put("ttl", 8);

        HeaderType.update(headerSettings);
        Loader loader = new Loader();
        NetworkVerifier nv = loader.nv;
        long s0 = System.nanoTime();
        loader.setTopologyByFile("i2/i2.topology");
        loader.readSpaceFile("i2/i2.space");
        loader.readFibDict("i2/rule/");
        long s1 = System.nanoTime();
        nv.calInitPEC();
        long s2 = System.nanoTime();
        nv.nodes.values().forEach(Node::updateSpacePEC);
        List<Check> checks = new LinkedList<>();
        long s3 = System.nanoTime();
        for(Node src: nv.nodes.values()) {
            for(Node dst: nv.nodes.values()) {
                if(src.equals(dst)) continue;
                for(PacketSet pec: dst.getSpacePEC()) {
                    checks.clear();
                    checks.add(new ReachabilityCheck(nv.allHeaders(), dst, nv));
                    Trace trace = nv.checkProperty(pec, Collections.singletonList(src), checks);
//                    System.out.println(src + " --> " + dst + " trace:");
//                    if(trace != null)
//                        trace.print();
//                    else
//                        System.out.println("null");
                }
            }
        }
        long s4 = System.nanoTime();
        System.out.println("read file time: " + (s1-s0)/1000000.0 + " ms");
        System.out.println("cal EC time: " + (s2-s1)/1000000.0 + " ms");
        System.out.println("verification time: " + (s4-s3)/1000000.0 + " ms");
        System.out.println(nv.getPecs().size());
        System.out.println(nv.getPecs());
    }
}
