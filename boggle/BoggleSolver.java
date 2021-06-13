/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BoggleSolver {
    private final TST<String> words;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        words = new TST<>();
        for (String s : dictionary) {
            words.put(s, s);
        }
    }

    private class WordAndHistory {
        private final String word;
        private final HashSet<List<Integer>> history;
        private final List<Integer> lastPosition;

        public WordAndHistory(String word, HashSet<List<Integer>> history,
                              List<Integer> lastPosition) {
            this.word = word;
            this.history = history;
            this.lastPosition = lastPosition;
        }

        public boolean isInHistory(List<Integer> cell) {
            return history.contains(cell);
        }
    }

    private String transformLetter(char a) {
        String s = String.valueOf(a);
        if (s.equals("Q")) {
            return "QU";
        }
        return s;
    }

    private String getKeyForHelpers(String originalWord) {
        return originalWord.substring(0, 2);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        HashSet<String> wordsSoFar = new HashSet<>();
        Stack<WordAndHistory> used = new Stack<>();
        for (int x = 0; x < board.rows(); x++) {
            for (int y = 0; y < board.cols(); y++) {
                HashSet<List<Integer>> hist = new HashSet<>();
                ArrayList<Integer> position = new ArrayList<>();
                position.add(x);
                position.add(y);
                hist.add(position);
                used.push(new WordAndHistory(
                        transformLetter(board.getLetter(x, y)),
                        hist,
                        position
                ));
            }
        }
        WordAndHistory curr;
        List<Integer> newPosition;
        HashSet<List<Integer>> newHistory;
        HashMap<String, TST<String>> helperWords = new HashMap<>();
        TST<String> tst;
        int x, y;
        while (!used.isEmpty()) {
            curr = used.pop();
            if (curr.word.length() >= 3) {
                String key = getKeyForHelpers(curr.word);
                tst = helperWords.get(key);
                if (tst == null) {
                    tst = new TST<String>();
                    helperWords.put(key, tst);
                    for (String word : words.keysWithPrefix(key)) {
                        tst.put(word, word);
                    }
                }
                if (tst.contains(curr.word)) {
                    wordsSoFar.add(curr.word);
                }
                if (!tst.keysWithPrefix(curr.word).iterator().hasNext()) {
                    continue;
                }
            }
            x = curr.lastPosition.get(0);
            y = curr.lastPosition.get(1);
            for (int newX = Math.max(0, x - 1); newX < Math.min(x + 2, board.rows());
                 newX = newX + 1) {
                for (int newY = Math.max(0, y - 1); newY < Math.min(y + 2, board.cols());
                     newY = newY + 1) {
                    if (newX == x && newY == y) {
                        continue;
                    }
                    newPosition = new ArrayList<>();
                    newPosition.add(newX);
                    newPosition.add(newY);
                    if (!curr.history.contains(newPosition)) {
                        newHistory = new HashSet<>(curr.history);
                        newHistory.add(newPosition);
                        used.push(new WordAndHistory(
                                curr.word + transformLetter(board.getLetter(newX, newY)),
                                newHistory,
                                newPosition
                        ));
                    }
                }
            }
        }
        // for (String key : helperWords.keySet()) {
        //     StdOut.println(key);
        // }
        //
        // for (String key : helperWords.get("TA").keys()) {
        //     StdOut.println(key);
        // }
        return wordsSoFar;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!words.contains(word) || word.length() < 3) {
            return 0;
        }
        switch (word.length()) {
            case 3:
                return 1;
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default:
                return 11;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        int wordsCount = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
            wordsCount += 1;
        }
        StdOut.println("Score = " + score);
        StdOut.println("Words count = " + wordsCount);
    }

}
