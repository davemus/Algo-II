import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.StdOut;

public class BurrowsWheeler {
    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output 
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        char[] resortedStringChars = new char[s.length()];
        int substringNumber;
        String stringAtIndex;
        // TODO check if it could be done more efficiently
        for (int i = 0; i < s.length(); i++) {
            substringNumber = csa.index(i);
            if ( 0 == substringNumber ) {
                BinaryStdOut.write(i); substringNumber = s.length();
            }
            resortedStringChars[i] = s.charAt(substringNumber - 1);
        }
        for (int i = 0; i < s.length(); i++) {
            BinaryStdOut.write(resortedStringChars[i]);
        }
        BinaryStdOut.flush();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int zerosIndex = BinaryStdIn.readInt();
        ArrayList<Character> list = new ArrayList<>();
        while (!BinaryStdIn.isEmpty()) {
            list.add(BinaryStdIn.readChar());
        }
        int radix = 256;
        int[] count = new int[radix];
        int[] pointers = new int[radix];
        int[] next = new int[list.size()];

        for (char c: list) {
            count[c] += 1;
        }
        for (char c = 1; c < radix; c++) {
            pointers[c] = pointers[c - 1] + count[c - 1];
        }
        for (int i = 0; i < list.size(); i++) {
            char c = list.get(i);
            next[pointers[c]] = i;
            pointers[c] += 1;
        }
        int runningIndex = zerosIndex;
        BinaryStdOut.write(list.get(runningIndex));
        runningIndex = next[runningIndex];
        while (runningIndex != zerosIndex) {
            BinaryStdOut.write(list.get(runningIndex));
            runningIndex = next[runningIndex];
        }
        BinaryStdOut.flush();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        switch (args[0]) {
            case "-": { BurrowsWheeler.transform(); break; }
            case "+": { BurrowsWheeler.inverseTransform(); break; }
            default: throw new IllegalArgumentException(
                "Arg should be either - for transform or + for inverse transform"
            );
        }
    }
}
