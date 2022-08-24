package verifier.util;

import jdd.bdd.BDD;
import verifier.HeaderType;
import verifier.NetworkVerifier;

import java.util.Objects;

public class PacketSet {
    int predicate;
    HeaderType ht;
    BDD bdd;
    BoundingVolume bv;

    public PacketSet(HeaderType ht, int predicate){
        this.predicate = predicate;
        this.ht = ht;
        bdd = ht.bdd;
        bv = ht.getBoundingVolume(predicate);
    }

    public PacketSet(PacketSet ps){
        this.predicate = ps.predicate;
        this.ht = ps.ht;
        bdd = ht.bdd;
    }

    public void updateBoundingVolume(){
        bv = ht.getBoundingVolume(predicate);
        System.out.println(ht.printBV(bv));
    }

    public PacketSet and(PacketSet p2){
        return new PacketSet(ht, bdd.ref(bdd.and(this.predicate, p2.predicate)));
    }

    public PacketSet or(PacketSet p2){
        return new PacketSet(ht, bdd.ref(bdd.or(this.predicate, p2.predicate)));
    }
    public PacketSet xor(PacketSet p2){
        return new PacketSet(ht, bdd.ref(bdd.xor(this.predicate, p2.predicate)));
    }

    public PacketSet not(){
        return new PacketSet(ht, bdd.ref(bdd.not(this.predicate)));
    }

    public boolean isEmpty(){
        return predicate==0;
    }
    public boolean hasOverlap(PacketSet ps){
        //todo
        return !this.and(ps).isEmpty();

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

    @Override
    public String toString() {
        if(bv == null) bv = ht.getBoundingVolume(predicate);
        return ht.printBV(bv);
    }
}
