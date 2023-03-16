package verifier.util;

public class Change {
    public PacketSet packetSet;
    public Behavior oldBehavior;
    public Behavior newBehavior;

    public Change(PacketSet packetSet, Behavior oldBehavior, Behavior newBehavior){
        this.packetSet = packetSet;
        this.oldBehavior = oldBehavior;
        this.newBehavior = newBehavior;
    }

    @Override
    public String toString() {
        return String.format("Change<%s, %s,%s>",packetSet.getPredicate(), oldBehavior, newBehavior);
    }
}
