package main;

import piece.*;

import javax.swing.*;
import java.awt.*;

public class GameBoard extends JLayeredPane implements Loadable {
    //LAYERS OF MAP
    private static volatile GameBoard instance;
    private final JPanel tilesLayer = new JPanel();
    private final JPanel piecesLayer = new JPanel();
    private final JPanel popupLayer = new JPanel();

    //PANEL DIMENSIONS
    private final int tileSize = 96; //in pixels
    private final int boardSize = 8; //8x8 board
    private final int boardPixelSize = tileSize * boardSize;

    //BOARD VARIABLES
    private Tile[][] tiles = new Tile[boardSize][boardSize];
    private Piece[][] pieces = new Piece[boardSize][boardSize];
    private boolean whitesMove = true;
    private boolean check = false;

    //SELECTED TILES
    private int currentColumn = -1;
    private int currentRow = -1;


    private GameBoard() {
        GameMenu gameMenu = GameMenu.getInstance();
        int posX = 20; //(gameMenu.getWidth() - boardPixelSize) / 2 - 75;
        int posY = (gameMenu.getHeight() - boardPixelSize) / 2;
        this.setBounds(posX, posY, boardPixelSize, boardPixelSize);
        this.setLayout(null);
        this.setOpaque(false);

        //setting layers of game
        //1. Board layer
        tilesLayer.setLayout(new GridBagLayout());
        tilesLayer.setOpaque(false);

        //2. Pieces layer
        piecesLayer.setLayout(null); //layout null to enable animations
        piecesLayer.setOpaque(false);

        //3. Popup windows layer
        popupLayer.setLayout(null); //layout null to spawn anywhere
        popupLayer.setOpaque(false);

        //setting bounds
        int width = (int) this.getBounds().getWidth();
        int height = (int) this.getBounds().getHeight();
        Rectangle boundsRect = new Rectangle(0, 0, width, height);
        tilesLayer.setBounds(boundsRect);
        piecesLayer.setBounds(boundsRect);
        popupLayer.setBounds(boundsRect);


        //adding to game board
        this.add(tilesLayer, Integer.valueOf(1));
        this.add(piecesLayer, Integer.valueOf(2));
        this.add(popupLayer, Integer.valueOf(3));

    }

    private void setTiles() {
        //setting base layout
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;

        //filling the map with tiles
        for (int row = 0; row < boardSize; row++) {
            for (int column = 0; column < boardSize; column++) {
                gridBagConstraints.gridy = row;
                gridBagConstraints.gridx = column;

                //adding tiles to map
                Tile createdTile;
                if ((row % 2 == 0 && column % 2 == 0) || (row % 2 == 1 && column % 2 == 1)) {
                    createdTile = new Tile("green", true, column, row);
                }
                else createdTile = new Tile("green", false, column, row);
                tilesLayer.add(createdTile, gridBagConstraints);
            }
        }
    }

    private void populateWithPieces() {
        //filling white
        new Rook(0, 7, true);
        new Knight(1, 7, true);
        new Bishop(2, 7, true);
        new Queen(3, 7, true);
        new King(4, 7, true);
        new Bishop(5, 7, true);
        new Knight(6, 7, true);
        new Rook(7, 7, true);
        for (int i = 0; i < boardSize; i++) {
            new Pawn(i, 6, true);
        }

        //filling black
        new Rook(0, 0, false);
        new Knight(1, 0, false);
        new Bishop(2, 0, false);
        new Queen(3, 0, false);
        new King(4, 0, false);
        new Bishop(5, 0, false);
        new Knight(6, 0, false);
        new Rook(7, 0, false);
        for (int i = 0; i < boardSize; i++) {
            new Pawn(i, 1, false);
        }
    }

    public boolean willBeCheck(Piece movingPiece, int newColumn, int newRow, Piece[][] pieces) {
        //copying array to make new arrangement
        Piece[][] piecesCopy = new Piece[pieces.length][pieces.length];
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                try {
                    if (pieces[i][j] == null) piecesCopy[i][j] = null;
                    else piecesCopy[i][j] = (Piece) pieces[i][j].clone();
                } catch (CloneNotSupportedException e) {
                    System.err.println("Can't make clone!");
                }
            }
        }
        piecesCopy[newRow][newColumn] = piecesCopy[movingPiece.getRow()][movingPiece.getColumn()];
        piecesCopy[newRow][newColumn].setTileLocation(newColumn, newRow);
        piecesCopy[movingPiece.getRow()][movingPiece.getColumn()] = null;

        King currentKing;
        King whiteKing = null;
        King blackKing = null;

        //iterating through all pieces to find kings of the moving piece
        for (int row = 0; row < boardSize; row++) {
            for (int column = 0; column < boardSize; column++) {
                if (piecesCopy[row][column] instanceof King && piecesCopy[row][column].isWhite()) whiteKing = ((King)piecesCopy[row][column]);
                if (piecesCopy[row][column] instanceof King && !piecesCopy[row][column].isWhite()) blackKing = ((King)piecesCopy[row][column]);
            }
        }

        //determining current king
        currentKing = movingPiece.isWhite() ? whiteKing : blackKing;
        //checking whether king(team of moving piece) will be under check if this turn happens:
        //iterating through every enemy piece to check king for safety
        if (tileInDanger(piecesCopy, currentKing.getColumn(), currentKing.getRow(), !currentKing.isWhite())) {
            return true;
        }
        else check = false;


        //if previous cycle passed without returning, then we check for check of the opponent king
        currentKing = movingPiece.isWhite() ? blackKing : whiteKing;

        //going through every enemy piece to check opponent king for safety
        if (tileInDanger(piecesCopy, currentKing.getColumn(), currentKing.getRow(), !currentKing.isWhite())) {

            //checking for checkmate
            if (isCheckmate(piecesCopy, currentKing.isWhite())) {
                //ending of the game
                UI.getInstance().createEndGameMenu(currentKing.isWhite() ? "White" : "Black");
            }

            //if possible for allied piece to attack the king in new position then setting whole game mode under check
            check = true;
        }

        //can move, but this won't make a check
        return false; //returns value for possibility of move, not check of the whole game
    }

    private boolean isCheckmate(Piece[][] piecesArr, boolean white) {
        //determining all possible moves for pieces and checking them
        for (int row = 0; row < boardSize; row++) {
            for (int column = 0; column < boardSize; column++) {
                Piece piece = piecesArr[row][column];
                if (piece != null && piece.isWhite() == white && hasLegalMove(piecesArr, piece)) return false;
            }
        }
        return true;
    }

    private boolean hasLegalMove(Piece[][] piecesArr, Piece piece) {
        for (int row = 0; row < boardSize; row++) {
            for (int column = 0; column < boardSize; column++) {
                //can we make such move
                if (piece.canMakeMove(column, row, piecesArr)) {
                    //if able, then check whether this move will get us out of check
                    if (!willBeCheck(piece, column, row, piecesArr)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean tileInDanger(Piece[][] piecesArr, int tileColumn, int tileRow, boolean white) {
        for (int row = 0; row < boardSize; row++) {
            for (int column = 0; column < boardSize; column++) {
                Piece piece = piecesArr[row][column];
                if (piece != null && piece.isWhite() == white && piece.canMakeMove(tileColumn, tileRow, piecesArr)) return true;
            }
        }
        return false;
    }

    public static JPanel createPopupPanel() {
        JPanel returnPanel = new JPanel(null);
        returnPanel.setBackground(new Color(38, 36, 33));

        return returnPanel;
    }

    private void refreshBoard() {
        tilesLayer.removeAll();
        piecesLayer.removeAll();
        popupLayer.removeAll();

        pieces = new Piece[8][8];

        SelectionMenu.getInstance().getTurnsListModel().removeAllElements();
        whitesMove = true;
        check = false;
    }


    public void startWithoutLoad() {
        refreshBoard();

        load();

        tilesLayer.revalidate(); //to recalculate all the places of tiles again
        repaint();
    }

    public void startWithLoad() {
        refreshBoard();

        pieces = Piece.readFromFile();
        //filling piecesLayer with the figures(manually, since pieces already created)
        for (Piece[] piecesRow : pieces) {
            for (Piece piece : piecesRow) {
                if (piece != null) piecesLayer.add(piece);
            }
        }
        setTiles();

        tilesLayer.revalidate();
        repaint();
    }

    @Override
    public void load() {
        setTiles();
        populateWithPieces();
        //populateWithCustomPieces();
    }

    //------------------------------------------------
    public int getTileSize() {
        return tileSize;
    }
    public Tile[][] getTiles() {
        return tiles;
    }
    public Piece[][] getPieces() {
        return pieces;
    }
    public int getCurrentRow() {
        return currentRow;
    }
    public int getCurrentColumn() {
        return currentColumn;
    }
    public JPanel getPiecesLayer() {
        return piecesLayer;
    }
    public JPanel getPopupLayer() {
        return popupLayer;
    }
    public boolean isWhitesMove() {
        return whitesMove;
    }
    public int getBoardPixelSize() {
        return boardPixelSize;
    }
    public boolean isCheck() {
        return check;
    }
    public static GameBoard getInstance() {
        if (instance == null) {
            instance = new GameBoard();
        }
        return instance;
    }

    //SETTERS

    public void setCurrentColumn(int currentColumn) {
        this.currentColumn = currentColumn;
    }
    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }
    public void changeTurn() {
        whitesMove = !whitesMove;

        String text = (whitesMove ? "White's" : "Black's") + " turn";
        SelectionMenu.getInstance().getCurrentTurnLabel().setText(text);
    }
    public void setWhitesMove(boolean whitesMove) {
        this.whitesMove = whitesMove;
    }
}
