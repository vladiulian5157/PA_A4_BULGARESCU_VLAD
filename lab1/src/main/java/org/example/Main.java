import java.util.*;

public class Main {

    public static int[] findBoundingBox(int[][] matrix, int threshold) {
        int n = matrix.length;
        int minRow = Integer.MAX_VALUE;
        int maxRow = Integer.MIN_VALUE;
        int minCol = Integer.MAX_VALUE;
        int maxCol = Integer.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] < threshold) {
                    if (i < minRow) minRow = i;
                    if (i > maxRow) maxRow = i;
                    if (j < minCol) minCol = j;
                    if (j > maxCol) maxCol = j;
                }
            }
        }

        if (minRow == Integer.MAX_VALUE) return null;
        return new int[]{minRow, minCol, maxRow, maxCol};
    }

    public static void main(String[] args) {

        System.out.println("Hello World!");

        String[] languages = {"C", "C++", "C#", "Python", "Go", "Rust", "JavaScript", "PHP", "Swift", "Java"};

        int n = (int)(Math.random() * 1_000_000);
        n = n * 3;
        n = n + 0b10101;
        n = n + 0xFF;
        n = n * 6;

        int result = n;
        while (result > 9) {
            int sum = 0;
            while (result > 0) {
                sum += result % 10;
                result /= 10;
            }
            result = sum;
        }

        System.out.println("Willy-nilly, this semester I will learn " + languages[result]);

        int[][] example = {
                {255,255,255,255,255,255,255,255,255,255,255,255,255,255,255},
                {255,255,255,200,180,160,255,255,255,255,255,255,255,255,255},
                {255,255,180,100, 80, 70,160,255,255,255,255,255,255,255,255},
                {255,200,120, 90, 60, 50, 80,200,255,255,255,255,255,255,255},
                {255,180,100, 70, 50, 40, 60,150,255,255,255,255,255,255,255},
                {255,255,160, 90, 60, 50, 80,200,255,255,255,255,255,255,255},
                {255,255,255,180,100, 80,160,255,255,255,255,255,255,255,255},
                {255,255,255,255,200,180,255,255,255,255,255,255,255,255,255},
                {255,255,255,255,255,255,255,255,255,255,255,255,255,255,255},
                {255,255,255,255,255,255,255,255,255,255,255,255,255,255,255},
                {255,255,255,255,255,255,255,255,255,255,255,255,255,255,255},
                {255,255,255,255,255,255,255,255,255,255,255,255,255,255,255},
                {255,255,255,255,255,255,255,255,255,255,255,255,255,255,255},
                {255,255,255,255,255,255,255,255,255,255,255,255,255,255,255},
                {255,255,255,255,255,255,255,255,255,255,255,255,255,255,255}
        };

        int threshold = 200;
        int[] box = findBoundingBox(example, threshold);

        if (box != null) {
            System.out.println("Bounding box:");
            System.out.println("Top-left: (" + box[0] + ", " + box[1] + ")");
            System.out.println("Bottom-right: (" + box[2] + ", " + box[3] + ")");
        } else {
            System.out.println("No shape detected.");
        }
    }
}
