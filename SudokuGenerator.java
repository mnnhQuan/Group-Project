package sudoku;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

class SudokuGenerator {

    // Generate a Sudoku grid with K empty cells
    public static int[][] sudokuGenerator(int k) {
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

    static void saveBoardToFile(int[][] puzzle, int[][] solution, String filename) {
        try (FileWriter writer = new FileWriter(filename, true)) {
            for (int[] row : puzzle) {
                for (int cell : row) {
                    writer.write(cell + " ");
                }
                writer.write("\n");
            }
            writer.write("\n");

            for (int[] row : solution) {
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

    static void saveUnsolvablePuzzleToFile(int[][] puzzle, String filename) {
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

        public static void main(String[] args) {
        System.out.println("Generating Sudoku puzzles and saving to file...");

        String filename = "sudoku_puzzles.txt";

        // Generate and save 5 Easy puzzles
        for (int i = 0; i < 10; i++) {
            int[][] puzzle = sudokuGenerator(30); // Easy: 30 cells removed
            int[][] solution = new int[9][9];
            for (int r = 0; r < 9; r++) {
                System.arraycopy(puzzle[r], 0, solution[r], 0, 9);
            }
            ConstraintSastifaction.solveBoard(solution);
            saveBoardToFile(puzzle, solution, filename);
        }

        // Generate and save 5 Medium puzzles
        for (int i = 0; i < 10; i++) {
            int[][] puzzle = sudokuGenerator(45); // Medium: 45 cells removed
            int[][] solution = new int[9][9];
            for (int r = 0; r < 9; r++) {
                System.arraycopy(puzzle[r], 0, solution[r], 0, 9);
            }
            ConstraintSastifaction.solveBoard(solution);
            saveBoardToFile(puzzle, solution, filename);
        }

        // Generate and save 5 Hard puzzles
        for (int i = 0; i < 10; i++) {
            int[][] puzzle = sudokuGenerator(55); // Hard: 55 cells removed
            int[][] solution = new int[9][9];
            for (int r = 0; r < 9; r++) {
                System.arraycopy(puzzle[r], 0, solution[r], 0, 9);
            }
            ConstraintSastifaction.solveBoard(solution);
            saveBoardToFile(puzzle, solution, filename);
        }

        for (int i = 0; i < 5; i++) {
            int[][] unsolvablePuzzle = generateUnsolvableSudoku();
            saveUnsolvablePuzzleToFile(unsolvablePuzzle, filename);
        }
    }
}