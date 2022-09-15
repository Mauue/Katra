package test;

import verifier.Edge;
import verifier.NetworkVerifier;
import verifier.Node;
import verifier.Rule;
import verifier.transformation.Transformation;
import verifier.util.IPPrefix;
import verifier.util.PacketSet;
import verifier.util.URule;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Loader {
    public NetworkVerifier nv;
    public Loader(){
        nv = new NetworkVerifier();
    }

    public long setTopologyByFile(String filepath){
        File file;
        InputStreamReader isr = null;
        BufferedReader br = null;
        long res = 0;
        try {
            file = new File(filepath);
            isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] token = line.split(" ");
                long s1 = System.nanoTime();
                addTopology(token[0], token[1], token[2], token[3]);
                long s2 = System.nanoTime();
                res += s2-s1;
            }
            isr.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public long readFibDict(String dirname){
        long res = 0;
        for(Map.Entry<String, Node> entry: nv.nodes.entrySet()){
            Node node = entry.getValue();
            String name = entry.getKey();
            res += readFibFile(node, dirname+name);
        }
        return res;
    }
    public long readSpaceFile(String filename) {
        int res = 0;
        nv.nodes.values().forEach(Node::clearSpace);
        try {
            File file = new File(filename);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] token = line.split("\\s+");
                Node node = nv.nodes.get(token[0]);

                node.addSpace(new IPPrefix(Long.parseLong(token[1]), Integer.parseInt(token[2])));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
//        nv.nodes.values().forEach(n->System.out.println(n.getSpace()));
    }
    public long readFibFile(Node node, String filename) {
        List<URule> rules = new LinkedList<>();
        long res = 0;
        try {
            File file = new File(filename);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] token = line.split("\\s+");
                if (token[0].equals("fw")) {
                    String forward = "drop"; // 去掉端口名中“.”后的字符
                    if(token.length > 3){
                        forward = token[3].split("\\.", 2)[0];
                    }

                    long ip = Long.parseLong(token[1]);
                    int prefix = Integer.parseInt(token[2]);
                    long s1 = System.nanoTime();
                    Edge edge = node.getEdge(forward);
                    URule r;
//                    PacketSet p = nv.createPrefix("dstip", ip, prefix);
                    if(edge == null) {
                        r = new URule(32-prefix, node.getSelfEdge(), ip, prefix, nv.getTDelv());
                    }else {
                        r = new URule(32 - prefix, edge, ip, prefix, nv.getTID());
                    }
                    rules.add(r);
                    long s2 = System.nanoTime();
                    res += s2-s1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        nv.addURules(rules);
        return res;
    }

    public long readTunnelFile(String filename) {
        List<URule> rules = new LinkedList<>();
        long res = 0;
        try {
            File file = new File(filename);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] token = line.split("\\s+");
                Node src = nv.nodes.get(token[0]);
                Node dst = nv.nodes.get(token[1]);

                int prefix = Integer.parseInt(token[4]);
                IPPrefix matchIP = new IPPrefix(Long.parseLong(token[2]), prefix);
                IPPrefix targetIP = new IPPrefix(Long.parseLong(token[3]), prefix);
                long s1 = System.nanoTime();
                Transformation push = nv.getTSeq(nv.getTPush(), nv.getTSet(targetIP));
                rules.add(new URule(2, src.getSelfEdge(), matchIP, push));
                rules.add(new URule(1, dst.getSelfEdge(), targetIP, nv.getTPop()));
                long s2 = System.nanoTime();
                res += s2-s1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        nv.addURules(rules);
        return res;
    }
    private void addTopology(String d1, String p1, String d2, String p2) {
        List<Node> ns = nv.getOrAddNodes(d1, d2);
        Node node1 = ns.get(0);
        Node node2 = ns.get(1);
        nv.getOrAddBiEdge(node1, p1, node2, p2);
    }
}
