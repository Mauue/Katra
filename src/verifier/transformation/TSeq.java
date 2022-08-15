package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TSeq extends Transformation{
    List<Transformation> transformations;

    public TSeq(NetworkVerifier nv, Transformation... transformations) {
        super(nv);
        this.transformations = new LinkedList<>();
        this.transformations.addAll(Arrays.asList(transformations));
    }

    @Override
    public HeaderStack transform(HeaderStack s) {
        for(Transformation t: transformations){
            s = t.transform(s);
        }
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        // todo
        return false;
    }
}
