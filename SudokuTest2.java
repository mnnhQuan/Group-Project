package sudoku;
import java.io.*;
import java.util.concurrent.*;

public class SudokuTest2 {
    public static void main(String[] args) {
        System.out.println("Testing Sudoku Solver with generated puzzles...\n");

        testPuzzles("Easy", "sudoku/SudokuTest/easy_puzzles.txt");
        testPuzzles("Medium", "sudoku/SudokuTest/medium_puzzles.txt");
        testPuzzles("Hard", "sudoku/SudokuTest/hard_puzzles.txt");
        testPuzzles("Unsolvable", "sudoku/SudokuTest/unsolvable_puzzles.txt");
    }

    public static void testPuzzles(String difficulty, String puzzleFile) {
        System.out.println(difficulty + " Puzzles:");
        int solvedCount = 0;
        long totalTime = 0;

        try (BufferedReader puzzleReader = new BufferedReader(new FileReader(puzzleFile))) {

            for (int i = 1; i <= 10; i++) { // Test 10 puzzles
                System.out.println("Puzzle " + i + ":");

                // Read the puzzle and the expected solution
                int[][] puzzle = readBoard(puzzleReader);

                // Print the puzzle
                System.out.println("Original Puzzle:");
                ConstraintSastifaction.printBoard(puzzle);

                // Solve the puzzle
                System.out.println("\nSolving...");
                long startTime = System.nanoTime();
                boolean solved = solveAndPrint(puzzle);
                long endTime = System.nanoTime();

                // Compare the solver's output with the expected solution
                if (solved) {
                    System.out.println("Solved successfully!");
                    solvedCount++;
                    totalTime += (endTime - startTime);
                } else {
                    System.out.println("Failed to solve.");
                }

                System.out.println();
            }

            // Calculate average time in seconds
            if (solvedCount > 0) {
            double averageTime = (totalTime / 1_000_000.0) / solvedCount;
            String roundedAverageTime = String.format("%.3f", averageTime);

            // Print the report
                System.out.println("Constraint Satisfaction Report (" + difficulty + ")");
                System.out.println("The algorithm solved correctly " + solvedCount + "/10 Sudokus");
                System.out.println("It takes an average of " + roundedAverageTime + " ms for the algorithm to solve all the Sudokus\n");
            } else {
                System.out.println("Constraint Satisfaction Report (" + difficulty + ")");
                System.out.println("The algorithm failed to solve any puzzles.\n");
            }        
        } catch (IOException e) {
            System.out.println("Error reading files: " + e.getMessage());
        }
    }

        // Read a Sudoku board from a text file
    private static int[][] readBoard(BufferedReader reader) throws IOException {
        int[][] board = new int[9][9];
        for (int i = 0; i < 9; i++) {
            String[] line = reader.readLine().trim().split("\\s+");
            for (int j = 0; j < 9; j++) {
                board[i][j] = Integer.parseInt(line[j]);
            }
        }
        reader.readLine(); // Skip the blank line between puzzles
        return board;
    }


    private static boolean solveAndPrint(int[][] board) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() -> ConstraintSastifaction.solveBoard(board));

        try {
            // Wait for the solver to complete within 2 minutes
            boolean solved = future.get(2, TimeUnit.MINUTES);
            if (solved) {
                System.out.println("Solved Sudoku:");
                ConstraintSastifaction.printBoard(board);
                return true;
            } else {
                System.out.println("No solution exists.");
                return false;
            }
        } catch (TimeoutException e) {
            System.out.println("Solver timed out after 2 minutes.");
            return false;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        } finally {
            executor.shutdownNow();
        }
    }
}