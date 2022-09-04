package test;

import verifier.HeaderType;
import verifier.NetworkVerifier;

import java.util.HashMap;
import java.util.Map;

public class GreenStartTest {
    public static void main(String[] args) {
        Map<String, Integer> headerSettings = new HashMap<>();
        headerSettings.put("dstip", 32);
//        headerSettings.put("ttl", 8);

        HeaderType.update(headerSettings);
        Loader loader = new Loader();
        NetworkVerifier nv = loader.nv;

        loader.setTopologyByFile("st/st.topology");
        loader.readFibDict("st/ruleExp/");

        nv.calInitPEC();
        System.out.println(nv.getPecs().size());
        System.out.println(nv.getPecs());
    }
}
