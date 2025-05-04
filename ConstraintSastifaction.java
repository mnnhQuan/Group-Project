package soduku;
import java.util.*;
import java.util.concurrent.*;

public class ConstraintSastifaction {
    private static final int GRID_SIZE = 9;

    public static void main(String[] args) {
        System.out.println("Testing Sudoku Solver with generated puzzles...\n");

        // Test with Easy, Medium, Hard, and Edge Cases
        testGeneratedPuzzles("Easy", 30);       // Easy: 30 cells removed
        testGeneratedPuzzles("Medium", 45);    // Medium: 45 cells removed
        testGeneratedPuzzles("Hard", 55);      // Hard: 55 cells removed
        testEdgeCases();                       // Edge cases: Timeout and Unsolvable
    }

    //Average complexity: O(n)
    private static boolean solveBoard(int[][] board) {
        // Find the most constrained cell (cell with the fewest possible values)
        int[] cell = findMostConstrainedCell(board);
        if (cell == null) {
            return true;
        }

        int row = cell[0];
        int col = cell[1];
        List<Integer> possibleValues = getPossibleValues(board, row, col);

        // Try each possible value
        for (int value : possibleValues) {
            board[row][col] = value;
            if (solveBoard(board)) {
                return true;
            }
            board[row][col] = 0;
        }

        return false;
    }

    //Average Complexity: O(n^3)
    private static int[] findMostConstrainedCell(int[][] board) {
        int[] result = null;
        int minOptions = GRID_SIZE + 1;

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] == 0) {
                    List<Integer> possibleValues = getPossibleValues(board, row, col);
                    if (possibleValues.size() < minOptions) {
                        minOptions = possibleValues.size();
                        result = new int[]{row, col};
                    }
                }
            }
        }

        return result;
    }

    //Average Complexity: O(n)
    private static List<Integer> getPossibleValues(int[][] board, int row, int col) {
        boolean[] used = new boolean[GRID_SIZE + 1];

        // Mark numbers used in the row
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[row][i] != 0) {
                used[board[row][i]] = true;
            }
        }

        // Mark numbers used in the column
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[i][col] != 0) {
                used[board[i][col]] = true;
            }
        }

        // Mark numbers used in the 3x3 subgrid
        int localBoxRow = row - row % 3;
        int localBoxCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[localBoxRow + i][localBoxCol + j] != 0) {
                    used[board[localBoxRow + i][localBoxCol + j]] = true;
                }
            }
        }

        // Collect all unused numbers
        List<Integer> possibleValues = new ArrayList<>();
        for (int num = 1; num <= GRID_SIZE; num++) {
            if (!used[num]) {
                possibleValues.add(num);
            }
        }

        return possibleValues;
    }

    private static void printBoard(int[][] board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                System.out.print(board[row][col] + " ");
            }
            System.out.println();
        }
    }

    private static void solveAndPrint(int[][] board) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() -> ConstraintSastifaction.solveBoard(board));

        try {
            // Wait for the solver to complete within 2 minutes
            boolean solved = future.get(2, TimeUnit.MINUTES);
            if (solved) {
                System.out.println("Solved Sudoku:");
                printBoard(board);
            } else {
                System.out.println("No solution exists.");
            }
        } catch (TimeoutException e) {
            System.out.println("Solver timed out after 2 minutes.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            executor.shutdownNow();
        }
    }
}