package verifier.check;

import verifier.NetworkVerifier;
import verifier.Node;
import verifier.transformation.Transformation;
import verifier.util.PacketSet;

public class ReachabilityCheck extends Check{

    public ReachabilityCheck(PacketSet predicate) {
        super(predicate);
    }
    Node dst;
    NetworkVerifier nv;

    @Override
    public boolean isSatisfy(Node dst, Transformation lastT) {
        return  (dst.equals(this.dst) && lastT.equals(nv.getTDelv()));
    }

    public ReachabilityCheck(PacketSet predicate, Node n, NetworkVerifier nv) {
        super(predicate);
        this.dst = n;
        this.nv = nv;
    }
}
