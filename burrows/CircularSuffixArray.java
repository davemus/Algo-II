import java.util.HashMap;
import java.util.Arrays;

import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
    private final int pLength;
    private final int[] indexes;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (null == s) {
            throw new IllegalArgumentException("The argument shouldn't be null");
        }
        pLength = s.length();
        indexes = new int[pLength];
        String[] strings = new String[pLength];
        HashMap<String, Integer> initialPositions = new HashMap<>();
        for (int i=0; i < pLength; i++) {
            strings[i] = s.substring(i, pLength) + s.substring(0, i);
            initialPositions.put(strings[i], i);
        }
        Arrays.sort(strings);
        for (int i=0; i<pLength; i++) {
            indexes[i] = initialPositions.get(strings[i]);
        }
    }

    // length of s
    public int length() {
        return pLength;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (0 > i || pLength <= i) {
            throw new IllegalArgumentException("The index is out of prescribed length");
        }
        return indexes[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray(args[0]);
        for (int i = 0; i < csa.length(); i++) {
            StdOut.println(String.format("%d - %d", i, csa.index(i)));
        }
    }
}
