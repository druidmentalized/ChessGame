package main;

import piece.Piece;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private static volatile GamePanel instance;
    private final JPanel whiteEatenAccounting;
    private final JPanel blackEatenAccounting;

    private GamePanel() {
        this.setPreferredSize(new Dimension(GameMenu.getInstance().getSize().width + 250, GameMenu.getInstance().getSize().height));
        this.setBackground(new Color(48, 46, 43));
        this.setLayout(null);

        //adding all objects to the panel
        this.add(SelectionMenu.getInstance());
        this.add(GameBoard.getInstance());
        GameBoard.getInstance().startWithoutLoad();

        GameBoard gb = GameBoard.getInstance();
        whiteEatenAccounting = createAccounterForEatenPieces();
        whiteEatenAccounting.setBounds(20, 10, gb.getBoardPixelSize(), 45);
        this.add(whiteEatenAccounting);

        blackEatenAccounting = createAccounterForEatenPieces();
        blackEatenAccounting.setBounds(20, gb.getY() + gb.getHeight() + 10, gb.getBoardPixelSize(), 45);
        this.add(blackEatenAccounting);
    }

    private JPanel createAccounterForEatenPieces() {
        JPanel returnAccounter = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        returnAccounter.setOpaque(false);
        return returnAccounter;
    }

    public void addEatenToAccounter(JPanel accounter, Piece eatenPiece) {
        Image originalImage = ((ImageIcon) eatenPiece.getIcon()).getImage();
        Image scaledImage = originalImage.getScaledInstance(accounter.getHeight(), accounter.getHeight(), Image.SCALE_SMOOTH);
        JLabel pieceImageLabel = new JLabel(new ImageIcon(scaledImage));
        accounter.add(pieceImageLabel);
        accounter.revalidate();
        accounter.repaint();
    }

    //--------------------------------------------------------

    public JPanel getWhiteEatenAccounting() {
        return whiteEatenAccounting;
    }

    public JPanel getBlackEatenAccounting() {
        return blackEatenAccounting;
    }

    public static GamePanel getInstance() {
        if (instance == null) {
            instance = new GamePanel();
        }
        return instance;
    }
}
