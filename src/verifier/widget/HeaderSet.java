package verifier.widget;

import verifier.HeaderType;
import verifier.NetworkVerifier;

public class HeaderSet {
    HeaderType ht;
    NetworkVerifier nv;
    int content;
    public HeaderSet(HeaderType ht, int content){
        this.ht = ht;
        this.content = content;
        this.nv = ht.getNv();
    }

    public NetworkVerifier getNv() {
        return nv;
    }
}
