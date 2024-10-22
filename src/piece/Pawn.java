package piece;

import main.GameBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Pawn extends Piece{
    private boolean firstMove = true;
    public Pawn(int column, int row, boolean white) {
        super(column, row, white);
    }

    @Override
    protected void loadIcon() {
        this.setIcon(makeIcon("pawn"));
    }

    @Override
    protected boolean isSpecificPieceMove(int column, int row, Piece[][] piecesArr) {
        int row_step = white ? -1 : 1;

        //pawn can make two tile move during its first move
        if (firstMove) {
            if ((this.row + row_step == row && column == this.column && piecesArr[row][column] == null) ||
                    (this.row + row_step * 2 == row && column == this.column && piecesArr[row][column] == null)) {
                firstMove = false;
                return true;
            }
            else return false;
        }

        //checking whether we want to eat someone
        if ((this.row + row_step == row && this.column - 1 == column && piecesArr[row][column] != null) ||
                ((this.row + row_step == row && this.column + 1 == column && piecesArr[row][column] != null))) {
            return true;
        }

        //promotion check
        if (!(piecesArr[row][column] instanceof King) && ((this.row + row_step == row && row == 0) ||
                (this.row + row_step == row && row == GameBoard.getInstance().getTiles().length - 1))) {
            promote();
        }

        //basic check
        return this.row + row_step == row && column == this.column && piecesArr[row][column] == null;
    }

    private void promote() {
        JPanel pieceChooserPanel = GameBoard.createPopupPanel();

        int width = 600;
        int height = 200;
        int posX = (GameBoard.getInstance().getBoardPixelSize() - width) / 2;
        int posY = (GameBoard.getInstance().getBoardPixelSize() - height) / 2;

        pieceChooserPanel.setBounds(posX, posY, width, height);

        //creating possible variants to promote
        JLabel queenLabel = createLabel("queen", pieceChooserPanel);
        JLabel knightLabel = createLabel("knight", pieceChooserPanel);
        JLabel rookLabel = createLabel("rook", pieceChooserPanel);
        JLabel bishopLabel = createLabel("bishop", pieceChooserPanel);

        //adjusting labels' position
        width = tileSize;
        height = tileSize;
        int posX_step = (pieceChooserPanel.getWidth() - width * 4) / 5;
        posX = posX_step;
        posX_step += width;
        posY = (pieceChooserPanel.getHeight() - height) / 2;

        queenLabel.setBounds(posX, posY, width, height);
        posX += posX_step;
        knightLabel.setBounds(posX, posY, width, height);
        posX += posX_step;
        rookLabel.setBounds(posX, posY, width, height);
        posX += posX_step;
        bishopLabel.setBounds(posX, posY, width, height);

        //adding them to JPanel
        pieceChooserPanel.add(queenLabel);
        pieceChooserPanel.add(knightLabel);
        pieceChooserPanel.add(rookLabel);
        pieceChooserPanel.add(bishopLabel);

        //adding JPanel to the popup panel
        GameBoard.getInstance().getPopupLayer().add(pieceChooserPanel);
        GameBoard.getInstance().getPopupLayer().repaint();
    }

    private JLabel createLabel(String pictureName, JPanel ancestorPanel) {
        JLabel returnLabel = new JLabel(makeIcon(pictureName));

        //returnLabel.setOpaque(false);

        returnLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //deleting pawn and substituting it with new chosen piece
                GameBoard gb = GameBoard.getInstance();
                Piece thisPiece = gb.getPieces()[row][column];
                gb.getPiecesLayer().remove(thisPiece);
                switch (pictureName) {
                    case "queen" -> new Queen(column, row, white);
                    case "knight" -> new Knight(column, row, white);
                    case "rook" -> new Rook(column, row, white);
                    case "bishop" -> new Bishop(column, row, white);
                }

                //getting rid of ancestor JPanel
                GameBoard.getInstance().getPopupLayer().remove(ancestorPanel);
                GameBoard.getInstance().getPopupLayer().repaint();
            }
        });

        return returnLabel;
    }

    @Override
    protected boolean isPathFree(int column, int row, Piece[][] piecesArr) {
        //for first turn of the pawn, where it is able to make two tile jump
        int row_step = white ? -1 : 1;
        int currentRow = this.row + row_step;
        while (currentRow != row) {
            if (piecesArr[currentRow][column] != null) return false;
            currentRow += row_step;
        }
        return true;
    }
}
