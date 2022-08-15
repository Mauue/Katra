package verifier;

import verifier.util.IPPrefix;
import verifier.util.PacketSet;
import verifier.util.Range;
import jdd.bdd.BDD;
import verifier.util.Utility;

import java.util.*;

public class HeaderType {
    public BDD bdd;

    NetworkVerifier nv;
    LinkedHashMap<String, Integer> elements;
    Map<String, Integer> elementsIndex;
    Map<String, int[]> elementsVar;
    int length;
    public HeaderType(Map<String, Integer> e){
        bdd = new BDD(10000, 10000);
        elements = new LinkedHashMap<>(e);
        elementsIndex = new HashMap<>();
        elementsVar = new HashMap<>();
        length = 0;
        elements.forEach((s, l)-> {
            elementsIndex.put(s, length);
            int[] array = new int[length];
            declareVars(array, length);
            elementsVar.put(s, array);
            length += l;
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

    private void declareVars(int[] vars, int bits) {
        for (int i = bits - 1; i >= 0; i--) {
            vars[i] = bdd.createVar();
        }
    }

    private int encodeSingle(long values, int[] vars, int bits) {
        int[] binRep = Utility.calBinRep(values, bits);
        int tempnode = 1;
        for (int i = 0; i < bits; i++) {
            if (i == 0) {
                tempnode = encodingVar(vars[i], binRep[i]);
            } else {
                int tempnode2 = encodingVar(vars[i], binRep[i]);
                int tempnode3 = bdd.ref(bdd.and(tempnode, tempnode2));
                tempnode = tempnode3;
            }
        }
        return tempnode;
    }
    private int encodePrefix(long ipaddr, int prefixlen, int[] vars, int bits) {

        int[] ipbin = Utility.calBinRep(ipaddr, bits);
        int[] ipbinprefix = new int[prefixlen];
        for (int k = 0; k < prefixlen; k++) {
            ipbinprefix[k] = ipbin[k + bits - prefixlen];
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
                tempnode = encodingVar(vars[bits - prefix.length + i],
                        prefix[i]);
            } else {
                int tempnode2 = encodingVar(vars[bits - prefix.length + i],
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
