package verifier.util;

public class IPPrefix {
    long IP;
    int prefix;
    public IPPrefix(long IP, int prefix){
        this.IP = IP;
        this.prefix = prefix;
    }

    public int getPrefix() {
        return prefix;
    }

    public long getIP() {
        return IP;
    }
}
