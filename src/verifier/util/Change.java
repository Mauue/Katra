package verifier.util;

import verifier.Edge;

public class Change {
    public PacketSet packetSet;
    public Behavior oldEdge;
    public Behavior newEdge;

    public Change(PacketSet packetSet, Behavior oldEdge, Behavior newEdge){
        this.packetSet = packetSet;
        this.oldEdge = oldEdge;
        this.newEdge = newEdge;
    }

    @Override
    public String toString() {
        return String.format("Change<%s,%s>",oldEdge, newEdge);
    }
}
