package verifier;

import verifier.util.*;
import jdd.bdd.BDD;

import java.util.*;

public class HeaderType {
    public BDD bdd;

    public NetworkVerifier nv = null;
    LinkedHashMap<String, Integer> elements;
    Map<String, Integer> elementVarIndex;
    Map<String, int[]> elementsVar;
    Map<String, Integer> elementIndex;
    Map<String, Integer> forallElement; // jdd-forall

    static PacketSet all = null;
    static PacketSet zero = null;
    public static HeaderType headerType = null;

    int length;
    int size;
    HeaderType(){
        bdd = new BDD(10000, 10000);
        elementVarIndex = new HashMap<>();
        elementsVar = new HashMap<>();
        forallElement = new HashMap<>();
        elementIndex = new HashMap<>();
        length = 0;

    }

    public static void init(){
        headerType = new HeaderType();
    }

//    public HeaderType(Map<String, Integer> e){
//        this();
//        update(e);
//    }
    public static void update(Map<String, Integer> e){
        headerType.elements = new LinkedHashMap<>(e);

        headerType.size = e.size();
        headerType.elements.forEach((s, l)-> {
            headerType.elementVarIndex.put(s, headerType.length);
            headerType.length += l;
            int[] array = new int[headerType.length];
            headerType.declareVars(array, headerType.length);
            headerType.elementsVar.put(s, array);
            headerType.elementIndex.put(s, headerType.elementIndex.size());
        });
        int last = 1;
        for (Map.Entry<String, Integer> entry : headerType.elements.entrySet()) {
            String s = entry.getKey();
            headerType.forallElement.put(s, last);
            int[] array = headerType.elementsVar.get(s);
            for (Integer i : array) {
                last = headerType.bdd.andTo(last, i);
            }
            headerType.bdd.ref(last);
        }

        all = new PacketSet(1);
        zero = new PacketSet(0);
    }

    public static PacketSet createSingle(Map<String, Integer> s){
        return headerType._createSingle(s);
    }

    PacketSet _createSingle(Map<String, Integer> s){
        int result = 1;
        for(Map.Entry<String, Integer> entry: s.entrySet()) {
            String name = entry.getKey();
            if(elements.containsKey(name)){
                int[] varsArray = elementsVar.get(name);
                int i = entry.getValue();
                int temp = encodeSingle(i, varsArray, elements.get(name));
                result = bdd.andTo(result, temp);
            }
        }
        return new PacketSet(result);
    }

    public static PacketSet createRange(Map<String, Range> s){
        return headerType._createRange(s);
    }
    PacketSet _createRange(Map<String, Range> ranges){
        // TODO
        for(Map.Entry<String, Range> entry: ranges.entrySet()){
            if(elements.containsKey(entry.getKey())){

            }
        }
        return null;
    }
    public static PacketSet createPrefix(Map<String, IPPrefix> p){
        return headerType._createPrefix(p);
    }
    PacketSet _createPrefix(Map<String, IPPrefix> p){
        int result = 1;
        for(Map.Entry<String, IPPrefix> entry: p.entrySet()){
            String name = entry.getKey();
            if(elements.containsKey(name)){
                int[] varsArray = elementsVar.get(name);
                IPPrefix i = entry.getValue();
                int temp = encodePrefix(i.getIP(), i.getPrefix(), varsArray, elements.get(name));
                result = bdd.andTo(result, temp);
                bdd.deref(temp);
            }
        }
        return new PacketSet(result);
    }

    public void setNv(NetworkVerifier nv){
        this.nv = nv;
    }

    public NetworkVerifier getNv() {
        return nv;
    }

    public static PacketSet allHeader(){
        return all;
    }

    public static PacketSet zeroHeader(){
        return zero;
    }

    public static BoundingVolume getBoundingVolume(int predicate){
        return headerType._getBoundingVolume(predicate);
    }

    public BoundingVolume _getBoundingVolume(int predicate){
        if(predicate == 0) return null;
        long[] min = new long[size];
        long[] max = new long[size];
        int i = 0;
        int cube;
        bdd.ref(predicate);
        for(Map.Entry<String, Integer> entry: elements.entrySet()){
            String name = entry.getKey();
            cube = forallElement.get(name);
            predicate = bdd.ref(bdd.exists(predicate, cube));
            int len = entry.getValue();
            int index = elementVarIndex.get(name);
            min[i] = findMinRec(predicate, index, index, len, 0L);
            max[i] = findMaxRec(predicate, index, index, len, 0L);
            i++;
        }
        return new BoundingVolume(min, max);
    }

    public long findMaxRec(int p, int level, int start, int len, long now){
        int var = bdd.getVar(p);
        if(p <2 || var >= start+len) {
            now += (1L << (len - (level - start))) -1;
            return now;
        }

        if(var > level) {
            now |= 1L<<(len-(level-start)-1);
            return findMaxRec(p, var, start, len, now);
        }
        int high = bdd.getHigh(p);

        if(high != 0){
            now |= 1L<<(len-(level-start)-1);
            return findMaxRec(high, level+1, start, len, now);
        }
        int low = bdd.getLow(p);

        return findMaxRec(low, level+1, start, len, now);
    }

    public long findMinRec(int p, int level, int start, int len, long now){
        int var = bdd.getVar(p);
        if(var >= start+len) return now;
        if(var > level) return findMinRec(p, var, start, len, now);


        int low = bdd.getLow(p);
        if(low != 0) {
            return findMinRec(low, level + 1, start, len, now);
        }

        int high = bdd.getHigh(p);
        now |= 1L<<(len-(level-start)-1);
        return findMinRec(high, level+1, start, len, now);

    }
    public static String printBV(PacketSet ps){
        return printBV(getBoundingVolume(ps.getPredicate()));
    }
    public static String printBV(BoundingVolume bv){
        StringBuilder sb = new StringBuilder();
        int i=0;
        for(String name: headerType.elements.keySet()){
            sb.append(String.format("%s: [%d - %d] ", name, bv.min[i], bv.max[i]));
            i++;
        }
        return sb.toString();
    }

//    public PacketSet ttlDecline(PacketSet ps, String key){
//        BoundingVolume bv = ps.getBv();
//        int index = elementIndex.get(key);
//        long min = bv.min[index];
//        if(min == 0) {
//            System.out.println("ttl decline on 0!");
//            return null;
//        }
//        long max = bv.max[index];
//
//        Map<String, Integer> minMap = new HashMap<>(); minMap.put(key, (int) (min-1));
//        Map<String, Integer> maxMap = new HashMap<>(); minMap.put(key, (int) max);
//        System.out.println(min);
//        System.out.println(max);
//        PacketSet minS = createSingle(minMap);
//        PacketSet maxS = createSingle(maxMap);
//
//        ps = ps.and(maxS.not());
//        int tmp = bdd.ref(bdd.forall(ps.getPredicate(), forallElement.get(key)));
//        tmp = bdd.ref(bdd.andTo(tmp, minS.getPredicate()));
//        return new PacketSet(this, tmp);
//    }
    private void declareVars(int[] vars, int bits) {
        for (int i = 0; i < bits; i++) {
            vars[i] = bdd.createVar();
        }
    }

    private int encodeSingle(long values, int[] vars, int bits) {
        int[] binRep = Utility.calBinRep(values, bits);
        int tempnode = 1;
        for (int i = 0; i < bits; i++) {
            if (i == 0) {
                tempnode = encodingVar(vars[i], binRep[bits-i-1]);
            } else {
                int tempnode2 = encodingVar(vars[i], binRep[bits-i-1]);
                int tempnode3 = bdd.ref(bdd.and(tempnode, tempnode2));
                tempnode = tempnode3;
            }
        }
//        System.out.println(Arrays.toString(binRep));
//        bdd.printSet(tempnode);
        return tempnode;
    }
    private int encodePrefix(long ipaddr, int prefixlen, int[] vars, int bits) {

        int[] ipbin = Utility.calBinRep(ipaddr, bits);
        int[] ipbinprefix = new int[prefixlen];
        for (int k = 0; k < prefixlen; k++) {
            ipbinprefix[k] = ipbin[bits-k-1];
        }
        return _encodePrefix(ipbinprefix, vars, bits);
    }

    private int _encodePrefix(int[] prefix, int[] vars, int bits) {
        if (prefix.length == 0) {
            return 1;
        }

        int tempnode = 1;
        for (int i = 0; i < prefix.length; i++) {
            if (i == 0) {
                tempnode = encodingVar(vars[i],
                        prefix[i]);
            } else {
                int tempnode2 = encodingVar(vars[i],
                        prefix[i]);
                int tempnode3 = bdd.ref(bdd.and(tempnode, tempnode2));
                tempnode = tempnode3;
            }
        }
        return tempnode;
    }

    private int encodingVar(int var, int flag) {
        if (flag == 0) {
            int tempnode = bdd.not(var);
            // no need to ref the negation of a variable.
            // the ref count is already set to maximal
            // aclBDD.ref(tempnode);
            return tempnode;
        }
        if (flag == 1) {
            return var;
        }

        // should not reach here
        System.err.println("flag can only be 0 or 1!");
        return -1;
    }
}
