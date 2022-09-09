package verifier.util;

import verifier.Edge;
import verifier.Trace;
import verifier.transformation.Transformation;

public class URule {
    public int priority;
    public Edge edge;
    public IPPrefix ipPrefix;

    public Transformation modify;

    public URule(int p, Edge e, long ip, int pr, Transformation t){
        this.priority = p;
        this.edge = e;
        this.ipPrefix = new IPPrefix(ip, pr);
        this.modify = t;
    }

    public URule(int p, Edge e, IPPrefix ipPrefix, Transformation t){
        this.priority = p;
        this.edge = e;
        this.ipPrefix = ipPrefix;
        this.modify = t;
    }
}
