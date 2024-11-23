package view.test;

import controller.Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameQuickMathFrm extends JFrame {

    // Constants
    private static final String TITLE = "QuickMath Game";
    private static final Dimension PREFERRED_SIZE = new Dimension(1200, 600);
//    private static final int RIGHT_PANEL_WIDTH = 200;

    // UI Components
    private final JLabel roundLabel;
    private final JLabel targetLabel;
    private final JLabel timeLabel;
    private final JPanel buttonPanel;
    private final List<JButton> numberButtons;
    private final JButton undoButton;
    private final JButton submitButton;
    private final JTextArea gameLogArea;
    private final JLabel playerScoreLabel;
    private final JLabel opponentScoreLabel;
    private final JLabel matchScoreLabel;
    private Timer questionTimer;
    private final JPanel expressionPanel;
    private final List<JButton> expressionButtons;

    // UI Style Constants
    private static final Color NUMBER_BUTTON_COLOR = new Color(230, 230, 250);
    private static final Color OPERATOR_BUTTON_COLOR = new Color(255, 228, 225);
    private static final Color CORRECT_ANSWER_COLOR = new Color(144, 238, 144);
    private static final Color WRONG_ANSWER_COLOR = new Color(255, 182, 193);

    public GameQuickMathFrm() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(PREFERRED_SIZE);

        // Initialize components
        roundLabel = createStyledLabel("Round ", 24, Font.BOLD);
        targetLabel = createStyledLabel("Target: ", 20, Font.BOLD);
        timeLabel = createStyledLabel("Time: ", 18, Font.BOLD);
        buttonPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        numberButtons = new ArrayList<>();
        undoButton = new JButton("Undo");
        submitButton = new JButton("Submit");
        gameLogArea = createGameLogArea();
        playerScoreLabel = createStyledLabel("Your Score: 0", 14, Font.PLAIN);
        opponentScoreLabel = createStyledLabel("Opponent Score: 0", 14, Font.PLAIN);
        matchScoreLabel = createStyledLabel("Match Score: 0 - 0", 14, Font.PLAIN);
        expressionPanel = createExpressionPanel();
        expressionButtons = new ArrayList<>();

        setupLayout();
        setupActionListeners();
        setupWindowListener();

        submitButton.setEnabled(false);
        pack();
        setLocationRelativeTo(null);

//        initializeWelcomeMessage();
        // Thêm addWindowListener
    }

    public void resetGame() {
        // Reset all game states
        roundLabel.setText("Round 1");
        targetLabel.setText("Target: ");
        timeLabel.setText("Time: ");
        timeLabel.setForeground(Color.BLACK);

        // Clear expression panel
        expressionPanel.removeAll();
        expressionPanel.revalidate();
        expressionPanel.repaint();
        expressionButtons.clear();

        // Reset scores
        playerScoreLabel.setText("Your Score: 0");
        opponentScoreLabel.setText("Opponent Score: 0");
        matchScoreLabel.setText("Match Score: 0 - 0");

        // Clear game log
        gameLogArea.setText("");

        // Add initial message
        addGameLog("=== Welcome to New Game ===");
    }


    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        GameQuickMathFrm.this,
                        "Are you sure you want to exit the match?\nYou will lose if you exit.",
                        "Confirm exit",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    try {
                        // 1. First notify server about leaving room
                        Client.socketHandle.write("left-room,");

                        // 2. Send game-finish to properly cleanup game state
                        Client.socketHandle.write("game-finish," + Client.socketHandle.getClientId());

                        // 3. Wait a moment for server processing
                        Timer delayTimer = new Timer(1000, evt -> {
                            try {
                                dispose();
                                Client.closeAllViews();
                                Client.openView(Client.View.HOMEPAGE);
                            } catch (Exception ex) {
                                showError("Error when returning to homepage: " + ex.getMessage());
                            }
                        });
                        delayTimer.setRepeats(false);
                        delayTimer.start();

                    } catch (IOException ex) {
                        showError("Error when exiting game: " + ex.getMessage());
                    }
                }
            }
        });
    }

//    private void initializeWelcomeMessage() {
//        SwingUtilities.invokeLater(() -> {
//            addGameLog("=== Welcome to QuickMath Game ===");
//            addGameLog("Hãy sẵn sàng cho thử thách tính toán!");
//            addGameLog("=========================================\n");
//        });
//    }
    private JLabel createStyledLabel(String text, int fontSize, int fontStyle) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", fontStyle, fontSize));
        return label;
    }

    private JPanel createExpressionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setPreferredSize(new Dimension(getWidth(), 50));
        return panel;
    }

    private JTextArea createGameLogArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        return area;
    }

    private JButton createChoiceButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));

        boolean isNumber = text.matches("[0-9]+");
        button.setBackground(isNumber ? NUMBER_BUTTON_COLOR : OPERATOR_BUTTON_COLOR);
        button.setToolTipText(getButtonTooltip(text, isNumber));

        setupButtonListeners(button, text);
        return button;
    }

    private void setupButtonListeners(JButton button, String text) {
        button.addActionListener(e -> {
            if (button.isEnabled()) {
                addToExpression(text, button.getBackground());
                button.setEnabled(false);
                updateSubmitButtonState();
            }
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorder(UIManager.getBorder("Button.border"));
            }
        });
    }

    public void updateChoices(String[] choices) {
        if (choices == null || choices.length == 0) {
            return;
        }

        buttonPanel.removeAll();
        numberButtons.clear();

        for (String choice : choices) {
            JButton button = createChoiceButton(choice);
            numberButtons.add(button);
            buttonPanel.add(button);
        }

        // Add padding if needed
        int paddingNeeded = 10 - choices.length; // Always 2x5 grid
        for (int i = 0; i < paddingNeeded; i++) {
            JButton paddingButton = new JButton();
            paddingButton.setEnabled(false);
            paddingButton.setVisible(false);
            buttonPanel.add(paddingButton);
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void setupActionListeners() {
        undoButton.addActionListener(e -> handleUndo());
        submitButton.addActionListener(e -> {
            try {
                handleSubmit();
            } catch (IOException ex) {
                Logger.getLogger(GameQuickMathFrm.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

//    private void handleUndo() {
//        if (expressionButtons.isEmpty()) {
//            return;
//        }
//
//        JButton lastExprBtn = expressionButtons.get(expressionButtons.size() - 1);
//        String value = lastExprBtn.getText();
//
//        // Re-enable original button
//        numberButtons.stream()
//                .filter(btn -> btn.getText().equals(value))
//                .findFirst()
//                .ifPresent(btn -> btn.setEnabled(true));
//
//        expressionButtons.remove(expressionButtons.size() - 1);
//        expressionPanel.remove(lastExprBtn);
//        expressionPanel.revalidate();
//        expressionPanel.repaint();
//
//        updateSubmitButtonState();
//    }
    private void handleUndo() {
        if (expressionButtons.isEmpty()) {
            return;
        }

        JButton lastExprBtn = expressionButtons.get(expressionButtons.size() - 1);
        String lastValue = lastExprBtn.getText();

        // Re-enable tương ứng button trong numberButtons
        for (JButton btn : numberButtons) {
            if (btn.getText().equals(lastValue) && !btn.isEnabled()) {
                btn.setEnabled(true);
                break; // Chỉ enable button đầu tiên tìm thấy
            }
        }

        // Xóa button khỏi expression
        expressionButtons.remove(expressionButtons.size() - 1);
        expressionPanel.remove(lastExprBtn);
        expressionPanel.revalidate();
        expressionPanel.repaint();

        // Cập nhật trạng thái nút Submit
        updateSubmitButtonState();
    }

    private void handleSubmit() throws IOException {
        if (expressionButtons.isEmpty()) {
            return;
        }

        String expression = expressionButtons.stream()
                .map(JButton::getText)
                .reduce((a, b) -> a + " " + b)
                .orElse("");

        int target = Integer.parseInt(targetLabel.getText().replaceAll("Target: ", ""));
        Client.socketHandle.submitAnswer(expression, target);
        disableAllButtons();
        submitButton.setEnabled(false);
    }

    private String getButtonTooltip(String text, boolean isNumber) {
        if (isNumber) {
            return "Số " + text;
        }
        return switch (text) {
            case "+" ->
                "Cộng";
            case "-" ->
                "Trừ";
            case "*" ->
                "Nhân";
            default ->
                "Phép tính";
        };
    }
    // Thêm method này để cleanup resources

    public void dispose() {
        stopTimer();
        super.dispose();
    }

// Method để hiển thị thông báo kết quả
    public void showResult(boolean isCorrect, boolean wasAnswered) {
        String message = wasAnswered
                ? (isCorrect ? "Câu trả lời chính xác!" : "Câu trả lời không chính xác!")
                : "Hết thời gian!";

        addGameLog(message);

        // Đổi màu thông báo tùy kết quả
        if (wasAnswered && isCorrect) {
            addGameLog(" +10 điểm!");
        }
    }

    public void startTimer(int seconds, Consumer<Integer> onTick, Runnable onFinish) {
        if (questionTimer != null) {
            questionTimer.stop();
        }

        questionTimer = new Timer(1000, null);
        final int[] timeLeft = {seconds};

        questionTimer.addActionListener(e -> {
            timeLeft[0]--;
            onTick.accept(timeLeft[0]);

            if (timeLeft[0] <= 0) {
                questionTimer.stop();
                onFinish.run();
            }
        });

        questionTimer.start();
    }

    public void stopTimer() {
        if (questionTimer != null) {
            questionTimer.stop();
        }
    }

    public void resetForNewQuestion() {
        // Xóa tất cả expression buttons
        expressionButtons.clear();
        expressionPanel.removeAll();
        expressionPanel.revalidate();
        expressionPanel.repaint();

        enableAllButtons();
        submitButton.setEnabled(false);
    }

    private void addToExpression(String value, Color originalBgColor) {
        JButton expressionBtn = new JButton(value);
        expressionBtn.setFont(new Font("Arial", Font.BOLD, 16));
        expressionBtn.setBackground(originalBgColor);
        expressionBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        expressionBtn.setPreferredSize(new Dimension(40, 35));
        expressionButtons.add(expressionBtn);
        expressionPanel.add(expressionBtn);
        expressionPanel.revalidate();
        expressionPanel.repaint();

        // Scroll to end if needed
        expressionPanel.scrollRectToVisible(expressionBtn.getBounds());
    }

    // Cập nhật setupActionListeners cho các nút số và phép tính
    private void updateSubmitButtonState() {
        if (expressionButtons.isEmpty()) {
            submitButton.setEnabled(false);
            return;
        }

        // Kiểm tra xem phần tử cuối có phải là toán tử không
        String lastValue = expressionButtons.get(expressionButtons.size() - 1).getText();
        boolean endsWithOperator = lastValue.matches("[+\\-*/]");
        submitButton.setEnabled(!endsWithOperator);
    }

    private void setupLayout() {
        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(roundLabel, BorderLayout.CENTER);

        // Game Info Panel
        JPanel gameInfoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        gameInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel expressionContainer = new JPanel(new BorderLayout());
        expressionContainer.setBorder(BorderFactory.createTitledBorder("Expression"));

        // Wrap expressionPanel trong JScrollPane để có thể scroll nếu cần
        JScrollPane scrollPane = new JScrollPane(expressionPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        expressionContainer.add(scrollPane, BorderLayout.CENTER);

        gameInfoPanel.add(targetLabel);
        gameInfoPanel.add(timeLabel);
        gameInfoPanel.add(expressionContainer);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(gameInfoPanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        controlPanel.add(undoButton);
        controlPanel.add(submitButton);
        centerPanel.add(controlPanel, BorderLayout.SOUTH);

        // Score Panel
        JPanel scorePanel = new JPanel(new GridLayout(3, 1, 5, 5));
        scorePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Game Status"));
        scorePanel.add(playerScoreLabel);
        scorePanel.add(opponentScoreLabel);
        scorePanel.add(matchScoreLabel);

        // Game Log Panel
        JScrollPane logScrollPane = new JScrollPane(gameLogArea);
        logScrollPane.setPreferredSize(new Dimension(300, 150));

        // Right Panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(scorePanel, BorderLayout.NORTH);
        rightPanel.add(logScrollPane, BorderLayout.CENTER);
        rightPanel.setPreferredSize(new Dimension(300, getHeight()));

        // Add all panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    // Thêm method để highlight kết quả đúng/sai
    public void highlightResult(boolean isCorrect) {
        Color resultColor = isCorrect ? new Color(144, 238, 144) : new Color(255, 182, 193);
        for (JButton btn : expressionButtons) {
            btn.setBackground(resultColor);
        }
    }

    // Public methods for controller interaction
    public void updateRound(int round) {
        roundLabel.setText("Round " + round);
    }

    public void updateTarget(int target) {
        targetLabel.setText("Target: " + target);
    }

    public void updateTime(int seconds) {
        timeLabel.setText("Time: " + seconds + "s");
        if (seconds <= 5) {
            timeLabel.setForeground(Color.RED);
        } else {
            timeLabel.setForeground(Color.BLACK);
        }
    }

    public void updateScores(int playerScore, int opponentScore) {
        playerScoreLabel.setText("Your Score: " + playerScore);
        opponentScoreLabel.setText("Opponent Score: " + opponentScore);
    }

    public void updateMatchScore(int playerMatchScore, int opponentMatchScore) {
        matchScoreLabel.setText("Match Score: " + playerMatchScore + " - " + opponentMatchScore);
    }

    public void addGameLog(String message) {
        gameLogArea.append(message + "\n");
        gameLogArea.setCaretPosition(gameLogArea.getDocument().getLength());
    }

    public void enableAllButtons() {
        for (JButton button : numberButtons) {
            button.setEnabled(true);
        }
    }

    public void disableAllButtons() {
        for (JButton button : numberButtons) {
            button.setEnabled(false);
        }
    }

    public void timeUp() {
        timeLabel.setText("Time's up!");
        timeLabel.setForeground(Color.RED);
        disableAllButtons();
    }

    public void showGameOver(String resultMessage, int matchScore1, int matchScore2,
            int finalScore1, int finalScore2) {
        stopTimer(); // Dừng timer nếu đang chạy

        // Hiển thị thông báo game over với style
        addGameLog("\n" + "=".repeat(20));
        addGameLog("GAME OVER");
        addGameLog("=".repeat(20));
        addGameLog(resultMessage);
        addGameLog("\nMatch result:");
        addGameLog(String.format("Match Score: %d - %d", matchScore1, matchScore2));
        addGameLog(String.format("Final Score: %d - %d", finalScore1, finalScore2));
        addGameLog("=".repeat(20) + "\n");

        disableAllButtons();
        submitButton.setEnabled(false);

        // Hiển thị dialog thông báo
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    this,
                    resultMessage + "\n\n"
                    + String.format("Match Score: %d - %d\n", matchScore1, matchScore2)
                    + String.format("Final Score: %d - %d", finalScore1, finalScore2),
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE
            );
            try {
                dispose(); // Đóng cửa sổ game hiện tại
                Client.closeAllViews();
                Client.openView(Client.View.HOMEPAGE);
            } catch (Exception ex) {
                showError("Lỗi khi trở về trang chủ: " + ex.getMessage());
            }
        });
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }

}
