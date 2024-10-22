package main;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static JFrame window = new JFrame("Chess game");
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createWindow);
    }

    private static void createWindow() {
        //tuning basic window
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.getContentPane().setLayout(new CardLayout());


        window.getContentPane().add(GameMenu.getInstance(), "GameMenu");
        GameMenu.getInstance().load();
        window.pack(); //used there in order to pack only to the main menu panel size
        window.getContentPane().add(GamePanel.getInstance(), "GamePanel");

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public static void changeWindow(String newPanel) {
        CardLayout cardLayout = (CardLayout) window.getContentPane().getLayout();
        cardLayout.show(window.getContentPane(), newPanel);

        window.revalidate();
        window.repaint();
        window.pack();
    }
}
