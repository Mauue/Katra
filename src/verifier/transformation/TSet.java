package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.Sequence;

public class TSet extends Transformation{
    String name;
    int value;
    public TSet(NetworkVerifier nv, String name, int value) {
        super(nv);
        this.name = name;
        this.value = value;
    }

    @Override
    public Sequence transform(Sequence s) {
        return null;
    }
}
