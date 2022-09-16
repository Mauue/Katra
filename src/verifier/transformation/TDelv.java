package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

import java.util.Objects;

public class TDelv extends Transformation{
    public TDelv(NetworkVerifier nv) {
        super(nv);
    }

    public static TDelv getTDelv(NetworkVerifier nv){
        return new TDelv(nv);
    }
    @Override
    public HeaderStack transform(HeaderStack s) {
        return null;
    }


    @Override
    public String toString() {
        return "T-delv";
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash("delv");
    }
}
