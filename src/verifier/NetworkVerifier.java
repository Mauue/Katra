package verifier;

import verifier.check.Check;
import verifier.transformation.*;
import verifier.transformation.Set;
import verifier.widget.HeaderSet;
import verifier.widget.IPPrefix;
import verifier.widget.Range;

import java.util.*;

public class NetworkVerifier {
    HeaderType headerType;
    Map<String, Node> nodes;
    List<Check> checks;
    List<Rule> rules;
    public NetworkVerifier(HeaderType ht){
        headerType = ht;
        headerType.setNv(this);
        checks = new ArrayList<>();
        nodes = new HashMap<>();
        rules = new LinkedList<>();
    }

    public HeaderSet createRange(Map<String, Range> r){
        return headerType.createRange(r);
    }
    public HeaderSet createPrefix(Map<String, IPPrefix> p){
        return headerType.createPrefix(p);
    }

    public List<Node> GetOrAddNodes(String... nodes){
        List<Node> result = new ArrayList<>(nodes.length);
        for(String name: nodes){
            if(this.nodes.containsKey(name)){
                result.add(this.nodes.get(name));
            }else{
                Node n = new Node(name, this);
                result.add(n);
                this.nodes.put(name, n);
            }
        }
        return result;
    }

    public List<Edge> GetOrAddBiEdge(Node n1, Node n2){
        Edge edge12 = new Edge(n1, n2);
        n1.addEdgeOut(edge12); n2.addEdgeIn(edge12);

        Edge edge21 = new Edge(n2, n1);
        n2.addEdgeOut(edge21); n1.addEdgeIn(edge21);

        return Arrays.asList(edge12, edge21);
    }

    public HeaderSet allHeaders(){
        return new HeaderSet(headerType, 1);
    }

    public void addCheck(Check check){
        this.checks.add(check);
    }

    public Transformation pop(){
        return new Pop(this);
    }

    public Transformation push(){
        return new Push(this);
    }

    public Transformation set(String name, int value){
        return new Set(this, name, value);
    }

    public Transformation seq(Transformation... transformations){
        return new Seq(this, transformations);
    }

    public Violation addRule(Rule rule){
        Violation v = new Violation(rule);
        rules.add(rule);
        return v;
    }
}
