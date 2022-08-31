package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

import java.util.Objects;

public class TDelv extends Transformation{
    static TDelv object = null;
    TDelv(NetworkVerifier nv) {
        super(nv);
    }

    public static TDelv getTDelv(NetworkVerifier nv){
        if(object == null) object = new TDelv(nv);
        return object;
    }
    @Override
    public HeaderStack transform(HeaderStack s) {
        return null;
    }


    @Override
    public String toString() {
        return "T-delv";
    }
}
