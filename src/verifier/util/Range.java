package verifier.util;

public class Range {
    int min;
    int max;
    public Range(int min, int max){
        this.min = min;
        this.max = max;
    }

    public int getMin(){
        return min;
    }

    public int getMax(){
        return max;
    }
}
