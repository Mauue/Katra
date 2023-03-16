package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

import java.util.Objects;

public class TPush extends Transformation{

    public static TPush getTPush(NetworkVerifier nv){
        return new TPush(nv);
    }
    public TPush(NetworkVerifier nv) {
        super(nv);
    }

    @Override
    public HeaderStack transform(HeaderStack s) {
        return s.push(s.top());
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(obj.getClass(), this.getClass());
    }

    @Override
    public String toString() {
        return "T-push";
    }

    @Override
    public int hashCode() {
        return Objects.hash("push");
    }
}
