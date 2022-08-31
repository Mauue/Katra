package verifier.util;

import verifier.Edge;
import verifier.Node;
import verifier.Rule;
import verifier.transformation.Transformation;

import java.util.Objects;

public class Behavior {
    public Transformation t;
    public Edge e;

    public Behavior(Edge e, Transformation t){
        this.e = e;
        this.t = t;
    }

    public Behavior(Rule r){
        this(r.getEdge(), r.getModify());
    }

    public Node getNode(){
        return e.src();
    }
    @Override
    public String toString() {
        return String.format("[%s,%s]", e, t);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Behavior behavior = (Behavior) o;
        return Objects.equals(t, behavior.t) && Objects.equals(e, behavior.e);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t, e);
    }
}
