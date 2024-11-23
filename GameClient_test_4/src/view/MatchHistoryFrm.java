package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import model.MatchHistory;
import model.User;
import controller.Client;

public class MatchHistoryFrm extends JFrame {

    private JTable tblHistory;
    private JScrollPane scrollPane;
    private JLabel lblTitle;
//    private JButton btnRefresh;
    private JPanel mainPanel;
    private JLabel lblWinRate;
    private JPanel statsPanel;

    public MatchHistoryFrm() {
        initComponents();
        setupTable();
        setupLayout();
        setupListeners();
        this.setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Set up main frame
        this.setTitle("Match History");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Initialize components
        mainPanel = new JPanel();
        lblTitle = new JLabel("Your Match History", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));

        // Stats panel
        statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        lblWinRate = new JLabel("Win Rate: 0%");
        statsPanel.add(lblWinRate);

        // Table
        String[] columns = {"Date", "Opponent", "Score", "Result", "Points Earned"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHistory = new JTable(model);
        scrollPane = new JScrollPane(tblHistory);

        // Refresh button
//        btnRefresh = new JButton("Refresh");
//        btnRefresh.setPreferredSize(new Dimension(100, 30));
    }

    private void setupTable() {
        // Set up table appearance
        tblHistory.setRowHeight(30);
        tblHistory.setIntercellSpacing(new Dimension(10, 5));
        tblHistory.setFillsViewportHeight(true);

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblHistory.getColumnCount(); i++) {
            tblHistory.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Customize header
        JTableHeader header = tblHistory.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(230, 230, 230));
        header.setForeground(Color.BLACK);

        // Set column widths
        tblHistory.getColumnModel().getColumn(0).setPreferredWidth(150); // Date
        tblHistory.getColumnModel().getColumn(1).setPreferredWidth(200); // Opponent
        tblHistory.getColumnModel().getColumn(2).setPreferredWidth(100); // Score
        tblHistory.getColumnModel().getColumn(3).setPreferredWidth(150); // Result
        tblHistory.getColumnModel().getColumn(4).setPreferredWidth(100); // Points

        // Custom cell renderer for the Result column
        tblHistory.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    switch (value.toString()) {
                        case "Won":
                            c.setForeground(new Color(0, 150, 0));
                            break;
                        case "Lost":
                            c.setForeground(new Color(200, 0, 0));
                            break;
                        case "Draw":
                            c.setForeground(new Color(128, 128, 128));
                            break;
                        case "Disconnected":
                            c.setForeground(new Color(200, 100, 0));
                            break;
                        default:
                            c.setForeground(table.getForeground());
                    }
                }
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
    }

    private void setupLayout() {
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add components to main panel
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(statsPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        buttonPanel.add(btnRefresh);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        this.add(mainPanel);
    }

    private void setupListeners() {
//        btnRefresh.addActionListener(e -> {
//            refreshMatchHistory();
//        });
    }

    private void refreshMatchHistory() {
        try {
            System.out.println("Requesting match history for user ID: " + Client.user.getID());
            Client.socketHandle.write("get_match_history," + Client.user.getID());
        } catch (Exception ex) {
            System.out.println("Error requesting match history: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to refresh match history: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

//    public void displayMatchHistory(List<MatchHistory> history) {
//        DefaultTableModel model = (DefaultTableModel) tblHistory.getModel();
//        model.setRowCount(0);
//        int totalGames = history.size();
//        int wins = 0;
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//        for (MatchHistory match : history) {
//            // Get opponent's name
//            String opponentName = match.getPlayer1Id() == Client.user.getID()
//                    ? match.getPlayer2Name()
//                    : match.getPlayer1Name();
//            // Get user's score and opponent's score
//            String score;
//            int userScore, opponentScore;
//            if (match.getPlayer1Id() == Client.user.getID()) {
//                userScore = match.getMatchScoreP1();
//                opponentScore = match.getMatchScoreP2();
//                score = match.getMatchScoreP1() + " - " + match.getMatchScoreP2();
//            } else {
//                userScore = match.getMatchScoreP2();
//                opponentScore = match.getMatchScoreP1();
//                score = match.getMatchScoreP2() + " - " + match.getMatchScoreP1();
//            }
//
//            // Determine result text - phần được sửa đổi
//            String result;
//            if (match.isIsDisconnected()) {
//                if (match.getDisconnectedPlayerId() == Client.user.getID()) {
//                    result = "Disconnected";
//                } else {
//                    result = "Won (Opponent disconnected)";
//                    wins++;
//                }
//            } else if (userScore > opponentScore) {
//                result = "Won";
//                wins++;
//            } else if (userScore == opponentScore) {
//                result = "Draw";
//                // Không tăng wins vì là hòa
//            } else {
//                result = "Lost";
//            }
//
//            // Calculate points earned
//            int pointsEarned;
//            if (match.getPlayer1Id() == Client.user.getID()) {
//                pointsEarned = match.getFinalScoreP1();
//            } else {
//                pointsEarned = match.getFinalScoreP2();
//            }
//            // Add row to table
//            model.addRow(new Object[]{
//                dateFormat.format(match.getStartTime()),
//                opponentName,
//                score,
//                result,
//                pointsEarned
//            });
//        }
//        // Update win rate
//        double winRate = totalGames > 0 ? (wins * 100.0 / totalGames) : 0;
//        lblWinRate.setText(String.format("Win Rate: %.1f%% (%d/%d)", winRate, wins, totalGames));
//        // Remind if no matches found
//        if (history.isEmpty()) {
//            model.addRow(new Object[]{"No matches found", "", "", "", ""});
//        }
//    }
    public void displayMatchHistory(List<MatchHistory> history) {
        DefaultTableModel model = (DefaultTableModel) tblHistory.getModel();
        model.setRowCount(0);
        if (history == null || history.isEmpty()) {
            model.addRow(new Object[]{"No matches found", "-", "-", "-", "-"});
            lblWinRate.setText("Win Rate: 0% (0/0)");
            return;
        }

        int totalGames = history.size();
        int wins = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (MatchHistory match : history) {
            // Xác định vai trò người chơi (player1 hay player2)
            boolean isPlayer1 = match.getPlayer1Id() == Client.user.getID();

            // Lấy thông tin trận đấu dựa trên vai trò
            String opponentName = isPlayer1 ? match.getPlayer2Name() : match.getPlayer1Name();
            int userScore = isPlayer1 ? match.getMatchScoreP1() : match.getMatchScoreP2();
            int opponentScore = isPlayer1 ? match.getMatchScoreP2() : match.getMatchScoreP1();

            // Format điểm số
            String score = userScore + " - " + opponentScore;

            // Xác định kết quả trận đấu và điểm earned
            String result;
            int pointsEarned;

            if (match.isIsDisconnected()) {
                score = opponentScore + " - " + userScore;
                if (match.getDisconnectedPlayerId() == Client.user.getID()) {
                    // Người chơi hiện tại disconnect
                    result = "Disconnected";
                    pointsEarned = 0;  // Người disconnect không được điểm
                } else {
                    // Đối thủ disconnect
                    result = "Won (Opp disconnected)";
                    wins++;
                    // Lấy điểm thực tế khi thắng do đối thủ disconnect
                    System.out.println("p1 final score: " + match.getFinalScoreP1());
                    System.out.println("p2 final score: " + match.getFinalScoreP2());
                    System.out.println("is player1" + isPlayer1);
                    pointsEarned = !isPlayer1 ? match.getFinalScoreP1() : match.getFinalScoreP2();
                }
            } else {
                // Trận đấu kết thúc bình thường
                pointsEarned = isPlayer1 ? match.getFinalScoreP1() : match.getFinalScoreP2();
                if (userScore > opponentScore) {
                    result = "Won";
                    wins++;
                } else if (userScore < opponentScore) {
                    result = "Lost";
                } else {
                    result = "Draw";
                }
            }
            if ("Disconnected".equals(result)) {
                score = "0 - 5";
                pointsEarned = 0;
            }
            
            if ("Won (Opp disconnected)".equals(result)) {
                score = "5 - 0";
                pointsEarned = 40;
            }

            // Thêm dữ liệu vào bảng
            model.addRow(new Object[]{
                dateFormat.format(match.getStartTime()),
                opponentName,
                score,
                result,
                pointsEarned
            });
        }

        // Cập nhật tỷ lệ thắng
        double winRate = totalGames > 0 ? (wins * 100.0 / totalGames) : 0;
        lblWinRate.setText(String.format("Win Rate: %.1f%% (%d/%d)", winRate, wins, totalGames));
    }
//    public void displayMatchHistory(List<MatchHistory> history) {
//        DefaultTableModel model = (DefaultTableModel) tblHistory.getModel();
//        model.setRowCount(0);
//
//        if (history == null || history.isEmpty()) {
//            model.addRow(new Object[]{"No matches found", "-", "-", "-", "-"});
//            lblWinRate.setText("Win Rate: 0% (0/0)");
//            return;
//        }
//
//        int totalGames = history.size();
//        int wins = 0;
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//
//        for (MatchHistory match : history) {
//            // Xác định vai trò người chơi (player1 hay player2)
//            boolean isPlayer1 = match.getPlayer1Id() == Client.user.getID();
//
//            // Lấy thông tin trận đấu dựa trên vai trò
//            String opponentName = isPlayer1 ? match.getPlayer2Name() : match.getPlayer1Name();
//            int userScore = isPlayer1 ? match.getMatchScoreP1() : match.getMatchScoreP2();
//            int opponentScore = isPlayer1 ? match.getMatchScoreP2() : match.getMatchScoreP1();
//            int pointsEarned = isPlayer1 ? match.getFinalScoreP1() : match.getFinalScoreP2();
//
//            // Format điểm số
//            String score = userScore + " - " + opponentScore;
//
//            // Xác định kết quả trận đấu
//            String result;
//            if (match.isIsDisconnected()) {
//                // Trường hợp có người chơi disconnect
//                if (match.getDisconnectedPlayerId() == Client.user.getID()) {
//                    result = "Disconnected";
//                } else {
//                    result = "Won (Opp disconnected)";
//                    wins++; // Thắng do đối thủ disconnect
//                }
//            } else {
//                // Trường hợp trận đấu kết thúc bình thường
//                if (userScore > opponentScore) {
//                    result = "Won";
//                    wins++;
//                } else if (userScore < opponentScore) {
//                    result = "Lost";
//                } else {
//                    result = "Draw";
//                    // Không tăng wins vì là hòa
//                }
//            }
//
//            // Thêm dữ liệu vào bảng
//            model.addRow(new Object[]{
//                dateFormat.format(match.getStartTime()),
//                opponentName,
//                score,
//                result,
//                pointsEarned
//            });
//        }
//
//        // Cập nhật tỷ lệ thắng
//        double winRate = totalGames > 0 ? (wins * 100.0 / totalGames) : 0;
//        lblWinRate.setText(String.format("Win Rate: %.1f%% (%d/%d)", winRate, wins, totalGames));
//    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            refreshMatchHistory();
        }
        super.setVisible(visible);
    }
}
