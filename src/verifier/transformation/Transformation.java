package verifier.transformation;

import verifier.NetworkVerifier;

public abstract class Transformation {
    NetworkVerifier nv;

    public Transformation(NetworkVerifier nv){
        this.nv = nv;
    }
}
