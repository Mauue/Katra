package verifier;

import verifier.widget.IPPrefix;
import verifier.widget.Range;
import jdd.bdd.BDD;
import verifier.widget.HeaderSet;

import java.util.*;

public class HeaderType {
    BDD bdd;

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

    public HeaderSet createRange(Map<String, Range> ranges){
        // TODO
        for(Map.Entry<String, Range> entry: ranges.entrySet()){
            if(elements.containsKey(entry.getKey())){

            }
        }
        return null;
    }

    public HeaderSet createPrefix(Map<String, IPPrefix> p){
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
        return new HeaderSet(this, result);
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
    private int encodePrefix(long ipaddr, int prefixlen, int[] vars, int bits) {

        int[] ipbin = Util.CalBinRep(ipaddr, bits);
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
                tempnode = EncodingVar(vars[bits - prefix.length + i],
                        prefix[i]);
            } else {
                int tempnode2 = EncodingVar(vars[bits - prefix.length + i],
                        prefix[i]);
                int tempnode3 = bdd.ref(bdd.and(tempnode, tempnode2));
                tempnode = tempnode3;
            }
        }
        return tempnode;
    }

    private int EncodingVar(int var, int flag) {
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
