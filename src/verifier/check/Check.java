package verifier.check;

import verifier.NetworkVerifier;
import verifier.widget.HeaderSet;

public class Check {
    HeaderSet hs;
    NetworkVerifier nv;
    public Check(HeaderSet hs){
        this.hs = hs;
        this.nv = hs.getNv();
    }
}
