package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

import java.util.*;

public class TSeq extends Transformation{
    List<Transformation> transformations;
    int _hashcode;

    public TSeq(NetworkVerifier nv, Transformation... transformations) {
        super(nv);
        this.transformations = new LinkedList<>();
        this.transformations.addAll(Arrays.asList(transformations));
        _hashcode = 0;
        this.transformations.forEach(t->_hashcode ^= t.hashCode());
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
        if(obj.getClass() != getClass()) return false;
        if(transformations.size() !=  ((TSeq) obj).transformations.size()) return false;
        Iterator<Transformation> iter = transformations.iterator();
        for(Transformation t:  ((TSeq) obj).transformations){
            if(!t.equals(iter.next())) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return _hashcode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Transformation t:transformations){
            sb.append(t.toString());
            sb.append(",");
        }
        return "T-seq[" + sb + "]";
    }
}
