package verifier;

import java.util.ArrayList;

public class Trie {
    TrieNode root;
    ArrayList<Rule> rules;

    boolean notPrefix;
    public Trie() {
        this.root = new TrieNode();
        rules = new ArrayList<>();
        notPrefix = false;
    }

    public ArrayList<Rule> addAndGetAllOverlappingWith(Rule rule) {
//        if(!rule.isPrefix || notPrefix) return _addAndGetAllOverlappingWith(rule);
        TrieNode t = this.root;
        ArrayList<Rule> ret = new ArrayList<>(t.getRules());

        long dstIp = rule.ip;
//        long bit = 1L << 31;
        for (int i = 0; i < rule.prefix; i++) {
            long bit = (dstIp >> (31 - i)) & 1;
            t = t.getNext(bit == 0 ? 0 : 1);
            ret.addAll(t.getRules());
        }

        t.explore(ret);
        t.add(rule);
//        rules.add(rule);
        return ret;
    }
    public ArrayList<Rule> getAllOverlappingWith(Rule rule) {
//        if(!rule.isPrefix || notPrefix) return _addAndGetAllOverlappingWith(rule);
        TrieNode t = this.root;
        ArrayList<Rule> ret = new ArrayList<>(t.getRules());

        long dstIp = rule.ip;
//        long bit = 1L << 31;
        for (int i = 0; i < rule.prefix; i++) {
            long bit = (dstIp >> (31 - i)) & 1;
            t = t.getNext(bit == 0 ? 0 : 1);
            ret.addAll(t.getRules());
        }

        t.explore(ret);
        return ret;
    }

    public void remove(Rule rule) {
        TrieNode t = this.root;

        long dstIp = rule.ip;
        for (int i = 0; i < rule.prefix; i++) {
            long bit = (dstIp >> (31 - i)) & 1;
            t = t.getNext(bit == 0 ? 0 : 1);
        }

        if(!t.rules.remove(rule))System.out.println("error remove");
    }
    private ArrayList<Rule> _addAndGetAllOverlappingWith(Rule rule) {
        ArrayList<Rule> result = new ArrayList<>();
        for(Rule rule1: rules){
            if(rule.match.hasOverlap(rule1.match)) result.add(rule1);
        }
        rules.add(rule);
        notPrefix = true;
        return result;
    }
    static class TrieNode {
        ArrayList<Rule> rules;
        TrieNode left, right;

        public TrieNode() {
            rules = new ArrayList<>();
            left = right = null;
        }

        public TrieNode getNext(int flag) {
            if (flag == 0) {
                if (this.left == null) {
                    this.left = new TrieNode();
                }
                return this.left;
            } else {
                if (this.right == null) {
                    this.right = new TrieNode();
                }
                return this.right;
            }
        }

        public void add(Rule rule) {
            this.rules.add(rule);
        }

        public ArrayList<Rule> getRules() {
            return this.rules;
        }

        public void explore(ArrayList<Rule> ret) {
            if (this.left != null) this.left.explore(ret);
            if (this.right != null) this.right.explore(ret);
            ret.addAll(this.getRules());
        }
    }
}
