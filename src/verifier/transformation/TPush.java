package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;
import verifier.util.LocatedPacket;

public class TPush extends Transformation{
    public TPush(NetworkVerifier nv) {
        super(nv);
    }

    @Override
    public HeaderStack transform(HeaderStack s) {
        LocatedPacket lp = s.top();
        return s.link(lp);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass();
    }

    @Override
    public String toString() {
        return "T-push";
    }
}
