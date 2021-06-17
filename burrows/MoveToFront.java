import java.util.LinkedList;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.StdOut;

public class MoveToFront {

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        LinkedList<Character> positionStorage = new LinkedList<>();        
        for (char c=0; c < 256; c++) {
            positionStorage.add(c);
        }
        while (!BinaryStdIn.isEmpty()) {
            char input = BinaryStdIn.readChar();
            char position = (char)positionStorage.indexOf(input);
            BinaryStdOut.write(position);
            positionStorage.remove(Character.valueOf(input));
            positionStorage.addFirst(input);
        }
        BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        LinkedList<Character> positionStorage = new LinkedList<>();        
        for (char c=0; c < 256; c++) {
            positionStorage.add(c);
        }
        while (!BinaryStdIn.isEmpty()) {
            int index = (int)BinaryStdIn.readChar();
            char output = positionStorage.get(index);
            BinaryStdOut.write(output);
            positionStorage.remove(Character.valueOf(output));
            positionStorage.addFirst(output);
        }
        BinaryStdOut.flush();

    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        switch (args[0]) {
            case "+": { MoveToFront.decode(); break; }
            case "-": { MoveToFront.encode(); break; }
            default: throw new IllegalArgumentException(
                "You should provide either + for decoding or - for encoding"
            );
        }
    }
}

