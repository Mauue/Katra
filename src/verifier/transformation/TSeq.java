package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.Sequence;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TSeq extends Transformation{
    List<Transformation> sequence;

    public TSeq(NetworkVerifier nv, Transformation... transformations) {
        super(nv);
        sequence = new LinkedList<>();
        sequence.addAll(Arrays.asList(transformations));
    }

    @Override
    public Sequence transform(Sequence s) {
        return null;
    }
}
