import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CheckersGame extends JFrame {

    char[][] board = new char[8][8];
    CellPanel[][] cells = new CellPanel[8][8];

    int selectedRow = -1, selectedCol = -1;
    boolean mustContinueCapture = false;

    String p1Name, p2Name;
    char p1Color, p2Color;
    char currentPlayer;

    int p1Score = 0, p2Score = 0;
    JLabel status;

    public CheckersGame(String p1, String p2, char c1, char c2) {
        p1Name = p1;
        p2Name = p2;
        p1Color = c1;
        p2Color = c2;
        currentPlayer = 'w'; // white starts

        setTitle("Checkers Game");
        setSize(600, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        status = new JLabel("", JLabel.CENTER);
        status.setFont(new Font("Arial", Font.BOLD, 16));
        add(status, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        add(boardPanel, BorderLayout.CENTER);

        initializeBoard();

        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                cells[r][c] = new CellPanel(r, c);
                boardPanel.add(cells[r][c]);
            }

        updateStatus();
        setVisible(true);
    }

    void updateStatus() {
        String turnName = (currentPlayer == p1Color) ? p1Name : p2Name;
        status.setText(
                turnName + "'s Turn | " +
                p1Name + ": " + p1Score +
                "   " + p2Name + ": " + p2Score
        );
    }

    void initializeBoard() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = '.';

        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 8; c++)
                if ((r + c) % 2 == 1)
                    board[r][c] = 'b';

        for (int r = 5; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if ((r + c) % 2 == 1)
                    board[r][c] = 'w';
    }

    class CellPanel extends JPanel {
        int row, col;

        CellPanel(int r, int c) {
            row = r;
            col = c;
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    handleClick(row, col);
                }
            });
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground((row + col) % 2 == 0 ? Color.WHITE : new Color(139, 69, 19));

            char p = board[row][col];
            if (p != '.') {
                g.setColor(Character.toLowerCase(p) == 'w' ? Color.WHITE : Color.BLACK);
                g.fillOval(15, 15, 40, 40);

                if (Character.isUpperCase(p)) {
                    g.setColor(Color.YELLOW);
                    g.drawOval(22, 22, 26, 26);
                }
            }

            if (row == selectedRow && col == selectedCol) {
                g.setColor(Color.RED);
                g.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
            }
        }
    }

    void handleClick(int r, int c) {

        // If chain capture is active, player must use same piece
        if (mustContinueCapture && !(r == selectedRow && c == selectedCol)
                && board[r][c] != '.') {
            return;
        }

        if (selectedRow == -1) {
            if (Character.toLowerCase(board[r][c]) == currentPlayer) {
                selectedRow = r;
                selectedCol = c;
                repaint();
            }
        } else {
            if (isValidMove(selectedRow, selectedCol, r, c)) {

                boolean wasCapture = Math.abs(r - selectedRow) == 2;
                makeMove(selectedRow, selectedCol, r, c);
                checkWin();

                if (wasCapture && hasAnotherCapture(r, c)) {
                    // Continue same player's turn
                    selectedRow = r;
                    selectedCol = c;
                    mustContinueCapture = true;
                } else {
                    // End turn
                    mustContinueCapture = false;
                    selectedRow = selectedCol = -1;
                    currentPlayer = (currentPlayer == 'w') ? 'b' : 'w';
                }
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "❌ Invalid Move!\nYou must continue capturing if possible.",
                        "Invalid Move",
                        JOptionPane.WARNING_MESSAGE
                );
                selectedRow = selectedCol = -1;
                mustContinueCapture = false;
            }
            updateStatus();
            repaint();
        }
    }

    boolean hasAnotherCapture(int r, int c) {
        int[] dr = {-2, -2, 2, 2};
        int[] dc = {-2, 2, -2, 2};

        char piece = board[r][c];
        boolean king = Character.isUpperCase(piece);

        for (int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];

            if (nr < 0 || nr > 7 || nc < 0 || nc > 7) continue;

            int mr = (r + nr) / 2;
            int mc = (c + nc) / 2;

            if (board[nr][nc] == '.' &&
                Character.toLowerCase(board[mr][mc]) != currentPlayer &&
                board[mr][mc] != '.') {

                if (king) return true;
                if (currentPlayer == 'w' && dr[i] == -2) return true;
                if (currentPlayer == 'b' && dr[i] == 2) return true;
            }
        }
        return false;
    }

    boolean isValidMove(int sr, int sc, int dr, int dc) {
        if (dr < 0 || dr > 7 || dc < 0 || dc > 7) return false;
        if (board[dr][dc] != '.') return false;

        char piece = board[sr][sc];
        boolean king = Character.isUpperCase(piece);

        int rd = dr - sr;
        int cd = dc - sc;

        if (Math.abs(cd) == 1 && !mustContinueCapture) {
            if (currentPlayer == 'w' && (rd == -1 || king)) return true;
            if (currentPlayer == 'b' && (rd == 1 || king)) return true;
        }

        if (Math.abs(cd) == 2) {
            int mr = (sr + dr) / 2;
            int mc = (sc + dc) / 2;
            char mid = board[mr][mc];

            if (Character.toLowerCase(mid) != currentPlayer && mid != '.') {
                if (currentPlayer == 'w' && (rd == -2 || king)) return true;
                if (currentPlayer == 'b' && (rd == 2 || king)) return true;
            }
        }
        return false;
    }

    void makeMove(int sr, int sc, int dr, int dc) {
        board[dr][dc] = board[sr][sc];
        board[sr][sc] = '.';

        if (Math.abs(dr - sr) == 2) {
            int mr = (sr + dr) / 2;
            int mc = (sc + dc) / 2;

            if (currentPlayer == p1Color) p1Score++;
            else p2Score++;

            board[mr][mc] = '.';
        }

        if (dr == 0 && board[dr][dc] == 'w') board[dr][dc] = 'W';
        if (dr == 7 && board[dr][dc] == 'b') board[dr][dc] = 'B';
    }

    void checkWin() {
        boolean w = false, b = false;

        for (char[] row : board)
            for (char c : row) {
                if (Character.toLowerCase(c) == 'w') w = true;
                if (Character.toLowerCase(c) == 'b') b = true;
            }

        if (!w || !b) {
            String winner = (!w && p1Color == 'b') || (!b && p1Color == 'w')
                    ? p1Name : p2Name;

            JOptionPane.showMessageDialog(this,
                    "🏆 " + winner + " Wins!\n\n" +
                    p1Name + ": " + p1Score + "\n" +
                    p2Name + ": " + p2Score);
            System.exit(0);
        }
    }
}
