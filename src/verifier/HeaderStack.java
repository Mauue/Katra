package verifier;

import verifier.util.LocatedPacket;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class HeaderStack {
    int i; // steps
    List<LocatedPacket> sequences;
    LocatedPacket initialPacket;
    NetworkVerifier nv;
    public HeaderStack(NetworkVerifier nv, Node n, Collection<PEC> pecs){
        sequences = new LinkedList<>();
        initialPacket = new LocatedPacket(n, pecs);
        this.nv = nv;
    }

    public HeaderStack(NetworkVerifier nv, LocatedPacket initialPacket){
        sequences = new LinkedList<>();
        this.initialPacket = initialPacket;
        this.nv = nv;
    }

    public HeaderStack(NetworkVerifier nv, Node n, PEC pec){
        this(nv, new LocatedPacket(n, pec));
    }

    public LocatedPacket top(){
        return sequences.get(sequences.size()-1);
    }

    public HeaderStack bot(){
        HeaderStack s = new HeaderStack(nv, initialPacket);
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

    public HeaderStack link(LocatedPacket lp){
        sequences.add(lp);
        i++;
        return this;
    }
}
