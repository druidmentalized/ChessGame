package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GameMenu extends JPanel implements Loadable{
    private static volatile GameMenu instance;
    private GameMenu() {
        this.setPreferredSize(new Dimension(1000, 900));
        this.setMaximumSize(this.getPreferredSize());
        this.setBounds(0, 0, getPreferredSize().width, getPreferredSize().height);
        this.setBackground(new Color(38, 37, 34));
        this.setLayout(null);

        int width;
        int height;
        int posX;
        int posY;

        //adding background picture
        JLabel backgroundImage = new JLabel();
        try {
            BufferedImage image = ImageIO.read(new File("res/menu/background.png"));
            backgroundImage.setIcon(new ImageIcon(image));
        } catch (IOException e) {
            e.printStackTrace();
        }
        width = backgroundImage.getIcon().getIconWidth();
        height = backgroundImage.getIcon().getIconHeight();
        posX = 0;
        posY = (this.getHeight() - height);
        backgroundImage.setBounds(posX, posY, width, height);

        //adding main label
        JLabel mainLabel = new JLabel("CHESS GAME");
        mainLabel.setFont(UI.getInstance().getOswaldMedium());
        mainLabel.setFont(mainLabel.getFont().deriveFont(90f));
        mainLabel.setForeground(Color.WHITE);

        width = 450;
        height = 200;
        posX = (this.getWidth() - width) / 2;
        posY = 0;
        mainLabel.setBounds(posX, posY, width, height);
        this.add(mainLabel);

        //adding buttons
        JButton newGameButton = createButton("NEW GAME");
        JButton loadGameButton = createButton("LOAD GAME");
        JButton quitGameButton = createButton("QUIT GAME");

        width = 300;
        height = 100;
        posX = calculatePosX(width);
        posY += height + 180;
        newGameButton.setBounds(posX, posY, width, height);
        posY += height + 100;
        loadGameButton.setBounds(posX, posY, width, height);
        posY += height + 100;
        quitGameButton.setBounds(posX, posY, width, height);


        backgroundImage.repaint();

        this.add(newGameButton);
        this.add(loadGameButton);
        this.add(quitGameButton);
        this.add(backgroundImage);
        this.revalidate();

        //-------------LISTENERS----------------------------------------
        newGameButton.addActionListener(e -> {
            Main.changeWindow("GamePanel");
            GameBoard.getInstance().startWithoutLoad();
        });
        loadGameButton.addActionListener(e -> {
            Main.changeWindow("GamePanel");
            GameBoard.getInstance().startWithLoad();
        });
        quitGameButton.addActionListener(e -> System.exit(0));
    }

    private JButton createButton(String text) {
        JButton returnButton = new JButton(text);
        returnButton.setBackground(new Color(118, 190, 79));
        returnButton.setForeground(Color.WHITE);
        returnButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        returnButton.setFont(UI.getInstance().getPoppinsSemiBold());
        returnButton.setFont(returnButton.getFont().deriveFont(20f));

        return returnButton;
    }

    private int calculatePosX(int width) {
        return (getWidth() - width) / 2;
    }

    private int calculatePosY(int height) {
        return (getHeight() - height) / 2;
    }

    public static GameMenu getInstance() {
        if (instance == null) {
            instance = new GameMenu();
        }
        return instance;
    }

    @Override
    public void load() {

    }
}
