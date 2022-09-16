package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

import java.util.Objects;

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
        return Objects.equals(obj.getClass(), this.getClass());
    }

    @Override
    public String toString() {
        return "T-id";
    }

    @Override
    public int hashCode() {
        return Objects.hash("id");
    }
}
