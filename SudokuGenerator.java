package sudoku;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class SudokuGenerator {

    // Generate a Sudoku grid with K empty cells
    public static int[][] sudokuGenerator(int k) {
        int[][] grid = new int[9][9];
        fillDiagonal(grid);
        fillRemaining(grid, 0, 0);
        return grid;
    }

    // Generate an unsolvable Sudoku grid
    public static int[][] generateUnsolvableSudoku() {
        int[][] grid = sudokuGenerator(20); // Start with a valid Sudoku
        grid[0][0] = grid[0][1]; // Introduce a conflict to make it unsolvable
        return grid;
    }

    // Fill the diagonal 3x3 matrices
    private static void fillDiagonal(int[][] grid) {
        for (int i = 0; i < 9; i += 3) {
            fillBox(grid, i, i);
        }
    }

    // Fill a 3x3 matrix
    private static void fillBox(int[][] grid, int row, int col) {
        Random rand = new Random();
        boolean[] used = new boolean[10];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int num;
                do {
                    num = rand.nextInt(9) + 1;
                } while (used[num]);
                used[num] = true;
                grid[row + i][col + j] = num;
            }
        }
    }

    // Fill remaining blocks in the grid
    private static boolean fillRemaining(int[][] grid, int i, int j) {
        if (i == 9) return true;
        if (j == 9) return fillRemaining(grid, i + 1, 0);
        if (grid[i][j] != 0) return fillRemaining(grid, i, j + 1);

        for (int num = 1; num <= 9; num++) {
            if (checkIfSafe(grid, i, j, num)) {
                grid[i][j] = num;
                if (fillRemaining(grid, i, j + 1)) return true;
                grid[i][j] = 0;
            }
        }
        return false;
    }

    // Check if it's safe to place a number in a cell
    private static boolean checkIfSafe(int[][] grid, int i, int j, int num) {
        return unUsedInRow(grid, i, num) && unUsedInCol(grid, j, num) &&
               unUsedInBox(grid, i - i % 3, j - j % 3, num);
    }

    private static boolean unUsedInRow(int[][] grid, int i, int num) {
        for (int j = 0; j < 9; j++) {
            if (grid[i][j] == num) return false;
        }
        return true;
    }

    private static boolean unUsedInCol(int[][] grid, int j, int num) {
        for (int i = 0; i < 9; i++) {
            if (grid[i][j] == num) return false;
        }
        return true;
    }

    private static boolean unUsedInBox(int[][] grid, int rowStart, int colStart, int num) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[rowStart + i][colStart + j] == num) return false;
            }
        }
        return true;
    }

    // Remove K digits randomly to create the puzzle
    private static void removeKDigits(int[][] grid, int k) {
        Random rand = new Random();
        while (k > 0) {
            int cellId = rand.nextInt(81);
            int i = cellId / 9;
            int j = cellId % 9;
            if (grid[i][j] != 0) {
                grid[i][j] = 0;
                k--;
            }
        }
    }

    private static void saveBoardToFile(int[][] puzzle, String filename) {
        try (FileWriter writer = new FileWriter(filename, true)) {
            for (int[] row : puzzle) {
                for (int cell : row) {
                    writer.write(cell + " ");
                }
                writer.write("\n");
            }
            writer.write("\n"); // Add a blank line between puzzles
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private static int[][] copyArray(int[][] board) {
        int[][] copiedBoard = new int[9][9];
        for (int r = 0; r < 9; r++) {
            System.arraycopy(board[r], 0, copiedBoard[r], 0, 9);
        }
        return copiedBoard;
    }

        public static void main(String[] args) {
        System.out.println("Generating Sudoku puzzles and saving to file...");

        String easy = "easy_puzzles.txt";
        String medium = "medium_puzzles.txt";
        String hard = "hard_puzzles.txt";
        String easyAnswer = "easy_answer.txt";
        String mediumAnswer = "medium_answer.txt";
        String hardAnswer = "hard_answer.txt";
        String unsolvable = "unsolvable_puzzles.txt";

        // Generate and save 10 Easy puzzles
        for (int i = 0; i < 10; i++) {
            int[][] solution = sudokuGenerator(0); // Generate a fully solved grid
            int[][] puzzle = copyArray(solution);  // Create a copy of the solution
            removeKDigits(puzzle, 30);           // Remove 30 digits for Easy difficulty
            saveBoardToFile(puzzle, easy);
            saveBoardToFile(solution, easyAnswer);
        }

        // Generate and save 10 Medium puzzles
        for (int i = 0; i < 10; i++) {
            int[][] solution = sudokuGenerator(0); // Generate a fully solved grid
            int[][] puzzle = copyArray(solution);  // Create a copy of the solution
            removeKDigits(puzzle, 45);           // Remove 45 digits for Medium difficulty
            saveBoardToFile(puzzle, medium);
            saveBoardToFile(solution, mediumAnswer);        }

        // Generate and save 10 Hard puzzles
        for (int i = 0; i < 10; i++) {
            int[][] solution = sudokuGenerator(0); // Generate a fully solved grid
            int[][] puzzle = copyArray(solution);  // Create a copy of the solution
            removeKDigits(puzzle, 55);           // Remove 55 digits for Hard difficulty
            saveBoardToFile(puzzle, hard);
            saveBoardToFile(solution, hardAnswer);        }

        //Generate and save 5 UnsolvableSudoku
        for (int i = 0; i < 5; i++) {
            int[][] unsolvablePuzzle = generateUnsolvableSudoku();
            saveBoardToFile(unsolvablePuzzle, unsolvable);
        }
    }
}