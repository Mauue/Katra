package verifier.util;

import jdd.bdd.BDD;
import verifier.Edge;
import verifier.HeaderType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Changes {
    private HashMap<Behavior, HashMap<Behavior, PacketSet>> oldToNewBehavior;
    private HashMap<PacketSet, ArrayList<Pair<Behavior, Behavior>>> bddToChanges;
    private HeaderType bddEngine;

    public Changes(HeaderType bddEngine) {
        this.bddEngine = bddEngine;
        oldToNewBehavior = new HashMap<>();
    }

    public void add(PacketSet deltaBdd, Behavior oldBehavior, Behavior newBehavior) {
        if (!oldToNewBehavior.containsKey(oldBehavior)) oldToNewBehavior.put(oldBehavior, new HashMap<>());
        HashMap<Behavior, PacketSet> newPortToBdd = oldToNewBehavior.get(oldBehavior);
        if (newPortToBdd.containsKey(newBehavior)) {
            PacketSet oldBdd = newPortToBdd.get(newBehavior);
            PacketSet union = oldBdd.or(deltaBdd);
            newPortToBdd.replace(newBehavior, union);
        } else {
            newPortToBdd.put(newBehavior, deltaBdd);
        }
    }

    public HashMap<PacketSet, ArrayList<Pair<Behavior, Behavior>>> getAll() {
        return bddToChanges;
    }

//    public void releaseBdd() {
//        for (HashMap<Edge, PacketSet> value : oldToNewPort.values()) {
//            for (Integer bdd : value.values()) {
//                bddEngine.bdd.deref(bdd);
//            }
//        }
//    }

    public void aggrBDDs() {
        bddToChanges = new HashMap<>();
        for (Map.Entry<Behavior, HashMap<Behavior, PacketSet>> entryI : oldToNewBehavior.entrySet()){
            Behavior oldBehavior = entryI.getKey();
            for (Map.Entry<Behavior, PacketSet> entryJ : entryI.getValue().entrySet()) {
                Behavior newBehavior = entryJ.getKey();
                PacketSet bdd = entryJ.getValue();
                if (!bddToChanges.containsKey(bdd)) {
                    bddToChanges.put(bdd, new ArrayList<>());
                }
                bddToChanges.get(bdd).add(new Pair<>(oldBehavior, newBehavior));
            }
        }
    }

    public int size() {
        int ret = 0;
        for (HashMap<Behavior, PacketSet> value : oldToNewBehavior.values()) ret += value.size();
        return ret;
    }

    public int bdds() {
        HashSet<PacketSet> ret = new HashSet<>();
        for (HashMap<Behavior, PacketSet> value : oldToNewBehavior.values()) ret.addAll(value.values());
        return ret.size();
    }


    public void merge(Changes t) {
        for (Behavior oldBehavior : t.oldToNewBehavior.keySet()) {
            for (Map.Entry<Behavior, PacketSet> entry : t.oldToNewBehavior.get(oldBehavior).entrySet()) {
                this.add(entry.getValue(), oldBehavior, entry.getKey());
            }
        }
    }
}
