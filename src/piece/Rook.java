package piece;

import main.GameBoard;

public class Rook extends Piece {
    private boolean firstMove = true;

    public Rook(int column, int row, boolean white) {
        super(column, row, white);
    }

    @Override
    protected void loadIcon() {
        this.setIcon(makeIcon("rook"));
    }

    @Override
    protected boolean isSpecificPieceMove(int column, int row, Piece[][] piecesArr) {
        //castle check
        if (firstMove) {
            if (piecesArr[row][column] instanceof King
                    && piecesArr[row][column].isWhite() == white
                    && ((King) piecesArr[row][column]).isFirstMove()) {
                return true;
            }
            else if (this.column == column || this.row == row) firstMove = false;
        }

        //can move only if same row or column
        return this.column == column || this.row == row;
    }

    @Override
    protected boolean isPathFree(int column, int row, Piece[][] piecesArr) {
        //castle checking
        if (firstMove) {
            //determining step
            int column_step = this.column > column ? -1 : 1;

            //checking whether crossed tiles are safe to go or unfree
            for (int checkedTileColumn = this.column + column_step; checkedTileColumn != column; checkedTileColumn += column_step) {
                if (GameBoard.getInstance().tileInDanger(piecesArr, checkedTileColumn, this.row, !white) ||
                        (piecesArr[this.row][checkedTileColumn]) != null) return false;
            }

            firstMove = false; //remains unchanged till actual move or castle
            Piece.castle((King) piecesArr[row][column], this);

            //returning false, since the move will be done not the normal way
            return false;
        }

        int col_step = Integer.compare(column, this.column);
        int row_step = Integer.compare(row, this.row);
        int currentCol = this.column + col_step;
        int currentRow = this.row + row_step;

        while (currentCol != column || currentRow != row) {
            if (piecesArr[currentRow][currentCol] != null) {
                firstMove = true;
                return false;
            }
            currentCol += col_step;
            currentRow += row_step;
        }

        return true;
    }

    //---------------------------------------------------------

    public boolean isFirstMove() {
        return firstMove;
    }
}
