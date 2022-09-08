package test;

import verifier.Edge;
import verifier.NetworkVerifier;
import verifier.Node;
import verifier.Rule;
import verifier.util.IPPrefix;
import verifier.util.PacketSet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Loader {
    public NetworkVerifier nv;
    public Loader(){
        nv = new NetworkVerifier();
    }

    public void setTopologyByFile(String filepath){
        File file;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            file = new File(filepath);
            isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] token = line.split(" ");
                addTopology(token[0], token[1], token[2], token[3]);
            }
            isr.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void readFibDict(String dirname){
        for(Map.Entry<String, Node> entry: nv.nodes.entrySet()){
            Node node = entry.getValue();
            String name = entry.getKey();
            readFibFile(node, dirname+name);
        }
    }
    public void readSpaceFile(String filename) {
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
//        nv.nodes.values().forEach(n->System.out.println(n.getSpace()));
    }
    public void readFibFile(Node node, String filename) {
        List<Rule> rules = new LinkedList<>();
        List<Vector<Long>> pairs = new LinkedList<>();
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
                    Edge edge = node.getEdge(forward);
                    Rule r;
                    PacketSet p = nv.createPrefix("dstip", ip, prefix);
                    if(edge == null) {
                        r = new Rule(32-prefix, node.getSelfEdge(), p, nv.getTDelv());
                    }else {
                        r = new Rule(32 - prefix, edge, p, nv.getTID());
                    }
                    r.setPrefixRule(ip, prefix);
                    rules.add(r);
                }
                if (token[0].equals("nat")) {
                    long match = Long.parseLong(token[1]);
                    int matchLength = Integer.parseInt(token[2]);
                    long target = Long.parseLong(token[3]);
                    int targetLength = Integer.parseInt(token[4]);
                    Vector<Long> l = new Vector<>(4);
                    l.add(match); l.add((long) matchLength); l.add(target); l.add((long) targetLength);
                    pairs.add(l);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        nv.addRules(rules);
    }

    private void addTopology(String d1, String p1, String d2, String p2) {
        List<Node> ns = nv.getOrAddNodes(d1, d2);
        Node node1 = ns.get(0);
        Node node2 = ns.get(1);
        nv.getOrAddBiEdge(node1, p1, node2, p2);
    }
}
