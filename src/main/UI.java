package main;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class UI {
    private static volatile UI instance;
    private Font poppinsSemiBold;
    private Font oswaldMedium;

    private UI() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/Poppins-SemiBold.ttf");
            poppinsSemiBold = Font.createFont(Font.TRUETYPE_FONT, is);
            is = getClass().getResourceAsStream("/fonts/Oswald-Medium.ttf");
            oswaldMedium = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch(IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    public void createEndGameMenu(String teamWinner) {
        JPanel endingPanel = GameBoard.createPopupPanel();

        int width = 400;
        int height = 400;
        int posX = (GameBoard.getInstance().getWidth() - width) / 2;
        int posY = (GameBoard.getInstance().getHeight() - height) / 2;
        endingPanel.setBounds(posX, posY, width, height);

        //creating <color> won label
        JLabel colorWon = createLabel(teamWinner + " won!");
        width = 276;
        height = 60;
        posX = (endingPanel.getWidth() - width) / 2;
        posY = 10;
        colorWon.setBounds(posX, posY, width, height);
        endingPanel.add(colorWon);

        //creating REMATCH button
        JButton rematchButton = createButton("Rematch");
        rematchButton.setBackground(new Color(118, 190, 79));

        //adding listener
        rematchButton.addActionListener(e -> GameBoard.getInstance().startWithoutLoad());

        //creating main menu button
        JButton mainMenuButton = createButton("Main Menu");
        mainMenuButton.setBackground(new Color(64, 64, 64));

        //adding listener
        mainMenuButton.addActionListener(e -> Main.changeWindow("GameMenu"));

        //adjusting buttons position
        width = 280;
        height = 55;
        posX = (endingPanel.getWidth() - width) / 2;
        posY = endingPanel.getHeight() / 2 + 70;

        rematchButton.setBounds(posX, posY, width, height);
        posY += height + 10;
        mainMenuButton.setBounds(posX, posY, width, height);

        //adding
        endingPanel.add(rematchButton);
        endingPanel.add(mainMenuButton);
        GameBoard.getInstance().getPopupLayer().add(endingPanel);
        GameBoard.getInstance().repaint();

    }

    private JButton createButton(String text) {
        JButton returnButton = new JButton(text);
        returnButton.setForeground(Color.WHITE);
        returnButton.setFont(poppinsSemiBold);
        returnButton.setFont(returnButton.getFont().deriveFont(35f));

        return returnButton;
    }

    private JLabel createLabel(String text) {
        JLabel returnLabel = new JLabel(text);
        returnLabel.setForeground(Color.WHITE);
        returnLabel.setFont(poppinsSemiBold);
        returnLabel.setFont(returnLabel.getFont().deriveFont(50f));

        return returnLabel;
    }

    public Font getPoppinsSemiBold() {
        return poppinsSemiBold;
    }

    public Font getOswaldMedium() {
        return oswaldMedium;
    }

    public static UI getInstance() {
        if (instance == null) {
            instance = new UI();
        }
        return instance;
    }
}
