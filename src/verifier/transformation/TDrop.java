package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

import java.util.Objects;

public class TDrop extends Transformation{
    TDrop(NetworkVerifier nv) {
        super(nv);
    }

    public static TDrop getTDrop(NetworkVerifier nv){
        return new TDrop(nv);
    }
    @Override
    public HeaderStack transform(HeaderStack s) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(obj.toString(), this.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash("drop");
    }

    @Override
    public String toString() {
        return "T-drop";
    }
}
