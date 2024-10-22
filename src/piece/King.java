package piece;

import main.GameBoard;

public class King extends Piece {
    private boolean firstMove = true;

    public King(int column, int row, boolean white) {
        super(column, row, white);
    }

    @Override
    protected void loadIcon() {
        this.setIcon(makeIcon("king"));
    }

    @Override
    protected boolean isSpecificPieceMove(int column, int row, Piece[][] piecesArr) {
        //castling check
        if (firstMove) {
            if (piecesArr[row][column] instanceof Rook
                    && piecesArr[row][column].isWhite() == white
                    && ((Rook) piecesArr[row][column]).isFirstMove()) {
                return true;
            }
            else if (Math.abs(column - this.column) <= 1 && Math.abs(row - this.row) <= 1) firstMove = false; //means that we move, but do not castle
        }

        //normal check
        return Math.abs(column - this.column) <= 1 && Math.abs(row - this.row) <= 1;
    }

    @Override
    protected boolean isPathFree(int column, int row, Piece[][] piecesArr) {
        //castling check(tiles shouldn't be under attack)
        if (firstMove) {
            //determining step
            int column_step = this.column > column ? -1 : 1;

            //checking whether crossed tiles are safe to go or unfree
            for (int checkedTileColumn = this.column + column_step; checkedTileColumn != column; checkedTileColumn += column_step) {
                if (GameBoard.getInstance().tileInDanger(piecesArr, checkedTileColumn, this.row, !white) ||
                        (piecesArr[this.row][checkedTileColumn]) != null) return false;
            }

            firstMove = false; //remains unchanged till actual move or castle
            Piece.castle(this, (Rook) piecesArr[row][column]);

            //returning false, since the move will be done not the normal way
            return false;
        }

        return true;
    }

    //--------------------------------
    public boolean isFirstMove() {
        return firstMove;
    }
}
