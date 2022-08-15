package verifier.util;

import java.util.Vector;

public class BoundingVolume {
    Vector<Integer> min;
    Vector<Integer> max;

    int dimension;

    public BoundingVolume(Vector<Integer> min, Vector<Integer>  max){
        assert min.size() == max.size();
        this.min = min;
        this.max = max;
        dimension = min.size();
    }

    public boolean isIntersection(BoundingVolume bv2){
        assert this.dimension == bv2.dimension;
        for(int i=0; i<dimension;i++){
            if(!singleDimensionIntersection(min.get(i), max.get(i), bv2.min.get(i), bv2.max.get(i)))
                return false;
        }
        return true;
    }

    private boolean singleDimensionIntersection(int min1, int max1, int min2, int max2){
        return !((min1 >max2) || (min2> max1));
    }
}
