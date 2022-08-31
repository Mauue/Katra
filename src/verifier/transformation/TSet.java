package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;
import verifier.util.PacketSet;

public class TSet extends Transformation{
    PacketSet p;
    public TSet(NetworkVerifier nv, PacketSet p) {
        super(nv);
        this.p = p;
    }

    @Override
    public HeaderStack transform(HeaderStack s) {
        return s.pop().push(p);
    }

    @Override
    public String toString() {
        return "T-set";
    }

    @Override
    public boolean equals(Object obj) {
        if(!this.toString().equals(obj.toString())) return false;
        return p.equals(((TSet)obj).p);
    }
}
