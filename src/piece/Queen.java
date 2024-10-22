package piece;

public class Queen extends Piece {
    public Queen(int column, int row, boolean white) {
        super(column, row, white);
    }

    @Override
    protected void loadIcon() {
        this.setIcon(makeIcon("queen"));
    }

    @Override
    protected boolean isSpecificPieceMove(int column, int row, Piece[][] piecesArr) {
        return (Math.abs(column - this.column) == Math.abs(row - this.row))
                || (column == this.column || row == this.row);
    }

    @Override
    protected boolean isPathFree(int column, int row, Piece[][] piecesArr) {
        int col_step = Integer.compare(column, this.column);
        int row_step = Integer.compare(row, this.row);
        int currentCol = this.column + col_step;
        int currentRow = this.row + row_step;

        while (currentCol != column || currentRow != row) {
            if (piecesArr[currentRow][currentCol] != null) return false;
            currentCol += col_step;
            currentRow += row_step;
        }

        return true;
    }
}
