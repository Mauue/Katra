package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

public class TDelv extends Transformation{
    public TDelv(NetworkVerifier nv) {
        super(nv);
    }

    @Override
    public HeaderStack transform(HeaderStack s) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass();
    }

    @Override
    public String toString() {
        return "T-delv";
    }
}
