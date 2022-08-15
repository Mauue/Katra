package verifier.util;

import verifier.HeaderType;
import verifier.NetworkVerifier;

import java.util.Objects;

public class PacketSet {
    int predicate;
    HeaderType ht;
    BoundingVolume bv;

    public PacketSet(HeaderType ht, int predicate){
        this.predicate = predicate;
    }

    public void updateBoundingVolume(){
        // todo
    }

    public PacketSet and(PacketSet p2){
        // todo
        return null;
    }

    public PacketSet or(PacketSet p2){
        // todo
        return null;
    }

    public PacketSet not(){
        // todo
        return null;
    }

    public boolean isEmpty(){
        return predicate==0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PacketSet packetSet = (PacketSet) o;
        return predicate == packetSet.predicate && Objects.equals(ht, packetSet.ht);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate, ht);
    }
}
