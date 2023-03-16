package verifier;

public class Violation {
    Rule rule;
//    NetworkVerifier nv;

    public Violation(Rule rule){
        this.rule = rule;
//        this.nv = rule.nv;
    }

    public int getCount(){
        //TODO
        return 1;
    }
}
