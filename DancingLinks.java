package soduku;
import java.util.ArrayList;
import java.util.List;

public class DancingLinks {
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

        DancingLinks solver = new DancingLinks();
        if (solver.solve(board)) {
            System.out.println("Solved Sudoku:");
            solver.printBoard(board);
        } else {
            System.out.println("No solution exists.");
        }
    }

    public boolean solve(int[][] board) {
        DLX dlx = new DLX(324); // 324 constraints for a 9x9 Sudoku
        List<int[]> rows = createExactCoverMatrix(board);

        for (int[] row : rows) {
            dlx.addRow(row);
        }

        List<int[]> solution = dlx.solve();
        if (solution == null) {
            return false; // No solution exists
        }

        // Fill the board with the solution
        for (int[] row : solution) {
            int r = row[0] / 81;
            int c = (row[0] % 81) / 9;
            int n = (row[0] % 9) + 1;
            board[r][c] = n;
        }

        return true;
    }

    private List<int[]> createExactCoverMatrix(int[][] board) {
        List<int[]> rows = new ArrayList<>();

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (board[r][c] != 0) {
                    int n = board[r][c] - 1;
                    rows.add(createRow(r, c, n));
                } else {
                    for (int n = 0; n < GRID_SIZE; n++) {
                        rows.add(createRow(r, c, n));
                    }
                }
            }
        }

        return rows;
    }

    private int[] createRow(int r, int c, int n) {
        int[] row = new int[324];
        row[r * 9 + c] = 1; // Cell constraint
        row[81 + r * 9 + n] = 1; // Row constraint
        row[162 + c * 9 + n] = 1; // Column constraint
        row[243 + ((r / 3) * 3 + (c / 3)) * 9 + n] = 1; // Subgrid constraint
        return row;
    }

    private void printBoard(int[][] board) {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                System.out.print(board[r][c] + " ");
            }
            System.out.println();
        }
    }

    // Inner class for Dancing Links
    private static class DLX {
        private final ColumnNode header;
        private final List<DataNode> solution;

        public DLX(int numColumns) {
            header = new ColumnNode();
            ColumnNode prev = header;
            for (int i = 0; i < numColumns; i++) {
                ColumnNode col = new ColumnNode();
                prev.right = col;
                col.left = prev;
                prev = col;
            }
            prev.right = header;
            header.left = prev;

            solution = new ArrayList<>();
        }

        public void addRow(int[] row) {
            DataNode first = null;
            ColumnNode col = (ColumnNode) header.right;
            for (int i = 0; i < row.length; i++) {
                if (row[i] == 1) {
                    DataNode node = new DataNode(col);
                    if (first == null) {
                        first = node;
                    }
                    col.up.down = node;
                    node.up = col.up;
                    col.up = node;
                    node.down = col;
                    col.size++;
                }
                col = (ColumnNode) col.right;
            }
        }

        public List<int[]> solve() {
    if (header.right == header) {
        // Extract the solution
        List<int[]> result = new ArrayList<>();
        for (DataNode node : solution) {
            int rowIndex = node.column[i];
            result.add(new int[]{rowIndex});
        }
        return result;
    }

    ColumnNode col = selectColumn();
    cover(col);

    for (DataNode row = col.down; row != col; row = row.down) {
        solution.add(row);
        for (DataNode node = row.right; node != row; node = node.right) {
            cover(node.column);
        }

        List<int[]> result = solve();
        if (result != null) {
            return result;
        }

        solution.remove(solution.size() - 1);
        for (DataNode node = row.left; node != row; node = node.left) {
            uncover(node.column);
        }
    }

    uncover(col);
    return null;
}

        private ColumnNode selectColumn() {
            ColumnNode col = (ColumnNode) header.right;
            for (ColumnNode node = (ColumnNode) col.right; node != header; node = (ColumnNode) node.right) {
                if (node.size < col.size) {
                    col = node;
                }
            }
            return col;
        }

        private void cover(ColumnNode col) {
            col.right.left = col.left;
            col.left.right = col.right;

            for (DataNode row = col.down; row != col; row = row.down) {
                for (DataNode node = row.right; node != row; node = node.right) {
                    node.down.up = node.up;
                    node.up.down = node.down;
                    node.column.size--;
                }
            }
        }

        private void uncover(ColumnNode col) {
            for (DataNode row = col.up; row != col; row = row.up) {
                for (DataNode node = row.left; node != row; node = node.left) {
                    node.column.size++;
                    node.down.up = node;
                    node.up.down = node;
                }
            }
            col.right.left = col;
            col.left.right = col;
        }

        private static class ColumnNode extends DataNode {
            int size;

            public ColumnNode() {
                super(null);
                size = 0;
                column = this;
            }
        }

        private static class DataNode {
            DataNode left, right, up, down;
            ColumnNode column;

            public DataNode(ColumnNode column) {
                this.column = column;
                left = right = up = down = this;
            }
        }
    }
}