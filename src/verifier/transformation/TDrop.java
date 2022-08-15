package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

public class TDrop extends Transformation{
    public TDrop(NetworkVerifier nv) {
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
}