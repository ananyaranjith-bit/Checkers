import java.awt.*;
import javax.swing.*;

public class PlayerSetup extends JFrame {

    JTextField p1NameField, p2NameField;
    JRadioButton whiteBtn, blackBtn, randomBtn;

    public PlayerSetup() {
        setTitle("Checkers - Player Setup");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 1, 5, 5));

        add(new JLabel("Player 1 Name:", JLabel.CENTER));
        p1NameField = new JTextField();
        add(p1NameField);

        add(new JLabel("Player 2 Name:", JLabel.CENTER));
        p2NameField = new JTextField();
        add(p2NameField);

        add(new JLabel("Player 1 Color:", JLabel.CENTER));

        ButtonGroup group = new ButtonGroup();
        whiteBtn = new JRadioButton("White");
        blackBtn = new JRadioButton("Black");
        randomBtn = new JRadioButton("Random", true);

        group.add(whiteBtn);
        group.add(blackBtn);
        group.add(randomBtn);

        JPanel colorPanel = new JPanel();
        colorPanel.add(whiteBtn);
        colorPanel.add(blackBtn);
        colorPanel.add(randomBtn);
        add(colorPanel);

        JButton startBtn = new JButton("Start Game");
        add(startBtn);

        startBtn.addActionListener(e -> startGame());

        setVisible(true);
    }

    void startGame() {
        String p1 = p1NameField.getText().trim();
        String p2 = p2NameField.getText().trim();

        if (p1.isEmpty() || p2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both player names are required!");
            return;
        }

        char p1Color;
        if (whiteBtn.isSelected())
            p1Color = 'w';
        else if (blackBtn.isSelected())
            p1Color = 'b';
        else
            p1Color = Math.random() < 0.5 ? 'w' : 'b';

        char p2Color = (p1Color == 'w') ? 'b' : 'w';

        new CheckersGame(p1, p2, p1Color, p2Color);
        dispose();
    }

    public static void main(String[] args) {
        new PlayerSetup();
    }
}
