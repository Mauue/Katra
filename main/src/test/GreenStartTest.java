package test;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.ArgumentParser;

import verifier.HeaderType;
import verifier.NetworkVerifier;
import verifier.Node;
import verifier.util.PacketSet;
import verifier.util.Utility;

import java.util.*;

public class GreenStartTest {

    public static GreenStartTest instance = new GreenStartTest();
    static List<Long> timeList = new LinkedList<>();
    static List<Long> s1TimeList = new LinkedList<>();
    int times = 0;
    String network;
    boolean readTunnel=true;
    int tunnelNumber = 50;
    public static void main(String[] args) {
        String network = args[0];
        int times = Integer.parseInt(args[1]);


    }
    public static void greenStartEntry(String[] args) {

        ArgumentParser parser = ArgumentParsers
                .newFor("Katra GreenStart").build()
                .defaultHelp(true)
                .description("Runner contains: GreenStart, Incremental");

        parser.addArgument("network").help("network name");

        parser.addArgument("-t", "--times").type(Integer.class).setDefault(1).help("the times of green start");
        parser.addArgument("--tunnel_num").type(Integer.class).setDefault(0).help("the number of IP tunnel");

        try {
            Namespace namespace = parser.parseArgs(args);

            GreenStartTest.instance.setNetwork(namespace.getString("network"))
                    .setTimes(namespace.getInt("times"))
                    .setTunnelNumber(namespace.getInt("tunnel_num"))
                    .greenStart();
        } catch (ArgumentParserException e) {
            parser.printHelp();
        }
    }
    public GreenStartTest setTimes(int t){
        times = t;
        return this;
    }

    public GreenStartTest setNetwork(String network){
        this.network = network;
        return this;
    }

    public GreenStartTest setTunnelNumber(int number){
        this.tunnelNumber = number;
        return this;
    }

    public GreenStartTest isReadTunnelFile(boolean is){
        this.readTunnel = is;
        return this;
    }
    public void greenStart(){
        for(int i=0;i<times;i++) {
            if(i%10==0)System.out.println(i);
            long t0 = System.nanoTime();
            HeaderType headerType = getHeaderType();
            long t1 = System.nanoTime();
            Loader loader = new Loader();
            loader.nv.headerType = headerType;
            loader.setTopologyByFile(network+"/" +network + ".topology");
            loader.readSpaceFile(network+"/" +network +".space");
            loader.readFibDict(network+ "/ruleExp/");
            if (readTunnel)
                loader.readTunnelFile(network+"/"+network + "." +tunnelNumber +".tunnel");
            long  t =verification(loader.nv);
            timeList.add(t+(t1-t0));
            System.gc();
            Node.cnt = 0;
            PacketSet.bvMap = new HashMap<>(10000);
        }
        if(times>1){
            s1TimeList.remove(0);
            timeList.remove(0);
        }
        System.out.println("build avg time:" + Utility.avg(s1TimeList)/1000000.0 + "ms");
        System.out.println("avg time:" + Utility.avg(timeList)/1000000.0 + "ms");
//        System.out.println(PacketSet.count +" " + PacketSet.bvcount + " " + (1-1.0*PacketSet.bvcount/PacketSet.count)*100 + " %");
    }


    static HeaderType getHeaderType(){
        HeaderType headerType = new HeaderType();
        Map<String, Integer> headerSettings = new HashMap<>();
        headerSettings.put("dstip", 32);
//        headerSettings.put("srcip", 32);
//        headerSettings.put("srcport", 16);
//        headerSettings.put("dstport", 16);
//        headerSettings.put("protocol", 8);
//        headerSettings.put("ttl", 8);
        headerType.update(headerSettings);
        return headerType;
    }
    static long verification(NetworkVerifier nv){
        PacketSet.networkVerifier = nv;
        long s1 = System.nanoTime();
        nv.calInitPEC();
        long m = System.nanoTime();
//        System.out.println(nv.getPecs());
        nv.nodes.values().forEach(Node::updateSpacePEC);
//        for(Node node: nv.nodes.values()){
//            System.out.println(node.getName() + ": " + node.getSpacePEC().size() + "/" + nv.getPecs().size());
//        }
        nv.checkAllReachability();
        long s2 = System.nanoTime();
        s1TimeList.add(m-s1);
        return s2-s1;

    }
}

