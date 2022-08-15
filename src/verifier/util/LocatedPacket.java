package verifier.util;

import verifier.Node;
import verifier.PEC;

import java.util.Collection;
import java.util.Collections;

public class LocatedPacket {
    Node node;
    Collection<PEC> hs;

    public LocatedPacket(Node u, Collection<PEC> h){
        node = u;
        hs = h;
    }

    public LocatedPacket(Node u, PEC h){
        node = u;
        hs = Collections.singletonList(h);
    }

    public Collection<PEC> getHeader() {
        return hs;
    }

    public Node getNode() {
        return node;
    }

    public LocatedPacket and(PEC pec){
        // todo
        return null;
    }
}
