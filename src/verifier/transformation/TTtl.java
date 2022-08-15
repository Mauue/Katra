package verifier.transformation;

import verifier.HeaderStack;
import verifier.NetworkVerifier;

public class TTtl extends Transformation{
    public TTtl(NetworkVerifier nv) {
        // todo
        super(nv);
    }

    @Override
    public HeaderStack transform(HeaderStack s) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
