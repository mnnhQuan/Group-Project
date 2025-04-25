import java.util.concurrent.*;

public class BackTracking {
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

    private static final int GRID_SIZE = 9;

    //Average complexity O(n)
    private static boolean isNumberInRow(int[][] board, int number, int row) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[row][i] == number) {
                return true;
            }
        }
        return false;
    }

    //Average complexity O(n)
    private static boolean isNumberInColumn(int[][] board, int number, int column) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[i][column] == number) {
                return true;
            }
        }
        return false;
    }

    //Average complexity O(1) = 9 iterations
    private static boolean isNumberInBox(int[][] board, int number, int row, int column) {
        int localBoxRow = row - row % 3;
        int localBoxColumn = column - column % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i + localBoxRow][j + localBoxColumn] == number) {
                    return true;
                }
            }
        }
        return false;
    }

    //Average complexity: O(n) = O(n) + O(n) + O(1)
    private static boolean isValidPlacement(int[][] board, int numberToTry, int row, int column) {
        return !isNumberInRow(board, numberToTry, row) &&
               !isNumberInColumn(board, numberToTry, column) &&
               !isNumberInBox(board, numberToTry, row, column);
    }

    //Average complexity O(n^3)
    private static boolean solveBoard(int[][] board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int column = 0; column < GRID_SIZE; column++) {
                if (board[row][column] == 0) {
                     for (int numberToTry = 1; numberToTry <= GRID_SIZE; numberToTry++) {
                        if (isValidPlacement(board, numberToTry, row, column)) {
                            board[row][column] = numberToTry;
                            if (solveBoard(board)) {
                                return true;
                            } else {
                                board[row][column] = 0;
                            }
                        }
                     }
                     return false;
                }
            }
        }
        return true;
    }
    
    //Average complexity O(n^2)
    private static void printBoard(int[][] board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int column = 0; column < GRID_SIZE; column++) {
                System.out.print(board[row][column] + " ");
            }
            System.out.println();
        }
    }
}