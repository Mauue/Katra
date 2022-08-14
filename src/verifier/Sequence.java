package verifier;

import verifier.widget.LocatedPacket;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Sequence {
    int i; // steps
    List<LocatedPacket> sequences;
    LocatedPacket initialPacket;
    NetworkVerifier nv;
    public Sequence(NetworkVerifier nv, Node n, Collection<PEC> pecs){
        sequences = new LinkedList<>();
        initialPacket = new LocatedPacket(n, pecs);
        this.nv = nv;
    }

    public Sequence(NetworkVerifier nv, LocatedPacket initialPacket){
        sequences = new LinkedList<>();
        this.initialPacket = initialPacket;
        this.nv = nv;
    }

    public Sequence(NetworkVerifier nv, Node n, PEC pec){
        this(nv, new LocatedPacket(n, pec));
    }

    public LocatedPacket top(){
        return sequences.get(sequences.size()-1);
    }

    public Sequence bot(){
        Sequence s = new Sequence(nv, initialPacket);
        s.sequences = new LinkedList<>(sequences.subList(0, sequences.size()-1));
        s.i = i-1;
        return s;
    }

    public int getLen(){
        return i;
    }

    public void repair(){
        //todo
    }
    public void push(LocatedPacket lp){
        sequences.add(lp);
        i++;
    }

    public Sequence link(LocatedPacket lp){
        sequences.add(lp);
        i++;
        return this;
    }
}
