package soduku;
import java.util.Random;

class SudokuGenerator {

    // Generate a Sudoku grid with K empty cells
    static int[][] sudokuGenerator(int k) {
        int[][] grid = new int[9][9];
        fillDiagonal(grid);
        fillRemaining(grid, 0, 0);
        removeKDigits(grid, k);
        return grid;
    }

    // Generate an unsolvable Sudoku grid
    static int[][] generateUnsolvableSudoku() {
        int[][] grid = sudokuGenerator(20); // Start with a valid Sudoku
        grid[0][0] = grid[0][1]; // Introduce a conflict to make it unsolvable
        return grid;
    }

    // Fill the diagonal 3x3 matrices
    static void fillDiagonal(int[][] grid) {
        for (int i = 0; i < 9; i += 3) {
            fillBox(grid, i, i);
        }
    }

    // Fill a 3x3 matrix
    static void fillBox(int[][] grid, int row, int col) {
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
    static boolean fillRemaining(int[][] grid, int i, int j) {
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
    static boolean checkIfSafe(int[][] grid, int i, int j, int num) {
        return unUsedInRow(grid, i, num) && unUsedInCol(grid, j, num) &&
               unUsedInBox(grid, i - i % 3, j - j % 3, num);
    }

    static boolean unUsedInRow(int[][] grid, int i, int num) {
        for (int j = 0; j < 9; j++) {
            if (grid[i][j] == num) return false;
        }
        return true;
    }

    static boolean unUsedInCol(int[][] grid, int j, int num) {
        for (int i = 0; i < 9; i++) {
            if (grid[i][j] == num) return false;
        }
        return true;
    }

    static boolean unUsedInBox(int[][] grid, int rowStart, int colStart, int num) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[rowStart + i][colStart + j] == num) return false;
            }
        }
        return true;
    }

    // Remove K digits randomly to create the puzzle
    static void removeKDigits(int[][] grid, int k) {
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

    // Print the Sudoku grid
    static void printBoard(int[][] board) {
        for (int[] row : board) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    // Test generated puzzles for a specific difficulty
    private static void testGeneratedPuzzles(String difficulty, int k) {
        System.out.println(difficulty + " Puzzles:");
        for (int i = 1; i <= 5; i++) { // Test 5 puzzles for each difficulty
            System.out.println("Puzzle " + i + ":");
            int[][] puzzle = sudokuGenerator(k);
            printBoard(puzzle);
            System.out.println("\nSolving...");
            solveAndPrint(puzzle);
            System.out.println();
        }
    }

    // Test edge cases: Timeout and Unsolvable
    private static void testEdgeCases() {
        System.out.println("Edge Case: Timeout Puzzle");
        int[][] timeoutPuzzle = sudokuGenerator(60); // Very hard puzzle
        printBoard(timeoutPuzzle);
        System.out.println("\nSolving...");
        solveAndPrint(timeoutPuzzle);
        System.out.println();

        System.out.println("Edge Case: Unsolvable Puzzle");
        int[][] unsolvablePuzzle = generateUnsolvableSudoku();
        printBoard(unsolvablePuzzle);
        System.out.println("\nSolving...");
        solveAndPrint(unsolvablePuzzle);
        System.out.println();
    }
}