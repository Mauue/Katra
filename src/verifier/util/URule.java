package verifier.util;

import verifier.Edge;
import verifier.transformation.Transformation;

public class URule {
    public int priority;
    public Edge edge;
    public IPPrefix ipPrefix;

    public URule(int p, Edge e, long ip, int pr){
        this.priority = p;
        this.edge = e;
        this.ipPrefix = new IPPrefix(ip, pr);
    }
}
