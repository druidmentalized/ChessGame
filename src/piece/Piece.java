package piece;

import main.GameBoard;
import main.SelectionMenu;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Queue;

public abstract class Piece extends JLabel implements Cloneable, Serializable {
    private static final String saveFilePath = "res/save.txt";
    protected final boolean white;
    protected final int tileSize;
    protected int column;
    protected int row;
    public Piece(int column, int row, boolean white) {
        this.white = white;
        tileSize = GameBoard.getInstance().getTileSize();
        this.column = column;
        this.row = row;
        loadIcon();
        this.setBounds(column * tileSize, row * tileSize, getIcon().getIconWidth(), getIcon().getIconHeight());
        GameBoard.getInstance().getPieces()[row][column] = this;
        GameBoard.getInstance().getPiecesLayer().add(this);
    }

    public boolean canMakeMove(int column, int row, Piece[][] piecesArr) {
        //checking specific movement restrictions for piece
        boolean canMove = isSpecificPieceMove(column, row, piecesArr) && isPathFree(column, row, piecesArr);

        //checking if piece is going on the piece with the same color(for check validation)
        if (canMove) canMove = piecesArr[row][column] == null || piecesArr[row][column].isWhite() != this.white;
        return canMove;
    }


    public void moveToTile(int newColumn, int newRow) {
        GameBoard gb = GameBoard.getInstance();

        //changing position visually
        moveUsingThread(newColumn * GameBoard.getInstance().getTileSize(), newRow * GameBoard.getInstance().getTileSize());

        //adding this turn to turns history
        addToTurnHistory(newColumn, newRow);

        //changing position inside a program
        gb.getPieces()[this.row][this.column] = null;
        this.row = newRow;
        this.column = newColumn;
        gb.getPieces()[newRow][newColumn] = this;
    }

    private void addToTurnHistory(int newColumn, int newRow) {
        DefaultListModel<String> turnsListModel = SelectionMenu.getInstance().getTurnsListModel();
        String turn = "";
        if (white) {
            turn += getAlgebraicNotation(newColumn, newRow);
        }
        else {
            turn = turnsListModel.elementAt(turnsListModel.size() - 1).split(". {4}")[1];
            turnsListModel.remove(turnsListModel.size() - 1);
            turn += "           " + getAlgebraicNotation(newColumn, newRow);
        }

        int currentTurn = turnsListModel.size() + 1;
        turn = currentTurn + ".    " + turn;
        turnsListModel.addElement(turn);
    }

    protected void addToTurnHistory(int newColumn, int newRow, boolean isCastle) {
        if (!isCastle) {
            addToTurnHistory(newColumn, newRow);
        }
        else
        {
            DefaultListModel<String> turnsListModel = SelectionMenu.getInstance().getTurnsListModel();
            String turn = "";

            if (white) {
                turn += newColumn < 4 ? "O-O-O" : "O-O";
            }
            else {
                turn = turnsListModel.elementAt(turnsListModel.size() - 1).split(". {4}")[1];
                turnsListModel.remove(turnsListModel.size() - 1);
                turn += "           " + (newColumn < 4 ? "O-O-O" : "O-O");
            }

            int currentTurn = turnsListModel.size() + 1;
            turn = currentTurn + ".    " + turn;
            turnsListModel.addElement(turn);
        }
    }

    private String getAlgebraicNotation(int newColumn, int newRow) {
        String text = "";
        //determining piece(nothing if pawn)
        if (this instanceof Rook) text += "R";
        else if (this instanceof Bishop) text += "B";
        else if (this instanceof Queen) text += "Q";
        else if (this instanceof King) text += "K";
        else if (this instanceof Knight) text += "N";

        //checking for capture
        if (GameBoard.getInstance().getPieces()[newRow][newColumn] != null) {
            text += "x";
        }

        //determining file(column)
        text += String.valueOf((char)('a' + newColumn));

        //determining row
        text += String.valueOf(8 - newRow);

        //check for check
        if (GameBoard.getInstance().isCheck()) {
            text += "+";
        }

        return text;
    }

    protected ImageIcon makeIcon(String pieceName) {
        try {
            BufferedImage image;
            if (white) {
                image = ImageIO.read(new File("res/pieces/white/" +pieceName + ".png"));
            }
            else {
                image = ImageIO.read(new File("res/pieces/black/" +pieceName + ".png"));
            }
            Image scaledImage = image.getScaledInstance(tileSize, tileSize, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            System.err.println("Error reading piece image!");
        }
        return null;
    }

    protected static void castle(King kingPiece, Rook rookPiece) {
        int columnAverage = (kingPiece.column + rookPiece.column) / 2;
        int kingColumn = kingPiece.column > rookPiece.column ? columnAverage : columnAverage + 1;
        int rookColumn = rookPiece.column > kingPiece.column ? columnAverage : columnAverage + 1;

        //moving these two pieces manually
        kingPiece.moveUsingThread(kingColumn * GameBoard.getInstance().getTileSize(), kingPiece.row * GameBoard.getInstance().getTileSize());
        GameBoard.getInstance().getPieces()[kingPiece.row][kingPiece.column] = null;
        kingPiece.column = kingColumn;
        GameBoard.getInstance().getPieces()[kingPiece.row][kingColumn] = kingPiece;

        rookPiece.moveUsingThread(rookColumn * GameBoard.getInstance().getTileSize(), rookPiece.row * GameBoard.getInstance().getTileSize());
        GameBoard.getInstance().getPieces()[rookPiece.row][rookPiece.column] = null;
        rookPiece.column = rookColumn;
        GameBoard.getInstance().getPieces()[rookPiece.row][rookColumn] = rookPiece;

        kingPiece.addToTurnHistory(kingPiece.column, kingPiece.row, true);
        GameBoard.getInstance().changeTurn();
    }

    protected void moveUsingThread(int newX, int newY) {
        Thread movingThread = new Thread(() -> {
            boolean going = true;
            int movingTime = 100; // 0.3 seconds
            int basicSpeed = 10;
            int speedX;
            int speedY;
            int path;

            //counting what delays thread should have & speed movement speed in both directions
            path = calculateVector(getX(), getY(), newX, newY);
            int amountOfTicksNeeded = path / basicSpeed;
            int threadSleepDelay = movingTime / amountOfTicksNeeded;

            //adjusting speed for directions
            speedX = Math.abs((int)(Math.round((double)(newX - getX()) / amountOfTicksNeeded)));
            speedY = Math.abs((int)(Math.round((double)(newY - getY()) / amountOfTicksNeeded)));

            while (going) {
                int nextX = getX();
                int nextY = getY();

                //calculating new x position
                if (getX() > newX) {
                    nextX -= speedX;
                    if (nextX < newX) nextX = newX;
                }
                else if (getX() < newX) {
                    nextX += speedX;
                    if (nextX > newX) nextX = newX;
                }

                //calculating new y position
                if (getY() > newY) {
                    nextY -= speedY;
                    if (nextY < newY) nextY = newY;
                }
                else if (getY() < newY) {
                    nextY += speedY;
                    if (nextY > newY) nextY = newY;
                }

                this.setLocation(nextX, nextY);

                if (nextX == newX && nextY == newY) {
                    going = false;
                }

                try {
                    Thread.sleep(threadSleepDelay);
                } catch (InterruptedException e) {
                    //nothing
                }
            }
        });
        movingThread.start();
    }

    private int calculateVector(int x1, int y1, int x2, int y2) {
        return (int)Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static void placeInFile(Piece[][] pieces) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFilePath));

            //writing all pieces location
            for (int i = 0; i < pieces.length; i++) {
                for (int j = 0; j < pieces[i].length; j++) {
                    oos.writeObject(pieces[i][j]);
                }
            }

            //writing whose turn it is
            oos.writeObject(GameBoard.getInstance().isWhitesMove());

            //writing turns history
            DefaultListModel<String> turnsListModel = SelectionMenu.getInstance().getTurnsListModel();
            for (int i = 0; i < turnsListModel.size(); i++) {
                oos.writeObject(turnsListModel.elementAt(i));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Piece[][] readFromFile() {
        Piece[][] piecesArr = new Piece[8][8];
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFilePath));

            //reading all pieces location
            for (int i = 0; i < piecesArr.length; i++) {
                for (int j = 0; j < piecesArr[i].length; j++) {
                    piecesArr[i][j] = (Piece) (ois.readObject());
                }
            }

            //reading whose turn it is
            GameBoard.getInstance().setWhitesMove((boolean) ois.readObject());

            //reading turns history(till the end)
            DefaultListModel<String> turnsListModel = SelectionMenu.getInstance().getTurnsListModel();
            while (true) {
                String turn = (String) ois.readObject();
                turnsListModel.addElement(turn);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            //nothing, because it allows to get out from the infinite cycle meaning that we reached ending of the file
        }
        return piecesArr;
    }


    //ABSTRACT METHODS
    protected abstract void loadIcon();
    protected abstract boolean isSpecificPieceMove(int column, int row, Piece[][] piecesArr);
    protected abstract boolean isPathFree(int column, int row, Piece[][] piecesArr);

    //----------------------------------------------------------------------

    public boolean isWhite() {
        return white;
    }
    public int getColumn() {
        return column;
    }
    public int getRow() {
        return row;
    }

    public void setTileLocation(int column, int row) {
        this.column = column;
        this.row = row;
    }
}
