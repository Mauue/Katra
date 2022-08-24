package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

public class TSet extends Transformation{
    String name;
    int value;
    public TSet(NetworkVerifier nv, String name, int value) {
        super(nv);
        this.name = name;
        this.value = value;
    }

    @Override
    public HeaderStack transform(HeaderStack s) {
        //todo
        return null;
    }

    @Override
    public String toString() {
        return "T-set";
    }

    @Override
    public boolean equals(Object obj) {
        //todo
        return false;
    }
}
