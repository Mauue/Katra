package verifier;

import verifier.util.*;
import jdd.bdd.BDD;

import java.util.*;

public class HeaderType {
    public BDD bdd;

    NetworkVerifier nv;
    LinkedHashMap<String, Integer> elements;
    Map<String, Integer> elementsIndex;
    Map<String, int[]> elementsVar;

    Map<String, Integer> forallElement; // jdd-forall

    int length;
    int size;
    public HeaderType(Map<String, Integer> e){
        bdd = new BDD(10000, 10000);
        elements = new LinkedHashMap<>(e);
        elementsIndex = new HashMap<>();
        elementsVar = new HashMap<>();
        forallElement = new HashMap<>();
        length = 0;
        size = e.size();
        elements.forEach((s, l)-> {
            elementsIndex.put(s, length);
            length += l;
            int[] array = new int[length];
            declareVars(array, length);
            elementsVar.put(s, array);
        });
        int last = 1;
        elements.forEach((s, l)-> {
            int res = last;
            int[] array = elementsVar.get(s);
            for(Integer i:array){
                res = bdd.andTo(res, i);
            }
            bdd.ref(res);
            forallElement.put(s, res);
        });
    }

    public PacketSet createSingle(Map<String, Integer> s){
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
        return new PacketSet(this, result);
    }
    public PacketSet createRange(Map<String, Range> ranges){
        // TODO
        for(Map.Entry<String, Range> entry: ranges.entrySet()){
            if(elements.containsKey(entry.getKey())){

            }
        }
        return null;
    }

    public PacketSet createPrefix(Map<String, IPPrefix> p){
        int result = 1;
        for(Map.Entry<String, IPPrefix> entry: p.entrySet()){
            String name = entry.getKey();
            if(elements.containsKey(name)){
                int[] varsArray = elementsVar.get(name);
                IPPrefix i = entry.getValue();
                int temp = encodePrefix(i.getIP(), i.getPrefix(), varsArray, elements.get(name));
                result = bdd.andTo(result, temp);
            }
        }
        return new PacketSet(this, result);
    }

    public void setNv(NetworkVerifier nv){
        this.nv = nv;
    }

    public NetworkVerifier getNv() {
        return nv;
    }

    public BoundingVolume getBoundingVolume(int predicate){
        long[] min = new long[size];
        long[] max = new long[size];
        int i = 0;
        int cube = 1;
        for(Map.Entry<String, Integer> entry: elements.entrySet()){
            predicate = bdd.exists(predicate, cube);
            String name = entry.getKey();
            int len = entry.getValue();
            int index = elementsIndex.get(name);
//            if(predicate >= 2){
//                bdd.print(predicate);
//                bdd.printSet(predicate);
//            }
            min[i] = findMinRec(predicate, index, index, len, 0L);
            max[i] = findMaxRec(predicate, index, index, len, 0L);
            i++;
            cube = forallElement.get(name);
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

    public String printBV(BoundingVolume bv){
        StringBuilder sb = new StringBuilder();
        int i=0;
        for(String name: elements.keySet()){
            sb.append(String.format("%s: [%d - %d] ", name, bv.min[i], bv.max[i]));
            i++;
        }
        return sb.toString();
    }
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
