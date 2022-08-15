package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

public class TId extends Transformation{
    public TId(NetworkVerifier nv) {
        super(nv);
    }

    @Override
    public HeaderStack transform(HeaderStack s) {
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass();
    }
}
