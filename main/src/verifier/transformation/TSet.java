package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;
import verifier.util.IPPrefix;
import verifier.util.PacketSet;

import java.util.Objects;

public class TSet extends Transformation{
    PacketSet p;
    PacketSet not_p;
    IPPrefix ipPrefix;

    public TSet(NetworkVerifier nv, PacketSet p) {
        super(nv);
        this.p = p;
        this.not_p = p.not();
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
        this.not_p = this.p.not();
    }

    @Override
    public HeaderStack transform(HeaderStack s) {
        PacketSet header = s.top();

        PacketSet intersection = header.and(p);
        if(!intersection.isEmpty()){
            PacketSet tmp1 = header.and(not_p);
            PacketSet res = tmp1.or(p);
            tmp1.release();
            return s.pop().push(res);
        }
        return s;
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

    @Override
    public int hashCode() {
        return Objects.hash("set")^Objects.hash(ipPrefix.getIP())^Objects.hash(ipPrefix.getPrefix());
    }
}
