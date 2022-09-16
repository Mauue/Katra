package verifier.transformation;

import verifier.NetworkVerifier;
import verifier.HeaderStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class Transformation {

    final String name = "abstract";
    NetworkVerifier nv;

    static List<Transformation> toBeUpdated = new LinkedList<>();
    public Transformation(NetworkVerifier nv){
        this.nv = nv;
    }

    public abstract HeaderStack transform(HeaderStack s);

    protected void update(){};

    public static void updateAll(){
        for(Transformation t: toBeUpdated){
            t.update();
        }
        toBeUpdated.clear();
    }
}
