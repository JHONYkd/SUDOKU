import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;

public class Sudoku {

    Color BG_MAIN = new Color(30, 30, 30);
    Color BG_PANEL = new Color(37, 37, 38);
    Color BG_FIXED = new Color(60, 60, 60);
    Color BG_EMPTY = new Color(30, 30, 30);
    Color FG_TEXT = Color.WHITE;
    Color SELECTED = new Color(0, 122, 204);
    Color ERROR = new Color(255, 85, 85);

    class Tile extends JButton {
        int r;
        int c;

        Tile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    int boardWidth = 600;
    int boardHeight = 650;

    String[] puzzle = {
            "--74916-5",
            "2---6-3-9",
            "-----7-1-",
            "-586----4",
            "--3----9-",
            "--62--187",
            "9-4-7---2",
            "67-83----",
            "81--45---"
    };

    JFrame frame = new JFrame("Sudoku");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();

    JButton numSelected = null;
    int errors = 0;

    Timer timer;
    int secondsElapsed = 0;

    boolean paused = false;

    public Sudoku() {
        frame.setSize(boardWidth, boardHeight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BG_MAIN);

        textLabel.setFont(new Font("Arial", Font.BOLD, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setForeground(FG_TEXT);

        textPanel.setBackground(BG_PANEL);
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(9, 9));
        boardPanel.setBackground(BG_PANEL);
        setupTiles();
        frame.add(boardPanel, BorderLayout.CENTER);

        buttonsPanel.setLayout(new GridLayout(1, 10));
        buttonsPanel.setBackground(BG_PANEL);
        setupButtons();
        frame.add(buttonsPanel, BorderLayout.SOUTH);

        startTimer();
        updateTimerLabel();

        frame.setVisible(true);
    }

    void setupTiles() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {

                Tile tile = new Tile(r, c);
                char ch = puzzle[r].charAt(c);

                if (ch != '-') {
                    tile.setText(String.valueOf(ch));
                    tile.setFont(new Font("Arial", Font.BOLD, 20));
                    tile.setBackground(BG_FIXED);
                    tile.setForeground(FG_TEXT);
                    tile.setEnabled(false);
                } else {
                    tile.setText("");
                    tile.setFont(new Font("Arial", Font.PLAIN, 20));
                    tile.setBackground(BG_EMPTY);
                    tile.setForeground(FG_TEXT);
                }

                if (r == 2 || r == 5) {
                    tile.setBorder(BorderFactory.createMatteBorder(1, 1, 4, 1, Color.BLACK));
                } else if (c == 2 || c == 5) {
                    tile.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 4, Color.BLACK));
                } else {
                    tile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                }

                tile.setFocusable(false);
                boardPanel.add(tile);

                tile.addActionListener(e -> {
                    if (paused || numSelected == null || !tile.getText().isEmpty()) return;

                    String value = numSelected.getText();

                    if (isValidMove(tile.r, tile.c, value)) {
                        tile.setText(value);
                        tile.setBackground(BG_FIXED);
                        tile.setForeground(FG_TEXT);
                        tile.setEnabled(false);
                        checkVictory();
                    } else {
                        errors++;
                        updateTimerLabel();
                    }
                });
            }
        }
    }

    void setupButtons() {
        for (int i = 1; i <= 9; i++) {
            JButton button = new JButton(String.valueOf(i));
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setFocusable(false);
            button.setBackground(BG_PANEL);
            button.setForeground(FG_TEXT);

            button.addActionListener(e -> {
                if (numSelected != null) numSelected.setBackground(BG_PANEL);
                numSelected = button;
                numSelected.setBackground(SELECTED);
            });

            buttonsPanel.add(button);
        }

        JButton pauseButton = new JButton("⏸ Pause");
        pauseButton.setFont(new Font("Arial", Font.BOLD, 16));
        pauseButton.setFocusable(false);
        pauseButton.setBackground(BG_PANEL);
        pauseButton.setForeground(FG_TEXT);

        pauseButton.addActionListener(e -> togglePause(pauseButton));
        buttonsPanel.add(pauseButton);
    }

    boolean isValidMove(int row, int col, String value) {
        for (int c = 0; c < 9; c++) {
            Tile t = (Tile) boardPanel.getComponent(row * 9 + c);
            if (value.equals(t.getText())) return false;
        }

        for (int r = 0; r < 9; r++) {
            Tile t = (Tile) boardPanel.getComponent(r * 9 + col);
            if (value.equals(t.getText())) return false;
        }

        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;

        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                Tile t = (Tile) boardPanel.getComponent(r * 9 + c);
                if (value.equals(t.getText())) return false;
            }
        }

        return true;
    }

    void startTimer() {
        timer = new Timer(1000, e -> {
            secondsElapsed++;
            updateTimerLabel();
        });
        timer.start();
    }

    void updateTimerLabel() {
        int min = secondsElapsed / 60;
        int sec = secondsElapsed % 60;

        textLabel.setText(
                String.format("Erros: %d | Tempo: %02d:%02d", errors, min, sec)
        );
    }

    void togglePause(JButton btn) {
        paused = !paused;

        if (paused) {
            timer.stop();
            btn.setText("▶ Continuar");
            setBoardEnabled(false);
        } else {
            timer.start();
            btn.setText("⏸ Pause");
            setBoardEnabled(true);
        }
    }

    void setBoardEnabled(boolean enabled) {
        for (Component comp : boardPanel.getComponents()) {
            Tile t = (Tile) comp;
            if (!t.getText().isEmpty() && !t.isEnabled()) continue;
            t.setEnabled(enabled);
        }
    }

    void checkVictory() {
        for (Component comp : boardPanel.getComponents()) {
            Tile t = (Tile) comp;
            if (t.getText().isEmpty()) return;
        }

        timer.stop();
        JOptionPane.showMessageDialog(frame, "Parabéns! 🎉 Você venceu!");
        setBoardEnabled(false);
    }

    public static void main(String[] args) {
        new Sudoku();
    }
}
