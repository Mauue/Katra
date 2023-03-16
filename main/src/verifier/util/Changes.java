package verifier.util;

import jdd.bdd.BDD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Changes {
    private HashMap<Behavior, HashMap<Behavior, PacketSet>> oldToNewBehavior;
    private HashMap<PacketSet, ArrayList<Pair<Behavior, Behavior>>> bddToChanges;


    public Changes() {
        oldToNewBehavior = new HashMap<>();
    }

    public void add(PacketSet deltaBdd, Behavior oldPort, Behavior newPort) {
        oldToNewBehavior.putIfAbsent(oldPort, new HashMap<>());
        HashMap<Behavior, PacketSet> newPortToBdd = oldToNewBehavior.get(oldPort);
        if (newPortToBdd.containsKey(newPort)) {
            PacketSet oldBdd = newPortToBdd.get(newPort);
            PacketSet union = oldBdd.or(deltaBdd);
            newPortToBdd.replace(newPort, union);
            oldBdd.release();
        } else {
            newPortToBdd.put(newPort, deltaBdd);
            deltaBdd.increase();
        }
    }

    public HashMap<PacketSet, ArrayList<Pair<Behavior, Behavior>>> getAll() {
        return bddToChanges;
    }

    public void releaseBdd() {
        for (HashMap<Behavior, PacketSet> value : oldToNewBehavior.values()) {
            for (PacketSet bdd : value.values()) {
                bdd.release();
            }
        }
    }
    public void aggrBDDs() {
        bddToChanges = new HashMap<>();
        for (Map.Entry<Behavior, HashMap<Behavior, PacketSet>> entryI : oldToNewBehavior.entrySet()){
            Behavior oldPort = entryI.getKey();
            for (Map.Entry<Behavior, PacketSet> entryJ : entryI.getValue().entrySet()) {
                Behavior newPort = entryJ.getKey();
                PacketSet bdd = entryJ.getValue();
                if (!bddToChanges.containsKey(bdd)) {
                    bddToChanges.put(bdd, new ArrayList<>());
                }
                bddToChanges.get(bdd).add(new Pair<>(oldPort, newPort));
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
        for (Behavior oldPort : t.oldToNewBehavior.keySet()) {
            for (Map.Entry<Behavior, PacketSet> entry : t.oldToNewBehavior.get(oldPort).entrySet()) {
                this.add(entry.getValue(), oldPort, entry.getKey());
            }
        }
    }
}
