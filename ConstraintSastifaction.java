import java.util.*;
import java.util.concurrent.*;

public class ConstraintSastifaction {
    private static final int GRID_SIZE = 9;

    public static void main(String[] args) {
        int[][] board = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() -> solveBoard(board));

        try {
            // Wait for the solver to complete within 2 minutes
            boolean solved = future.get(2, TimeUnit.MINUTES);
            if (solved) {
                System.out.println("Solved Sudoku:");
                printBoard(board);
            } else {
                throw new Exception("No solution exists.");
            }
        } catch (TimeoutException e) {
            System.out.println("Solver timed out after 2 minutes");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            executor.shutdownNow();
        }
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
}