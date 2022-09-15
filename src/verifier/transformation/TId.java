package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

import java.util.Objects;

public class TId extends Transformation{

    public static TId getTId(NetworkVerifier nv){
        return new TId(nv);
    }
    TId(NetworkVerifier nv) {
        super(nv);
    }

    @Override
    public HeaderStack transform(HeaderStack s) {
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(obj.toString(), this.toString());
    }

    @Override
    public String toString() {
        return "T-id";
    }
}
