package test;

import verifier.HeaderType;
import verifier.NetworkVerifier;
import verifier.Node;
import verifier.Trace;
import verifier.check.Check;
import verifier.check.ReachabilityCheck;
import verifier.util.PacketSet;
import verifier.util.Utility;

import java.util.*;

public class GreenStartTest {
    static List<Long> timeList = new LinkedList<>();
    public static void main(String[] args) {
        String network = args[0];
        int times = Integer.parseInt(args[1]);
        for(int i=0;i<times;i++) {
            init();
            Loader loader = new Loader();
            loader.setTopologyByFile(network+"/" +network + ".topology");
            loader.readSpaceFile(network+"/" +network +".space");
            loader.readFibDict(network+ "/rule/");
            verification(loader.nv);
        }
        System.out.println("avg time:" + Utility.avg(timeList)/1000000.0 + "ms");
    }
    static void init(){
        HeaderType.init();
        Map<String, Integer> headerSettings = new HashMap<>();
        headerSettings.put("dstip", 32);
//        headerSettings.put("ttl", 8);
        HeaderType.update(headerSettings);
    }
    static void verification(NetworkVerifier nv){
        long s1 = System.nanoTime();
        nv.calInitPEC();
        System.out.println(nv.getPecs());
        nv.nodes.values().forEach(Node::updateSpacePEC);
        List<Check> checks = new LinkedList<>();
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
        long s2 = System.nanoTime();
        System.out.println(s2-s1);
        timeList.add(s2-s1);
    }
}

