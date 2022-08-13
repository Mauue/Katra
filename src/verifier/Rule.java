package verifier;

import verifier.transformation.Transformation;
import verifier.widget.HeaderSet;

public class Rule {
    int priority;
    Edge edge;
    HeaderSet match;
    Transformation modify;

    NetworkVerifier nv;

    public Rule(int p, Edge e, HeaderSet hs, Transformation t){
        this.priority = p;
        this.edge = e;
        this.match = hs;
        this.modify = t;
        this.nv = hs.getNv();
    }
}
