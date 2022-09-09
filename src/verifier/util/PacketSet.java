package verifier.util;

import jdd.bdd.BDD;
import verifier.HeaderType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PacketSet {
    int predicate;
    static BDD bdd = HeaderType.headerType.bdd;
    static Map<Integer, BoundingVolume> bvMap = new HashMap<>(100000);


    public PacketSet(int predicate){
        this.predicate = predicate;
    }

    public PacketSet(PacketSet ps){
        this.predicate = ps.predicate;
    }

    public static BoundingVolume getBoundingVolume(int predicate){
        if(!bvMap.containsKey(predicate)) {
            BoundingVolume bv = HeaderType.getBoundingVolume(predicate);
            bvMap.put(predicate, bv);
        }
        return bvMap.get(predicate);
//        System.out.println(ht.printBV(bv));
    }

    public PacketSet and(PacketSet p2){
        return new PacketSet(bdd.ref(bdd.and(this.predicate, p2.predicate)));
    }

    public PacketSet or(PacketSet p2){
        return new PacketSet(bdd.ref(bdd.or(this.predicate, p2.predicate)));
    }
    public PacketSet xor(PacketSet p2){
        return new PacketSet(bdd.ref(bdd.xor(this.predicate, p2.predicate)));
    }

    public PacketSet not(){
        return new PacketSet(bdd.ref(bdd.not(this.predicate)));
    }

//    public BoundingVolume getBv() {
//        return bv;
//    }

    public boolean isEmpty(){
        return predicate==0;
    }
    public boolean hasOverlap(PacketSet ps){
        if(this.predicate==0 || ps.predicate == 0) return false;
        if(this.predicate==1 || ps.predicate == 1) return true;
        if(this.predicate == ps.predicate) return true;
        BoundingVolume bv1 = getBoundingVolume(ps.predicate);
        BoundingVolume bv2 = getBoundingVolume(this.predicate);
        if(bv1.isIntersection(bv2)){
            return !this.and(ps).isEmpty();
        }
        return false;
    }

    public int getPredicate() {
        return predicate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PacketSet packetSet = (PacketSet) o;
        return predicate == packetSet.predicate;
    }

    public void release(){
        bdd.deref(predicate);
    }
    @Override
    public int hashCode() {
        return Objects.hash(predicate);
    }

    @Override
    public String toString() {
        if(predicate == 0) return "0[null]";
        if(predicate == 1) return "1[all]";
        return predicate + HeaderType.printBV(getBoundingVolume(predicate));
    }
}
