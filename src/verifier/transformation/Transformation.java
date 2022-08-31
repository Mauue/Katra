package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

public abstract class Transformation {

    final static String name = "abstract";
    NetworkVerifier nv;

    public Transformation(NetworkVerifier nv){
        this.nv = nv;
    }

    public abstract HeaderStack transform(HeaderStack s);

}
