package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;
import verifier.util.IPPrefix;
import verifier.util.PacketSet;

public class TSet extends Transformation{
    PacketSet p;
    IPPrefix ipPrefix;

    public TSet(NetworkVerifier nv, PacketSet p) {
        super(nv);
        this.p = p;
    }

    public TSet(NetworkVerifier nv, IPPrefix p) {
        super(nv);
        this.ipPrefix = p;
        this.p = null;
        toBeUpdated.add(this);
    }

    @Override
    protected void update(){
        this.p = nv.createPrefix("dstip", ipPrefix);
    }

    @Override
    public HeaderStack transform(HeaderStack s) {
        return s.pop().push(p);
    }

    @Override
    public String toString() {
        return "T-set";
    }

    @Override
    public boolean equals(Object obj) {
        if(!this.toString().equals(obj.toString())) return false;
        return p.equals(((TSet)obj).p);
    }
}
