package verifier.check;

import verifier.Node;
import verifier.transformation.Transformation;
import verifier.util.PacketSet;

public abstract class Check {
    PacketSet predicate;
    public Check(PacketSet predicate){
        this.predicate = predicate;
    }

    public abstract boolean isSatisfy(Node dst, Transformation lastT);
}
