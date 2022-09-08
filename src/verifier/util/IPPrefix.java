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

    @Override
    public String toString() {
        return String.format("%s %s", IP, prefix);
    }
}
