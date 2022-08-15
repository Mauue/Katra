package verifier.util;


import verifier.Edge;
import verifier.transformation.Transformation;

import java.util.Objects;

public class Behavior {
    Edge edge;
    Transformation transformation;

    public Behavior(Edge edge, Transformation t){
        this.edge = edge;
        this.transformation = t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Behavior behavior = (Behavior) o;
        return Objects.equals(edge, behavior.edge) && Objects.equals(transformation, behavior.transformation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edge, transformation);
    }
}
