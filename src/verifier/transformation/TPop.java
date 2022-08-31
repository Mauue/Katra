package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

import java.util.Objects;

public class TPop extends Transformation{
    static TPop object = null;
    TPop(NetworkVerifier nv) {
        super(nv);
    }

    public static TPop getTPop(NetworkVerifier nv){
        if(object == null) object = new TPop(nv);
        return object;
    }
    @Override
    public HeaderStack transform(HeaderStack s) {
        return s.bot();
    }

    @Override
    public String toString() {
        return "T-pop";
    }
}
