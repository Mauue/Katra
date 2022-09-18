package test;

import verifier.*;
import verifier.check.Check;
import verifier.check.ReachabilityCheck;
import verifier.util.PacketSet;
import verifier.util.Pair;

import java.util.*;

public class IncrementalTest {
    public static IncrementalTest instance = new IncrementalTest();
    static List<Long> timeList = new LinkedList<>();
    static List<Long> s1TimeList = new LinkedList<>();
    String network;
    boolean readTunnel=true;
    int tunnelNumber = 50;
    static Loader loader;
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
        IncrementalTest.loader = loader;
        loader.nv.headerType = headerType;
        loader.setTopologyByFile(network+"/" +network + ".topology");
        loader.readSpaceFile(network+"/" +network +".space");
        loader.readFibDict(network+ "/ruleExp/");
        loader.readIncrementalSequence(network+"/ruleExp/"+network+"-sequence.txt");
        loader.readLatency("latency/"+network+".csv");
        setCenter(loader.nv, network);
        if (readTunnel)
            loader.readTunnelFile(network+"/"+network + "." +tunnelNumber +".tunnel");
        start(loader.nv);
        incrementalTest(loader.nv);
        System.out.println(timeList);
    }


    static HeaderType getHeadetType(){
        HeaderType headerType = new HeaderType();
        Map<String, Integer> headerSettings = new HashMap<>();
        headerSettings.put("dstip", 32);
        headerType.update(headerSettings);
        return headerType;
    }

    static void setCenter(NetworkVerifier nv, String network){
        switch (network){
            case "i2":{
                loader.center = "newy32aoa"; break;
            }
            case "Oteglobe":{
                loader.center = "Los-Angeles"; break;
            }
            case "BtNorthAmerica": {
                loader.center = "New-York"; break;
            }
            default:{
                System.out.println("error network");
            }
        }
    }
    static void start(NetworkVerifier nv){
        PacketSet.networkVerifier = nv;
        nv.calInitPEC();
        nv.nodes.values().forEach(Node::updateSpacePEC);
        nv.checkAllReachability();
    }

    static void incrementalTest(NetworkVerifier nv){
        for(String[] element: nv.sequences){
            long s=0;
            Node node = nv.nodes.get(element[0]);
            long ip = Long.parseLong(element[2]);
            int prefix = Integer.parseInt(element[3]);
            Rule r = node.parseRule(ip, prefix, element[4]);
            switch (element[1]){
                case "insert": {
                    s = System.nanoTime();
                    nv.insertRuleAndUpdate(r);
                    break;
                }
                case "delete": {
                    s = System.nanoTime();
                    nv.removeRuleAndUpdate(r);
                    break;
                }
                case "modify": {
                    Rule r2 = node.parseRule(ip, prefix, element[5]);
                    s = System.nanoTime();
                    nv.removeRuleAndUpdate(r);
                    nv.insertRuleAndUpdate(r2);
                    break;
                }
                default:{
                    System.err.println("error sequence: " + Arrays.toString(element));
                }
            }
            nv.checkAllReachability();
            long e = System.nanoTime();
            long l = getCollectionTime(node.getName(), loader.center);
            timeList.add(e-s+l);
        }
    }
    static public long getCollectionTime(String from,String to){
        if(from.equals(to)) return 0;
        Long l = loader.latencyMap.get(new Pair<>(from, to));
        if(l == null) {
            System.err.println("no latency between:<"+from+","+to+">");
            return 0;
        }
        return l;
    }

}
