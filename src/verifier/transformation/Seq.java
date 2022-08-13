package verifier.transformation;

import verifier.NetworkVerifier;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Seq extends Transformation{
    List<Transformation> sequence;

    public Seq(NetworkVerifier nv, Transformation... transformations) {
        super(nv);
        sequence = new LinkedList<>();
        sequence.addAll(Arrays.asList(transformations));
    }
}
