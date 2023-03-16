package verifier.util;

import jdd.bdd.BDD;
import verifier.HeaderType;
import verifier.NetworkVerifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PacketSet {
    int predicate;
    public static BDD bdd;
    public static Map<Integer, BoundingVolume> bvMap = new HashMap<>(100000);
    public static int count =0;
    public static int bvcount = 0;
    public static NetworkVerifier networkVerifier;

    public PacketSet(int predicate){
        this.predicate = predicate;
    }

    public PacketSet(PacketSet ps){
        this.predicate = ps.predicate;
    }

    public static BoundingVolume getBoundingVolume(int predicate){
        if(!bvMap.containsKey(predicate)) {
            BoundingVolume bv = networkVerifier.headerType.getBoundingVolume(predicate);
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

    public PacketSet diff(PacketSet p2){
        int tmp = bdd.ref(bdd.not(p2.predicate));
        int ret = bdd.ref(bdd.and(predicate, tmp));
        bdd.deref(tmp);
        return new PacketSet(ret);
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
//        count++;
//        return  bdd.and(this.predicate, ps.predicate)!=0;
        BoundingVolume bv1 = getBoundingVolume(ps.predicate);
        BoundingVolume bv2 = getBoundingVolume(this.predicate);

        if(bv1.isIntersection(bv2)){
//            bvcount++;
            return bdd.hasIntersection(this.predicate, ps.getPredicate());
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
    public void increase(){
        bdd.ref(predicate);
    }
    @Override
    public int hashCode() {
        return Objects.hash(predicate);
    }

    @Override
    public String toString() {
        if(predicate == 0) return "0[null]";
        if(predicate == 1) return "1[all]";
        return String.valueOf(predicate);
//                + HeaderType.printBV(getBoundingVolume(predicate))
//                ;
    }
}
