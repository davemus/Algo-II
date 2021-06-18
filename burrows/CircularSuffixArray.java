import java.util.HashMap;
import java.util.Arrays;

import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
    private final int pLength;
    private final int[] indexes;

    private class StringWithOffset implements Comparable<StringWithOffset> {
        private final int offset;
        private final String string;

        public StringWithOffset(String string, int offset) {
            this.string = string;
            this.offset = offset;
        }

        public int compareTo(StringWithOffset other) {
            for (int i = 0; i < string.length(); i++) {
                char c1 = string.charAt((i + offset) % string.length());
                char c2 = other.string.charAt((i + other.offset) % other.string.length());
                if (c1 < c2) {
                    return -1;
                }
                if (c1 > c2) {
                    return 1;
                }
            }
            return 0;
        }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (null == s) {
            throw new IllegalArgumentException("The argument shouldn't be null");
        }
        pLength = s.length();
        indexes = new int[pLength];
        StringWithOffset[] strings = new StringWithOffset[pLength];
        for (int i=0; i < pLength; i++) {
            strings[i] = new StringWithOffset(s, i);
        }
        Arrays.sort(strings);
        for (int i=0; i<pLength; i++) {
            indexes[i] = strings[i].offset;
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
