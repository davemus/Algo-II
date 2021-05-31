import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
    private Picture pPicture;

    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();
        this.pPicture = new Picture(picture);
    }

    private void checkY(int y) {
        if (y < 0 || y >= height()) throw new IllegalArgumentException();
    }

    private void checkX(int x) {
        if (x < 0 || x >= width()) throw new IllegalArgumentException();
    }

    public Picture picture() {
        return new Picture(this.pPicture);
    }

    public int width() {
        return this.pPicture.width();
    }

    public int height() {
        return this.pPicture.height();
    }

    // computes sum of squared differences of color components
    private double energyHelper(Color c1, Color c2) {
        return Math.pow(c1.getRed() - c2.getRed(), 2)
                + Math.pow(c1.getGreen() - c2.getGreen(), 2)
                + Math.pow(c1.getBlue() - c2.getBlue(), 2);
    }

    public double energy(int x, int y) {
        checkX(x);
        checkY(y);
        if (0 == x || width() - 1 == x || 0 == y || height() - 1 == y)
            return 1000;
        Color x1, x2, y1, y2;
        x1 = pPicture.get(x - 1, y);
        x2 = pPicture.get(x + 1, y);
        y1 = pPicture.get(x, y - 1);
        y2 = pPicture.get(x, y + 1);
        return Math.sqrt(energyHelper(x1, x2) + energyHelper(y1, y2));
    }

    public int[] findHorizontalSeam() {
        // precompute energies, as they will be used 3 times on average
        double[][] energies = new double[width()][height()];
        double[][] bestWeights = new double[width()][height()];
        int[][] bestPaths = new int[width()][height()];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                energies[x][y] = energy(x, y);
                bestWeights[x][y] = Double.POSITIVE_INFINITY;
                bestPaths[x][y] = -1;
            }
        }
        for (int y = 0; y < height(); y++) bestWeights[0][y] = energies[0][y];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                for (int newY = y - 1; newY <= y + 1; newY++) {
                    try {
                        if (bestWeights[x][y] + energies[x + 1][newY] < bestWeights[x + 1][newY]) {
                            bestPaths[x + 1][newY] = y;
                            bestWeights[x + 1][newY] = bestWeights[x][y] + energies[x + 1][newY];
                        }
                    }
                    catch (IndexOutOfBoundsException ignored) {
                    }
                }
            }
        }
        int bestPathEndIdx = 0;
        double bestPathWeight = Double.POSITIVE_INFINITY;
        for (int y = 0; y < height(); y++) {
            if (bestWeights[bestWeights.length - 1][y] < bestPathWeight) {
                bestPathEndIdx = y;
                bestPathWeight = bestWeights[bestWeights.length - 1][y];
            }
        }
        int[] seam = new int[width()];
        for (int x = width() - 1; x >= 0; x--) {
            seam[x] = bestPathEndIdx;
            bestPathEndIdx = bestPaths[x][bestPathEndIdx];
        }
        return seam;
    }

    public int[] findVerticalSeam() {
        // precompute energies, as they will be used 3 times on average
        double[][] energies = new double[width()][height()];
        double[][] bestWeights = new double[width()][height()];
        int[][] bestPaths = new int[width()][height()];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                energies[x][y] = energy(x, y);
                bestWeights[x][y] = Double.POSITIVE_INFINITY;
                bestPaths[x][y] = -1;
            }
        }
        for (int x = 0; x < width(); x++) bestWeights[x][0] = energies[x][0];
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                for (int newX = x - 1; newX <= x + 1; newX++) {
                    try {
                        if (bestWeights[x][y] + energies[newX][y + 1] < bestWeights[newX][y + 1]) {
                            bestPaths[newX][y + 1] = x;
                            bestWeights[newX][y + 1] = bestWeights[x][y] + energies[newX][y + 1];
                        }
                    }
                    catch (IndexOutOfBoundsException ignored) {
                    }
                }
            }
        }
        int bestPathEndIdx = 0;
        double bestPathWeight = Double.POSITIVE_INFINITY;
        for (int x = 0; x < width(); x++) {
            if (bestWeights[x][height() - 1] < bestPathWeight) {
                bestPathEndIdx = x;
                bestPathWeight = bestWeights[x][height() - 1];
            }
        }
        int[] seam = new int[height()];
        for (int y = height() - 1; y >= 0; y--) {
            seam[y] = bestPathEndIdx;
            bestPathEndIdx = bestPaths[bestPathEndIdx][y];
        }
        return seam;
    }

    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (seam.length != width()) throw new IllegalArgumentException();
        if (height() == 1) throw new IllegalArgumentException();
        checkY(seam[0]);
        Arrays.stream(seam).reduce(
                (int y1, int y2) -> {
                    checkY(y2);
                    if (Math.abs(y1 - y2) > 1)
                        throw new IllegalArgumentException();
                    return y2;
                });
        // end of validations
        Picture newPicture = new Picture(width(), height() - 1);
        for (int x = 0; x < width(); x++) {
            boolean removedWasPassed = false;
            Color color;
            for (int y = 0; y < height(); y++) {
                if (!removedWasPassed && seam[x] == y) {
                    removedWasPassed = true;
                    continue;
                }
                color = pPicture.get(x, y);
                if (removedWasPassed) newPicture.set(x, y - 1, color);
                else newPicture.set(x, y, color);
            }
        }
        this.pPicture = newPicture;
    }

    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (seam.length != height()) throw new IllegalArgumentException();
        if (width() == 1) throw new IllegalArgumentException();
        checkX(seam[0]);
        Arrays.stream(seam).reduce(
                (int x1, int x2) -> {
                    checkX(x2);
                    if (Math.abs(x1 - x2) > 1)
                        throw new IllegalArgumentException();
                    return x2;
                });
        // end of validations
        Picture newPicture = new Picture(width() - 1, height());
        for (int y = 0; y < height(); y++) {
            boolean removedWasPassed = false;
            Color color;
            for (int x = 0; x < width(); x++) {
                if (!removedWasPassed && seam[y] == x) {
                    removedWasPassed = true;
                    continue;
                }
                color = pPicture.get(x, y);
                if (removedWasPassed) newPicture.set(x - 1, y, color);
                else newPicture.set(x, y, color);
            }
        }
        this.pPicture = newPicture;
    }

    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver carver = new SeamCarver(picture);
        carver.picture().show();
        carver.findVerticalSeam();
    }
}
