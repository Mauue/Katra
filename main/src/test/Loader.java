package test;

import verifier.NetworkVerifier;
import verifier.Node;
import verifier.Rule;
import verifier.transformation.Transformation;
import verifier.util.IPPrefix;
import verifier.util.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Loader {
    final static String dir="config/";
    public NetworkVerifier nv;
    public Map<Pair<String, String>, Long> latencyMap;

    public String center;
    public Loader(){
        nv = new NetworkVerifier();
        latencyMap = new HashMap<>();
    }

    public long setTopologyByFile(String filepath){
        File file;
        InputStreamReader isr = null;
        BufferedReader br = null;
        long res = 0;
        try {
            file = new File(dir+filepath);
            isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] token = line.split("\\s+");
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
            res += readFibFile(node, dir+dirname+name);
        }
        return res;
    }
    public long readSpaceFile(String filename) {
        int res = 0;
        nv.nodes.values().forEach(Node::clearSpace);
        try {
            File file = new File(dir+filename);
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

    public void readIncrementalSequence(String filename){
        try {  
            File file = new File(dir+filename);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] token = line.split("\\s+");
                if(token.length >4)
                    nv.sequences.add(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public long readFibFile(Node node, String filename) {
        List<Rule> rules = new LinkedList<>();
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
                    forward = token[3];
                    long ip = Long.parseLong(token[1]);
                    int prefix = Integer.parseInt(token[2]);
                    long s1 = System.nanoTime();
                    Rule r = node.parseRule(ip, prefix, forward);
                    rules.add(r);
                    long s2 = System.nanoTime();
                    res += s2-s1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        nv.addRules(rules);
        return res;
    }
    public void readLatency(String filename){
        String FIRST_LINE = "Source,City,Distance,Average(ms),%of SOL f/0,min,max,mdev,Last Checked";
        try {
            File file = new File(dir+filename);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line;
            line = br.readLine();
            if(!line.equals(FIRST_LINE)) {
                Logger.getGlobal().warning("not formal file");
                return;
            }
            latencyMap = new HashMap<>();
            while ((line = br.readLine()) != null) {
                String[] token = line.split(",");
                long latency = (long) Double.parseDouble(token[3])*1000000;
                latencyMap.put(new Pair<>(token[0], token[1]), latency);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public long readTunnelFile(String filename) {
        List<Rule> rules = new LinkedList<>();
        long res = 0;
        try {
            File file = new File(dir+filename);
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
                rules.add(new Rule(2, src.getSelfEdge(), matchIP, push));
                rules.add(new Rule(1, dst.getSelfEdge(), targetIP, nv.getTPop()));
                long s2 = System.nanoTime();
                res += s2-s1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        nv.addRules(rules);
        return res;
    }
    private void addTopology(String d1, String p1, String d2, String p2) {
        List<Node> ns = nv.getOrAddNodes(d1, d2);
        Node node1 = ns.get(0);
        Node node2 = ns.get(1);
        nv.getOrAddBiEdge(node1, p1, node2, p2);
    }
}
