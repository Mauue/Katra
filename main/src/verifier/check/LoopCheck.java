package verifier.check;

import verifier.Node;
import verifier.transformation.Transformation;
import verifier.util.PacketSet;

public class LoopCheck extends Check {

    public LoopCheck(PacketSet p) {
        super(p);
    }

    @Override
    public boolean isSatisfy(Node dst, Transformation lastT) {
        return true;
    }
}