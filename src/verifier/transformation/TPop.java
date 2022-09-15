package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

import java.util.Objects;

public class TPop extends Transformation{
    TPop(NetworkVerifier nv) {
        super(nv);
    }

    public static TPop getTPop(NetworkVerifier nv){
        return new TPop(nv);
    }
    @Override
    public HeaderStack transform(HeaderStack s) {
        if(s.getLen()==1) return null;
        return s.bot();
    }

    @Override
    public String toString() {
        return "T-pop";
    }
}
