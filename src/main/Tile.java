package main;

import piece.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tile extends JLabel {
    private boolean selected = false;
    private final int tileSize;
    private final ImageIcon normalIcon;
    private final ImageIcon selectedIcon;
    private final int row, column;
    public Tile(String filePath, boolean light, int column, int row) {
        //basic settings
        tileSize = GameBoard.getInstance().getTileSize();
        this.column = column;
        this.row = row;
        String tileBrightness = light ? "light/" : "dark/";
        normalIcon = makeIcon(tileBrightness + filePath);
        selectedIcon = makeIcon(tileBrightness + filePath + "_selected");
        this.setIcon(normalIcon);
        GameBoard.getInstance().getTiles()[row][column] = this;

        //adding letter or number(if needed)
        if (this.column == 0) {
            this.setLayout(null);
            JLabel boardLetter = createBoardLetterLabel(String.valueOf(8 - this.row), filePath, light);
            this.add(boardLetter);
        }
        if (this.row == GameBoard.getInstance().getTiles().length - 1) {
            this.setLayout(null);
            JLabel boardLetter = createBoardLetterLabel(String.valueOf((char)('a' + this.column)), filePath, light);
            this.add(boardLetter);
        }

        //adding listener
        this.addMouseListener(new MouseAdapter() {
            //possible positions:
            //1. Clicked on the tile w/ piece -- making this tile selected
            //2. Clicked on the tile w/o piece, when exists selected tile and able to make move -- making move
            //3. CLicked on the tile w/o piece, when exists selected tile and unable to make a move -- deselecting tile
            //4. Clicked on the tile w/o piece, when no selected tile -- nothing happens
            @Override
            public void mouseClicked(MouseEvent e) {
                GameBoard gb = GameBoard.getInstance();
                Piece[][] pieces = gb.getPieces();
                Tile[][] tiles = gb.getTiles();
                int currentRow = gb.getCurrentRow();
                int currentColumn = gb.getCurrentColumn();
                //clicked tile contains piece
                if (pieces[row][column] != null) {
                    //already exists selected tile
                    if (currentRow != -1 && currentColumn != -1) {
                        //unselecting it anyway
                        tiles[currentRow][currentColumn].toggleSelected();

                        //if selecting another tile with piece, which has another color, then trying to eat it
                        if ((pieces[currentRow][currentColumn].isWhite() != pieces[row][column].isWhite()) //different color
                                && (pieces[currentRow][currentColumn].isWhite() == GameBoard.getInstance().isWhitesMove()) //piece color corresponds to current turn color
                                && pieces[currentRow][currentColumn].canMakeMove(column, row, GameBoard.getInstance().getPieces()) //can actually move there
                                && !GameBoard.getInstance().willBeCheck(pieces[currentRow][currentColumn], column, row, GameBoard.getInstance().getPieces())) //this turn won't make a check
                        {
                            //adding piece to the "eaten pieces"
                            JPanel accounter = pieces[row][column].isWhite() ? GamePanel.getInstance().getWhiteEatenAccounting() :
                                    GamePanel.getInstance().getBlackEatenAccounting();
                            GamePanel.getInstance().addEatenToAccounter(accounter, pieces[row][column]);

                            //removing piece in the system
                            gb.getPiecesLayer().remove(pieces[row][column]);

                            //changing it visually
                            pieces[currentRow][currentColumn].moveToTile(column, row);

                            GameBoard.getInstance().changeTurn();

                        }
                        //for castle
                        else if (pieces[currentRow][currentColumn] instanceof King && ((King) pieces[currentRow][currentColumn]).isFirstMove()) {
                            pieces[currentRow][currentColumn].canMakeMove(column, row, GameBoard.getInstance().getPieces());
                        }
                        //for castle
                        else if (pieces[currentRow][currentColumn] instanceof Rook && ((Rook) pieces[currentRow][currentColumn]).isFirstMove()) {
                            pieces[currentRow][currentColumn].canMakeMove(column, row, GameBoard.getInstance().getPieces());
                        }
                        else toggleSelected();
                    }
                    else toggleSelected();
                }
                //doesn't contain piece
                else {
                    //already exists selected tile
                    if (currentRow != -1 && currentColumn != -1) {
                        //trying to move
                        if ((pieces[currentRow][currentColumn].isWhite() == GameBoard.getInstance().isWhitesMove()) //piece color corresponds to current turn color
                                && (pieces[currentRow][currentColumn].canMakeMove(column, row, GameBoard.getInstance().getPieces())) //can actually move there
                                && (!GameBoard.getInstance().willBeCheck(pieces[currentRow][currentColumn], column, row, GameBoard.getInstance().getPieces()))) //this turn won't make a check
                        {
                            pieces[currentRow][currentColumn].moveToTile(column, row);
                            GameBoard.getInstance().changeTurn();
                        }
                        tiles[currentRow][currentColumn].toggleSelected();
                    }

                    //nothing happens if no tile was selected before
                }
            }
        });
    }

    private JLabel createBoardLetterLabel(String text, String filePath, boolean light) {
        JLabel boardLetter = new JLabel(text);
        //means that this text is number
        if ((int)text.charAt(0) < 97) {
            boardLetter.setBounds(10, 10, 20, 20);
        }
        //means that it is character: a, b,...
        else {
            boardLetter.setBounds(this.getIcon().getIconWidth() - 25, this.getIcon().getIconHeight() - 25, 20, 20);
        }
        boardLetter.setFont(UI.getInstance().getPoppinsSemiBold());
        boardLetter.setFont(boardLetter.getFont().deriveFont(20f));
        boardLetter.setForeground(getAnotherColor(filePath, light));
        return boardLetter;
    }

    private Color getAnotherColor(String filePath, boolean light) {
        Color returnColor = null;
        try {
            String tileBrightness = light ? "dark/" : "light/";
            BufferedImage bufferedImage = ImageIO.read(new File("res/tiles/" + tileBrightness + filePath + ".png"));
            returnColor = new Color(bufferedImage.getRGB(1, 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnColor;
    }

    private ImageIcon makeIcon(String filePath) {
        try {
            BufferedImage image = ImageIO.read(new File("res/tiles/" + filePath + ".png"));
            Image resizedImage = image.getScaledInstance(tileSize, tileSize, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void toggleSelected() {
        if (selected) {
            this.setIcon(normalIcon);
            GameBoard.getInstance().setCurrentColumn(-1);
            GameBoard.getInstance().setCurrentRow(-1);
            selected = false;
        }
        else {
            this.setIcon(selectedIcon);
            GameBoard.getInstance().setCurrentColumn(column);
            GameBoard.getInstance().setCurrentRow(row);
            selected = true;
        }
    }

    public boolean isSelected() {
        return selected;
    }
}
