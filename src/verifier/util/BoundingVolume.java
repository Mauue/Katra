package verifier.util;

import java.util.Vector;

public class BoundingVolume {
    public long[] min;
    public long[] max;

    int dimension;

    public BoundingVolume(long[] min, long[]  max){
        assert min.length == max.length;
        this.min = min;
        this.max = max;
        dimension = min.length;
    }

    public boolean isIntersection(BoundingVolume bv2){
        assert this.dimension == bv2.dimension;
        for(int i=0; i<dimension;i++){
            if(!singleDimensionIntersection(min[i], max[i], bv2.min[i], bv2.max[i]))
                return false;
        }
        return true;
    }

    private boolean singleDimensionIntersection(long min1, long max1, long min2, long max2){
        return !((min1 >= max2) || (min2 >= max1));
    }
}
