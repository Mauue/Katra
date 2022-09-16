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

public class IncrementalTest {
    public static IncrementalTest instance = new IncrementalTest();
    static List<Long> timeList = new LinkedList<>();
    static List<Long> s1TimeList = new LinkedList<>();
    String network;
    boolean readTunnel=true;
    int tunnelNumber = 50;

    public IncrementalTest setNetwork(String network){
        this.network = network;
        return this;
    }

    public IncrementalTest setTunnelNumber(int number){
        this.tunnelNumber = number;
        return this;
    }

    public IncrementalTest isReadTunnelFile(boolean is){
        this.readTunnel = is;
        return this;
    }

    public void test(){
        HeaderType headerType = getHeadetType();
        Loader loader = new Loader();
        loader.nv.headerType = headerType;
        loader.setTopologyByFile(network+"/" +network + ".topology");
        loader.readSpaceFile(network+"/" +network +".space");
        loader.readFibDict(network+ "/ruleExp/");
        loader.readIncrementalSequence(network+"/ruleExp/"+network+"-sequence.txt");
        if (readTunnel)
            loader.readTunnelFile(network+"/"+network + "." +tunnelNumber +".tunnel");
        start(loader.nv);
    }


    static HeaderType getHeadetType(){
        HeaderType headerType = new HeaderType();
        Map<String, Integer> headerSettings = new HashMap<>();
        headerSettings.put("dstip", 32);
        headerType.update(headerSettings);
        return headerType;
    }
    static void start(NetworkVerifier nv){
        PacketSet.networkVerifier = nv;
        nv.calInitPEC();
        nv.nodes.values().forEach(Node::updateSpacePEC);
        List<Check> checks = new LinkedList<>();
        for(Node src: nv.nodes.values()) {
            for(Node dst: nv.nodes.values()) {
                if(src.equals(dst)) continue;
                for(PacketSet pec: src.getSpacePEC()) {
                    checks.clear();
                    checks.add(new ReachabilityCheck(nv.allHeaders(), dst, nv));
                    List<Trace> trace = nv.checkProperty(pec, Collections.singletonList(src), checks);
                }
            }
        }
    }


}
