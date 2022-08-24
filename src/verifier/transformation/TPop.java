package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

public class TPop extends Transformation{

    public TPop(NetworkVerifier nv) {
        super(nv);
    }

    @Override
    public HeaderStack transform(HeaderStack s) {
        return s.bot();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass();
    }

    @Override
    public String toString() {
        return "T-pop";
    }
}
