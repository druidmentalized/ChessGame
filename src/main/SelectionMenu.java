package main;

import piece.Piece;
import javax.swing.*;
import java.awt.*;

public class SelectionMenu extends JPanel {
    private static volatile SelectionMenu instance;
    private DefaultListModel<String> turnsListModel = new DefaultListModel<>();
    private JList<String> turnsList = new JList<>(turnsListModel);
    private JLabel currentTurnLabel;
    private SelectionMenu() {
        this.setLayout(null);
        this.setBackground(new Color(38, 37, 34));

        int width = 412;
        int height = 850;
        int posX = (GameBoard.getInstance().getX() + GameBoard.getInstance().getWidth() + 20);
        int posY = (GameMenu.getInstance().getHeight() - height) / 2;
        this.setBounds(posX, posY, width, height);

        //label with current turn
        currentTurnLabel = new JLabel();
        currentTurnLabel.setFont(UI.getInstance().getPoppinsSemiBold());
        currentTurnLabel.setText("White's turn");
        currentTurnLabel.setFont(currentTurnLabel.getFont().deriveFont(40f));
        currentTurnLabel.setForeground(new Color(197, 196, 196));
        currentTurnLabel.setOpaque(false);
        width = 250;
        height = 100;
        posX = (this.getWidth() - width) / 2;
        posY = 50;
        currentTurnLabel.setBounds(posX, posY, width, height);
        this.add(currentTurnLabel);

        //Game history JList
        createTurnsHistory();

        //BUTTONS:
        createButtons();
    }

    private void createTurnsHistory() {
        Color backgroundsColor = new Color(42, 41, 38);
        Color darkBackgroundColor = new Color(38, 37, 34);
        Color textColor = new Color(195, 194, 193);

        turnsList.setFont(UI.getInstance().getPoppinsSemiBold());
        turnsList.setFont(turnsList.getFont().deriveFont(15f));
        turnsList.setBackground(backgroundsColor);
        turnsList.setForeground(textColor);
        turnsList.setBounds(0, 200, 412, 450);
        turnsList.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                //changes color, when selected
                if (isSelected) {
                    setBackground(darkBackgroundColor);
                    setBorder(BorderFactory.createLineBorder(darkBackgroundColor, 2));
                }
                else{
                     setBackground(backgroundsColor);
                     setBorder(null);
                }

                setForeground(textColor);
                setFont(UI.getInstance().getPoppinsSemiBold());
                setFont(getFont().deriveFont(20f));

                return this;
            }
        });
        JScrollPane jScrollPane = new JScrollPane(turnsList);
        //jScrollPane.setBackground(backgroundsColor);
        jScrollPane.setBounds(turnsList.getBounds());
        jScrollPane.setBorder(BorderFactory.createLineBorder(backgroundsColor, 2));
        this.add(jScrollPane);
    }

    private void createButtons() {
        //previous turn button
        JButton saveButton = createButton("SAVE");

        //next turn button
        JButton loadButton = createButton("LOAD");

        //Restart button
        JButton restartButton = createButton("RESTART");

        //HOME button
        JButton homeButton = createButton("MENU");

        //adding buttons
        int width = 190;
        int height = 85;
        int posX = 10;
        int posY = this.getHeight() - 190;
        int x_step = width + 10;
        int y_step = height + 10;

        saveButton.setBounds(posX, posY, width, height);
        posX += x_step;
        loadButton.setBounds(posX, posY, width, height);
        posX -= x_step;
        posY += y_step;
        restartButton.setBounds(posX, posY, width, height);
        posX += x_step;
        homeButton.setBounds(posX, posY, width, height);

        this.add(restartButton);
        this.add(saveButton);
        this.add(loadButton);
        this.add(homeButton);

        //listeners
        restartButton.addActionListener(e -> GameBoard.getInstance().startWithoutLoad());
        saveButton.addActionListener(e -> Piece.placeInFile(GameBoard.getInstance().getPieces()));
        loadButton.addActionListener(e -> GameBoard.getInstance().startWithLoad());
        homeButton.addActionListener(e -> Main.changeWindow("GameMenu"));
    }

    private JButton createButton(String text) {
        JButton returnButton = new JButton(text);
        returnButton.setFont(UI.getInstance().getPoppinsSemiBold());
        returnButton.setFont(returnButton.getFont().deriveFont(30f));
        returnButton.setBackground(new Color(50, 49, 57));
        returnButton.setForeground(new Color(197, 196, 196));
        returnButton.setBorder(BorderFactory.createLineBorder(new Color(50, 49, 57), 2));

        return returnButton;
    }

    public JLabel getCurrentTurnLabel() {
        return currentTurnLabel;
    }

    public DefaultListModel<String> getTurnsListModel() {
        return turnsListModel;
    }

    public static SelectionMenu getInstance() {
        if (instance == null) {
            instance = new SelectionMenu();
        }
        return instance;
    }
}
