package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.Sequence;

public abstract class Transformation {
    NetworkVerifier nv;

    public Transformation(NetworkVerifier nv){
        this.nv = nv;
    }

    public abstract Sequence transform(Sequence s);
}
