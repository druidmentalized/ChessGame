package piece;

public class Knight extends Piece {
    public Knight(int column, int row, boolean white) {
        super(column, row, white);
    }

    @Override
    protected void loadIcon() {
        this.setIcon(makeIcon("knight"));
    }

    @Override
    protected boolean isSpecificPieceMove(int column, int row, Piece[][] piecesArr) {
        return (Math.abs(column - this.column) == 2 && Math.abs(row - this.row) == 1)
                || (Math.abs(column - this.column) == 1 && Math.abs(row - this.row) == 2);
    }

    @Override
    protected boolean isPathFree(int column, int row, Piece[][] piecesArr) {
        //not needed for this type of piece. Jumps over the pieces -> not needed
        return true;
    }
}
