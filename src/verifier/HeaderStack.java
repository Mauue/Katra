package verifier;

import verifier.util.PacketSet;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HeaderStack {
    List<PacketSet> sequences;
    PacketSet initialPacket;
    NetworkVerifier nv;
    public HeaderStack(NetworkVerifier nv, List<PacketSet> packetSets){
        sequences = new LinkedList<>(packetSets);
        initialPacket = packetSets.get(0);
        this.nv = nv;
    }


    public HeaderStack(NetworkVerifier nv, PacketSet pec){
        this(nv, Collections.singletonList(pec));
    }

    public PacketSet top(){
        if(sequences.isEmpty()) return null;
        return sequences.get(sequences.size()-1);
    }

    public HeaderStack bot(){
        HeaderStack s = new HeaderStack(nv, sequences);
        s.pop();
        return s;
    }

    public int getLen(){
        return sequences.size();
    }

    public void repair(){
        //todo
    }
    public HeaderStack push(PacketSet ps){
        sequences.add(ps);
        return this;
    }

    public HeaderStack pop(){
        if(getLen() > 0)
            sequences.remove(getLen()-1);
        return this;
    }

}
