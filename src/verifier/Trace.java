package verifier;

import verifier.util.PacketSet;
import verifier.util.Pair;

import java.util.LinkedList;
import java.util.List;


public class Trace {
    List<Pair<Node, PacketSet>> seq;
    public Trace(){
        seq = new LinkedList<>();
    }

    public void add(Node n, PacketSet p){
        seq.add(new Pair<>(n, p));
    }

    public void print(){
        for(Pair<Node, PacketSet> pair: seq){
            System.out.println(pair.getFirst() + " " + pair.getSecond());
        }
    }
}
