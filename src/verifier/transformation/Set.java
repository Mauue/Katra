package verifier.transformation;

import verifier.NetworkVerifier;

public class Set extends Transformation{
    String name;
    int value;
    public Set(NetworkVerifier nv, String name, int value) {
        super(nv);
        this.name = name;
        this.value = value;
    }
}
