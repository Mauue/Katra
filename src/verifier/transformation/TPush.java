package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

public class TPush extends Transformation{

    public static TPush getTPush(NetworkVerifier nv){
        return new TPush(nv);
    }
    TPush(NetworkVerifier nv) {
        super(nv);
    }

    @Override
    public HeaderStack transform(HeaderStack s) {
        return s.push(s.top());
    }

    @Override
    public boolean equals(Object obj) {
        return obj.toString() == this.toString();
    }

    @Override
    public String toString() {
        return "T-push";
    }
}
