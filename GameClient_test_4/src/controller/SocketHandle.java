package controller;

import model.MatchHistory;
import model.User;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import java.util.Arrays;

public class SocketHandle implements Runnable {

    private BufferedWriter outputWriter;
    private Socket socketOfClient;
    private static volatile int currentScore = 0;
    private int clientId;
    private volatile boolean hasAnswered = false;  // Thêm dòng này
    private volatile Timer currentTimer = null;
    private volatile boolean receivedResult = false;

    private int hostId = -1;

    public SocketHandle() {

    }

    public List<User> getListUser(String[] message) {
        List<User> friend = new ArrayList<>();
        for (int i = 1; i < message.length; i = i + 4) {
            friend.add(new User(Integer.parseInt(message[i]),
                    message[i + 1],
                    message[i + 2].equals("1"),
                    message[i + 3].equals("1")));
        }
        return friend;
    }

    public List<User> getListRank(String[] message) {
        List<User> friend = new ArrayList<>();
        for (int i = 1; i < message.length; i = i + 10) {
            friend.add(new User(Integer.parseInt(message[i]),
                    message[i + 1],
                    message[i + 2],
                    message[i + 3],
                    message[i + 4],
                    Integer.parseInt(message[i + 5]),
                    Integer.parseInt(message[i + 6]),
                    Integer.parseInt(message[i + 7]),
                    Integer.parseInt(message[i + 8]),
                    Integer.parseInt(message[i + 9])));
        }
        return friend;
    }

    public User getUserFromString(int start, String[] message) {
        return new User(Integer.parseInt(message[start]),
                message[start + 1],
                message[start + 2],
                message[start + 3],
                message[start + 4],
                Integer.parseInt(message[start + 5]),
                Integer.parseInt(message[start + 6]),
                Integer.parseInt(message[start + 7]),
                Integer.parseInt(message[start + 8]),
                Integer.parseInt(message[start + 9]));
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public void run() {

        try {
            socketOfClient = new Socket("127.0.0.1", 4899);
            System.out.println("Kết nối thành công!");
            outputWriter = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
            String message;

            while (true) {
                message = inputReader.readLine();
                if (message == null) {
                    break;
                }
                if (message.startsWith("Your ID: ")) {
                    setClientId(Integer.parseInt(message.split(": ")[1]));
                    System.out.println("Your client ID is: " + clientId);
                }

//                System.out.println(message);
                String[] messageSplit = message.split(",");

                if (messageSplit[0].equals("server-send-id")) {
//                    System.out.println(message);
                    int serverId = Integer.parseInt(messageSplit[1]);
                    System.out.println("serverId" + serverId);

                }
                //Đăng nhập thành công
                if (messageSplit[0].equals("login-success")) {
                    System.out.println("Đăng nhập thành công");
                    Client.closeAllViews();
                    Client.user = getUserFromString(1, messageSplit);
                    Client.openView(Client.View.HOMEPAGE);

                }
                //Thông tin tài khoản sai
                if (messageSplit[0].equals("wrong-user")) {
                    System.out.println("Thông tin sai");
                    Client.closeView(Client.View.GAME_NOTICE);
                    Client.openView(Client.View.LOGIN, messageSplit[1], messageSplit[2]);
                    Client.loginFrm.showError("Wrong username or password!");
                }
                //Tài khoản đã đăng nhập ở nơi khác
                if (messageSplit[0].equals("dupplicate-login")) {
                    System.out.println("Đã đăng nhập");
                    Client.closeView(Client.View.GAME_NOTICE);
                    Client.openView(Client.View.LOGIN, messageSplit[1], messageSplit[2]);
                    Client.loginFrm.showError("Account is logged in elsewhere.");
                }
//                //Tài khoản đã bị banned
//                if (messageSplit[0].equals("banned-user")) {
//                    Client.closeView(Client.View.GAME_NOTICE);
//                    Client.openView(Client.View.LOGIN, messageSplit[1], messageSplit[2]);
//                    Client.loginFrm.showError("Tài khoản đã bị ban");
//                }
                //Xử lý register trùng tên
                if (messageSplit[0].equals("duplicate-username")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.REGISTER);
                    JOptionPane.showMessageDialog(Client.registerFrm, "The username has already been taken by someone else");
                }
                //Xử lý hiển thị thông tin đối thủ là bạn bè/không
                if (messageSplit[0].equals("check-friend-response")) {
                    if (Client.competitorInfoFrm != null) {
                        Client.competitorInfoFrm.checkFriend((messageSplit[1].equals("1")));
                    }
                }
                //Xử lý kết quả tìm phòng từ server
                if (messageSplit[0].equals("room-fully")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                    JOptionPane.showMessageDialog(Client.homePageFrm, "Room fully!!");
                }
                // Xử lý không tìm thấy phòng trong chức năng vào phòng
                if (messageSplit[0].equals("room-not-found")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                    JOptionPane.showMessageDialog(Client.homePageFrm, "Room not found !");
                }
                // Xử lý phòng có mật khẩu sai
                if (messageSplit[0].equals("room-wrong-password")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                    JOptionPane.showMessageDialog(Client.homePageFrm, "Wrong room password!");
                }
                //Xử lý xem rank
                if (messageSplit[0].equals("return-get-rank-charts")) {
                    if (Client.rankFrm != null) {
                        Client.rankFrm.setDataToTable(getListRank(messageSplit));
                    }
                }
                //Xử lý lấy danh sách phòng
                if (messageSplit[0].equals("room-list")) {
                    Vector<String> rooms = new Vector<>();
                    Vector<String> passwords = new Vector<>();
                    for (int i = 1; i < messageSplit.length; i = i + 2) {
                        rooms.add("" + messageSplit[i]);
                        passwords.add(messageSplit[i + 1]);
                    }
                    Client.roomListFrm.updateRoomList(rooms, passwords);
                }
                if (messageSplit[0].equals("return-friend-list")) {
                    if (Client.friendListFrm != null) {
                        Client.friendListFrm.updateFriendList(getListUser(messageSplit));
                    }
                }
//                 bắt đầu vào phòng
                if (messageSplit[0].equals("go-to-room")) {
                    System.out.println("Vào phòng");
                    int roomID = Integer.parseInt(messageSplit[1]);
                    String competitorIP = messageSplit[2];
                    int isStart = Integer.parseInt(messageSplit[3]);

                    User competitor = getUserFromString(4, messageSplit);
                    // tim phong nhanh
                    if (Client.findRoomFrm != null) {
                        Client.findRoomFrm.showFoundRoom();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ex) {
                            JOptionPane.showMessageDialog(Client.findRoomFrm, "Lỗi khi sleep thread");
                        }
                    } else if (Client.waitingRoomFrm != null) {
                        Client.waitingRoomFrm.showFoundCompetitor();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ex) {
                            JOptionPane.showMessageDialog(Client.waitingRoomFrm, "Lỗi khi sleep thread");
                        }
                    }

                    Client.closeAllViews();
                    System.out.println("Đã vào phòng: " + roomID);

                    // Xử lý vào phòng và gửi client-ready
                    Client.openView(Client.View.GAME_QUICK_MATH);

                    if (Client.gameQuickMathFrm != null) {
                        System.out.println("GameQuickMathFrm successfully initialized");
                        Client.gameQuickMathFrm.resetGame();
                        // Đợi một chút để đảm bảo UI đã render xong
                        Timer readyTimer = new Timer(1000, e -> {
                            try {
                                // Gửi signal ready đến server
                                write("client-ready");
                                System.out.println("Sent client-ready signal");
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        });
                        readyTimer.setRepeats(false);
                        readyTimer.start();
                    } else {
                        System.out.println("GameQuickMathFrm initialization failed");
                    }

                }

                //Tạo phòng và server trả về tên phòng
                if (messageSplit[0].equals("your-created-room")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.WAITING_ROOM);
                    Client.waitingRoomFrm.setRoomName(messageSplit[1]);
                    if (messageSplit.length == 3) {
                        Client.waitingRoomFrm.setRoomPassword("Room's password: " + messageSplit[2]);
                    }
                }
                //Xử lý yêu cầu kết bạn tới
                if (messageSplit[0].equals("make-friend-request")) {
                    int ID = Integer.parseInt(messageSplit[1]);
                    String nickname = messageSplit[2];
                    Client.openView(Client.View.FRIEND_REQUEST, ID, nickname);
                }
                //Xử lý khi nhận được yêu cầu thách đấu
                if (messageSplit[0].equals("duel-notice")) {
                    int res = JOptionPane.showConfirmDialog(Client.getVisibleJFrame(),
                            "Battle request from " + messageSplit[2] + " (ID=" + messageSplit[1] + ")",
                            "Confirm", JOptionPane.YES_NO_OPTION);
                    if (res == JOptionPane.YES_OPTION) {
                        Client.socketHandle.write("agree-duel," + messageSplit[1]);
                    } else {
                        Client.socketHandle.write("disagree-duel," + messageSplit[1]);
                    }
                }
                //Xử lý không đồng ý thách đấu
                if (messageSplit[0].equals("disagree-duel")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                    JOptionPane.showMessageDialog(Client.homePageFrm, "Opponent refused!");
                }

                if (messageSplit[0].equals("opponent-finished")) {
                    handleOpponentFinished();
                }

                // Xử lý game tính nhanh
                processServerMessage(message);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleOpponentFinished() {
        if (Client.gameQuickMathFrm != null) {
            // Đảm bảo rằng client cũng kết thúc và trở về homepage
            SwingUtilities.invokeLater(() -> {
                Timer returnTimer = new Timer(1000, e -> {
                    try {
                        write("game-finish," + clientId);
                        Client.closeAllViews();
                        Client.openView(Client.View.HOMEPAGE);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                returnTimer.setRepeats(false);
                returnTimer.start();
            });
        }
    }

    private void processServerMessage(String message) {
        String[] parts = message.split(";");
        switch (parts[0]) {
            case "Game starting...":
                handleGameStart();
                break;
            case "question":
                handleQuestion(parts);
                break;
            case "hostId":
                hostId = Integer.parseInt(parts[1]);
                System.out.println("hostId" + hostId);
                break;
            case "question_result":
                handleQuestionResult(parts);
                break;
            case "game_over":
                handleGameOver(message);

                break;
            case "opponent_disconnected":
                System.out.println("opponent_disconnected");
                displayEndGameClientDisconnected(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                break;
            case "points_earned":
                int pointsEarned = Integer.parseInt(parts[1]);
                currentScore += pointsEarned;
                if (Client.gameQuickMathFrm != null) {
                    Client.gameQuickMathFrm.addGameLog("\nYou earned " + pointsEarned + " points!");
                    Client.gameQuickMathFrm.addGameLog("Your current score: " + currentScore);
                }
                break;
            case "final_scores":
                displayFinalScores(parts);
                break;
            case "update_user_info":
                handleUserInfoUpdate(parts);
                break;
            case "match_history":
                if (Client.matchHistoryFrm != null) {
                    handleMatchHistory(parts);
                }
                break;
            default:
                System.out.println("(default) Message from server: " + message);
        }
    }

    // Thêm method mới
//    private void handleMatchHistory(String[] parts) {
//        List<MatchHistory> history = new ArrayList<>();
//        // Skip phần tử đầu tiên (match_history)
//        String[] matches = parts[1].split(",");
//        for (String match : matches) {
//            String[] matchData = match.split(";");
//            if (matchData.length >= 14) {
//                MatchHistory matchHistory = new MatchHistory(
//                        Integer.parseInt(matchData[0]), // matchId
//                        Integer.parseInt(matchData[1]), // player1Id
//                        matchData[2], // player1Name
//                        Integer.parseInt(matchData[3]), // player2Id
//                        matchData[4], // player2Name
//                        Integer.parseInt(matchData[5]), // winnerId
//                        new Timestamp(Long.parseLong(matchData[6])), // startTime
//                        new Timestamp(Long.parseLong(matchData[7])), // endTime
//                        Integer.parseInt(matchData[8]), // matchScoreP1
//                        Integer.parseInt(matchData[9]), // matchScoreP2
//                        Integer.parseInt(matchData[10]), // finalScoreP1
//                        Integer.parseInt(matchData[11]), // finalScoreP2
//                        matchData[12].equals("1"), // isDisconnected
//                        Integer.parseInt(matchData[13]) // disconnectedPlayerId
//                );
//                history.add(matchHistory);
//            }
//        }
//
//        // Hiển thị lịch sử trong UI
//        if (Client.matchHistoryFrm != null) {
//            Client.matchHistoryFrm.displayMatchHistory(history);
//        }
//    }
//    private void handleMatchHistory(String[] parts) {
//        List<MatchHistory> history = new ArrayList<>();
//        // Skip phần tử đầu tiên (match_history)
//        String[] matches = parts[1].split(",");
//        for (String match : matches) {
//            String[] matchData = match.split(";");
//            if (matchData.length >= 14) {
//                MatchHistory matchHistory = new MatchHistory(
//                        Integer.parseInt(matchData[0]), // matchId
//                        Integer.parseInt(matchData[1]), // player1Id
//                        matchData[2], // player1Name
//                        Integer.parseInt(matchData[3]), // player2Id
//                        matchData[4], // player2Name
//                        Integer.parseInt(matchData[5]), // winnerId
//                        new Timestamp(Long.parseLong(matchData[6])), // startTime
//                        new Timestamp(Long.parseLong(matchData[7])), // endTime
//                        Integer.parseInt(matchData[8]), // matchScoreP1
//                        Integer.parseInt(matchData[9]), // matchScoreP2
//                        Integer.parseInt(matchData[10]), // finalScoreP1
//                        Integer.parseInt(matchData[11]), // finalScoreP2
//                        matchData[12].equals("1"), // isDisconnected
//                        Integer.parseInt(matchData[13]) // disconnectedPlayerId
//                );
//                history.add(matchHistory);
//            }
//        }
//
//        // Hiển thị lịch sử trong UI
//        if (Client.matchHistoryFrm != null) {
//            Client.matchHistoryFrm.displayMatchHistory(history);
//        }
//    }
//    private void handleMatchHistory(String[] parts) {
//        // Thêm kiểm tra
//        if (parts.length < 2) {
//            System.out.println("Invalid match history data");
//            return;
//        }
//
//        List<MatchHistory> history = new ArrayList<>();
//        String[] matches = parts[1].split(",");
//        System.out.println("Number of matches received: " + matches.length);
//
//        for (String match : matches) {
//            String[] matchData = match.split(";");
//            System.out.println("Match data length: " + matchData.length);
//
//            if (matchData.length >= 14) {
//                try {
//                    MatchHistory matchHistory = new MatchHistory(
//                            Integer.parseInt(matchData[0]),
//                            Integer.parseInt(matchData[1]),
//                            matchData[2],
//                            Integer.parseInt(matchData[3]),
//                            matchData[4],
//                            Integer.parseInt(matchData[5]),
//                            new Timestamp(Long.parseLong(matchData[6])),
//                            new Timestamp(Long.parseLong(matchData[7])),
//                            Integer.parseInt(matchData[8]),
//                            Integer.parseInt(matchData[9]),
//                            Integer.parseInt(matchData[10]),
//                            Integer.parseInt(matchData[11]),
//                            matchData[12].equals("1"),
//                            Integer.parseInt(matchData[13])
//                    );
//                    history.add(matchHistory);
//                } catch (Exception e) {
//                    System.out.println("Error parsing match data: " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        // Thêm log
//        System.out.println("Successfully parsed " + history.size() + " matches");
//
//        // Hiển thị lịch sử trong UI
//        if (Client.matchHistoryFrm != null) {
//            Client.matchHistoryFrm.displayMatchHistory(history);
//        }
//    }
//    private void handleMatchHistory(String[] parts) {
//        System.out.println("Raw message received: " + Arrays.toString(parts));
//
//        if (parts.length < 2) {
//            System.out.println("Invalid match history data");
//            return;
//        }
//
//        if (parts[1].equals("empty")) {
//            System.out.println("No match history found");
//            if (Client.matchHistoryFrm != null) {
//                Client.matchHistoryFrm.displayMatchHistory(new ArrayList<>());
//            }
//            return;
//        }
//
//        List<MatchHistory> history = new ArrayList<>();
//        String[] matches = parts[1].split(",");
//        System.out.println("Number of matches received: " + matches.length);
//
//        for (String match : matches) {
//            System.out.println("Processing match data: " + match);
//            String[] matchData = match.split(";");
//            System.out.println("Match data split into " + matchData.length + " parts");
//
//            if (matchData.length >= 14) {
//                try {
//                    MatchHistory matchHistory = new MatchHistory(
//                            Integer.parseInt(matchData[0]),
//                            Integer.parseInt(matchData[1]),
//                            matchData[2],
//                            Integer.parseInt(matchData[3]),
//                            matchData[4],
//                            Integer.parseInt(matchData[5]),
//                            new Timestamp(Long.parseLong(matchData[6])),
//                            new Timestamp(Long.parseLong(matchData[7])),
//                            Integer.parseInt(matchData[8]),
//                            Integer.parseInt(matchData[9]),
//                            Integer.parseInt(matchData[10]),
//                            Integer.parseInt(matchData[11]),
//                            matchData[12].equals("1"),
//                            Integer.parseInt(matchData[13])
//                    );
//                    history.add(matchHistory);
//                    System.out.println("Successfully added match to history");
//                } catch (Exception e) {
//                    System.out.println("Error parsing match data: ");
//                    e.printStackTrace();
//                }
//            } else {
//                System.out.println("Match data has insufficient parts: " + matchData.length);
//            }
//        }
//
//        System.out.println("Total matches parsed: " + history.size());
//
//        if (Client.matchHistoryFrm != null) {
//            Client.matchHistoryFrm.displayMatchHistory(history);
//        }
//    }
//    private void handleMatchHistory(String[] parts) {
//        System.out.println("Raw message received: " + Arrays.toString(parts));
//
//        if (parts.length < 2) {
//            System.out.println("Invalid match history data");
//            return;
//        }
//
//        if (parts[1].equals("empty")) {
//            System.out.println("No match history found");
//            if (Client.matchHistoryFrm != null) {
//                Client.matchHistoryFrm.displayMatchHistory(new ArrayList<>());
//            }
//            return;
//        }
//
//        List<MatchHistory> history = new ArrayList<>();
//        // Sử dụng | để split các trận đấu
//        String[] matches = parts[1].split("\\|");
//        System.out.println("Number of matches received: " + matches.length);
//
//        for (String match : matches) {
//            System.out.println("Processing match data: " + match);
//            String[] matchData = match.split(";");
//            System.out.println("Match data split into " + matchData.length + " parts");
//
//            if (matchData.length >= 14) {
//                try {
//                    MatchHistory matchHistory = new MatchHistory(
//                            Integer.parseInt(matchData[0]),
//                            Integer.parseInt(matchData[1]),
//                            matchData[2],
//                            Integer.parseInt(matchData[3]),
//                            matchData[4],
//                            Integer.parseInt(matchData[5]),
//                            new Timestamp(Long.parseLong(matchData[6])),
//                            new Timestamp(Long.parseLong(matchData[7])),
//                            Integer.parseInt(matchData[8]),
//                            Integer.parseInt(matchData[9]),
//                            Integer.parseInt(matchData[10]),
//                            Integer.parseInt(matchData[11]),
//                            matchData[12].equals("1"),
//                            Integer.parseInt(matchData[13])
//                    );
//                    history.add(matchHistory);
//                    System.out.println("Successfully added match to history");
//                } catch (Exception e) {
//                    System.out.println("Error parsing match data: ");
//                    e.printStackTrace();
//                }
//            } else {
//                System.out.println("Match data has insufficient parts: " + matchData.length);
//            }
//        }
//
//        System.out.println("Total matches parsed: " + history.size());
//
//        if (Client.matchHistoryFrm != null) {
//            Client.matchHistoryFrm.displayMatchHistory(history);
//        }
//    }
    private void handleMatchHistory(String[] parts) {
        System.out.println("Raw message received: " + Arrays.toString(parts));

        if (parts.length < 2) {
            System.out.println("Invalid match history data");
            if (Client.matchHistoryFrm != null) {
                Client.matchHistoryFrm.displayMatchHistory(new ArrayList<>());
            }
            return;
        }

        if (parts[1].equals("empty")) {
            System.out.println("No match history found");
            if (Client.matchHistoryFrm != null) {
                Client.matchHistoryFrm.displayMatchHistory(new ArrayList<>());
            }
            return;
        }

        List<MatchHistory> history = new ArrayList<>();

        // Tách toàn bộ message thành mảng các trận đấu
        String allMatches = parts[1];
        for (String matchPart : allMatches.split("\\|")) {
            System.out.println("Processing match data: " + matchPart);
            String[] matchData = matchPart.split(", ");
            System.out.println("Match data split into " + matchData.length + " parts");

            if (matchData.length >= 14) {
                try {
                    MatchHistory matchHistory = new MatchHistory(
                            Integer.parseInt(matchData[0].trim()),
                            Integer.parseInt(matchData[1].trim()),
                            matchData[2].trim(),
                            Integer.parseInt(matchData[3].trim()),
                            matchData[4].trim(),
                            Integer.parseInt(matchData[5].trim()),
                            new Timestamp(Long.parseLong(matchData[6].trim())),
                            new Timestamp(Long.parseLong(matchData[7].trim())),
                            Integer.parseInt(matchData[8].trim()),
                            Integer.parseInt(matchData[9].trim()),
                            Integer.parseInt(matchData[10].trim()),
                            Integer.parseInt(matchData[11].trim()),
                            matchData[12].trim().equals("1"),
                            Integer.parseInt(matchData[13].trim())
                    );
                    history.add(matchHistory);
                    System.out.println("Successfully added match to history");
                } catch (Exception e) {
                    System.out.println("Error parsing match data: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Match data has insufficient parts: " + matchData.length
                        + " for match: " + matchPart);
            }
        }

        System.out.println("Total matches parsed: " + history.size());

        if (Client.matchHistoryFrm != null) {
            Client.matchHistoryFrm.displayMatchHistory(history);
        }
    }

    private void handleUserInfoUpdate(String[] messageSplit) {
        if (messageSplit.length >= 12) {  // Đảm bảo đủ thông tin
            User updatedUser = new User(
                    Integer.parseInt(messageSplit[1]), // ID
                    messageSplit[2], // username
                    messageSplit[3], // password
                    messageSplit[4], // nickname
                    messageSplit[5], // avatar
                    Integer.parseInt(messageSplit[6]), // numberOfGame
                    Integer.parseInt(messageSplit[7]), // numberOfWin
                    Integer.parseInt(messageSplit[8]), // numberOfDraw
                    Integer.parseInt(messageSplit[9]) == 1,// isOnline
                    Integer.parseInt(messageSplit[10]) == 1,// isPlaying
                    Integer.parseInt(messageSplit[11]), // rank
                    Integer.parseInt(messageSplit[12]) // score
            );

            Client.user = updatedUser;

            // Cập nhật UI nếu cần
//            if (Client.gameQuickMathFrm != null) {
//                Client.gameQuickMathFrm.updateUserInfo(updatedUser);
//            }
            if (Client.homePageFrm != null) {
                Client.homePageFrm.updateUserInfo(updatedUser);
            }
        }
    }

    private void handleGameStart() {
        currentScore = 0; // Reset điểm số
        if (Client.gameQuickMathFrm != null) {
            Client.gameQuickMathFrm.resetGame(); // Thêm method mới trong GameQuickMathFrm
            Client.gameQuickMathFrm.addGameLog("=== New Game Starting ===");
            Client.gameQuickMathFrm.addGameLog("Get ready for the first question!");
        }
    }

    private void handleQuestion(String[] parts) {
        // Format: question;target;choices;round;timeAllowed

        resetQuestionState();

        hasAnswered = false;
        int target = Integer.parseInt(parts[1]);
        String choices = parts[2].trim(); // Nhận chuỗi các lựa chọn
        int round = Integer.parseInt(parts[3]);
        int timeAllowed = Integer.parseInt(parts[4]);

        // Tách chuỗi choices thành mảng các lựa chọn riêng lẻ
        String[] choiceArray = choices.split(" ");

        if (Client.gameQuickMathFrm != null) {
            Client.gameQuickMathFrm.updateRound(round);
            Client.gameQuickMathFrm.updateTarget(target);
            Client.gameQuickMathFrm.updateTime(timeAllowed);
            Client.gameQuickMathFrm.resetForNewQuestion(); // Reset UI cho câu hỏi mới
            Client.gameQuickMathFrm.updateChoices(choiceArray);
        }

        startQuestionTimer(timeAllowed);
    }

// Thêm method mới để xử lý timer
    private void startQuestionTimer(int timeAllowed) {
        hasAnswered = false;
        receivedResult = false;

        if (Client.gameQuickMathFrm != null) {
            // Hủy timer cũ nếu có
            if (currentTimer != null) {
                currentTimer.stop();
            }

            if (Client.gameQuickMathFrm != null) {
                Client.gameQuickMathFrm.startTimer(timeAllowed, (timeLeft) -> {
                    // Callback khi thời gian thay đổi
//                    Client.gameQuickMathFrm.updateTime(timeLeft);
                    if (!hasAnswered && !receivedResult) {
                        Client.gameQuickMathFrm.updateTime(timeLeft);
                    }
                }, () -> {
                    try {

//                        if (!hasAnswered && !receivedResult) {
//                            // Callback khi hết giờ
//
//                            write("answer;" + clientId + ";false");
//                            hasAnswered = true;
//                        }
                        synchronized (this) {
                            // Chỉ gửi câu trả lời nếu chưa trả lời và chưa nhận kết quả từ server
                            if (!hasAnswered && !receivedResult) {
                                write("answer;" + clientId + ";false");
                                hasAnswered = true;
                                Client.gameQuickMathFrm.timeUp();
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(SocketHandle.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                    Client.gameQuickMathFrm.timeUp();
                });
            }
        }
    }
    // Thêm method để UI có thể gửi câu trả lời

    public void submitAnswer(String answer, int target) throws IOException {
        if (hasAnswered || receivedResult) {
            return;
        }
        hasAnswered = true;

        // Hủy timer vì đã có câu trả lời
        if (currentTimer != null) {
            currentTimer.stop();
        }
        if (isValidExpression(answer, target)) {
            write("answer;" + clientId + ";true");
            if (Client.gameQuickMathFrm != null) {
                Client.gameQuickMathFrm.addGameLog("Answer submitted successfully!");
            }
        } else {
            write("answer;" + clientId + ";false");
            if (Client.gameQuickMathFrm != null) {
                Client.gameQuickMathFrm.addGameLog("Invalid answer submitted.");
            }
        }

    }

//    private void handleQuestionResult(String[] parts) {
//        try {
//            int player1Id = Integer.parseInt(parts[1]);
//            boolean player1Answered = parts[2].equals("1");
//            boolean player1Correct = parts[3].equals("1");
//            long player1Time = Long.parseLong(parts[4]);
//
//            int player2Id = Integer.parseInt(parts[5]);
//            boolean player2Answered = parts[6].equals("1");
//            boolean player2Correct = parts[7].equals("1");
//            long player2Time = Long.parseLong(parts[8]);
//
//            if (Client.gameQuickMathFrm != null) {
//                // Hiển thị kết quả trên UI
//                if (player1Id == clientId) {
//                    displayQuestionResultOnUI(player1Answered, player1Correct,
//                            player2Answered, player2Correct);
//                } else {
//                    displayQuestionResultOnUI(player2Answered, player2Correct,
//                            player1Answered, player1Correct);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    private void handleQuestionResult(String[] parts) {
        try {
            synchronized (this) {
                receivedResult = true; // đánh dấu đã nhận kết quả từ server
                if (currentTimer != null) {
                    currentTimer.stop(); // dừng timer khi có kết quả từ server
                }
            }

            int player1Id = Integer.parseInt(parts[1]);
            boolean player1Answered = parts[2].equals("1");
            boolean player1Correct = parts[3].equals("1");
            long player1Time = Long.parseLong(parts[4]);

            int player2Id = Integer.parseInt(parts[5]);
            boolean player2Answered = parts[6].equals("1");
            boolean player2Correct = parts[7].equals("1");
            long player2Time = Long.parseLong(parts[8]);

            if (Client.gameQuickMathFrm != null) {
                // Hiển thị kết quả trên UI
                if (player1Id == clientId) {
                    displayQuestionResultOnUI(player1Answered, player1Correct,
                            player2Answered, player2Correct);
                } else {
                    displayQuestionResultOnUI(player2Answered, player2Correct,
                            player1Answered, player1Correct);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetQuestionState() {
        synchronized (this) {
            hasAnswered = false;
            receivedResult = false;
            if (currentTimer != null) {
                currentTimer.stop();
                currentTimer = null;
            }
        }
    }

    private void displayQuestionResultOnUI(boolean yourAnswered, boolean yourCorrect,
            boolean oppAnswered, boolean oppCorrect) {
        if (Client.gameQuickMathFrm != null) {
            StringBuilder result = new StringBuilder("=== Question Results ===\n");

            result.append("Your result: ");
            if (!yourAnswered) {
                result.append("Did not answer in time!");
            } else {
                result.append(yourCorrect ? "CORRECT" : "INCORRECT");
            }

            result.append("\nOpponent's result: ");
            if (!oppAnswered) {
                result.append("Did not answer in time!");
            } else {
                result.append(oppCorrect ? "CORRECT" : "INCORRECT");
            }

            Client.gameQuickMathFrm.addGameLog(result.toString());
        }
    }

//    private void handleGameOver(String message) {
//        String[] parts = message.split(";");
//        if (parts.length != 6) {
//            return;
//        }
//
//        try {
//            int winnerId = Integer.parseInt(parts[1]);
//            int matchScore1 = Integer.parseInt(parts[2]);
//            int matchScore2 = Integer.parseInt(parts[3]);
//            int finalScore1 = Integer.parseInt(parts[4]);
//            int finalScore2 = Integer.parseInt(parts[5]);
//
//            if (Client.gameQuickMathFrm != null) {
//                // Hiển thị kết quả cuối cùng trên UI
//                boolean isWinner = winnerId == clientId;
//                String resultMessage = isWinner
//                        ? "Congratulations! You won!" : "Game Over! You lost!";
//
//                if (clientId == hostId) {
//                    Client.gameQuickMathFrm.showGameOver(resultMessage,
//                            matchScore1, matchScore2, finalScore1, finalScore2);
//                } else {
//                    Client.gameQuickMathFrm.showGameOver(resultMessage,
//                            matchScore2, matchScore1, finalScore2, finalScore1);
//                }
//            }
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//    }
//    private void handleGameOver(String message) {
//        String[] parts = message.split(";");
//        if (parts.length != 6) {
//            return;
//        }
//        try {
//            int winnerId = Integer.parseInt(parts[1]);
//            int matchScore1 = Integer.parseInt(parts[2]);
//            int matchScore2 = Integer.parseInt(parts[3]);
//            int finalScore1 = Integer.parseInt(parts[4]);
//            int finalScore2 = Integer.parseInt(parts[5]);
//
////        // Gửi request cập nhật trạng thái playing
////        try {
////            write("left-room"); // Gửi signal để server cập nhật trạng thái
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//            if (Client.gameQuickMathFrm != null) {
//                String resultMessage;
//                // Kiểm tra trường hợp hòa trước dựa vào matchScore
//                if (matchScore1 == matchScore2) {
//                    resultMessage = "Game Over! It's a draw!";
//                } else {
//                    // Nếu không hòa thì xác định thắng thua
//                    boolean isWinner = winnerId == clientId;
//                    resultMessage = isWinner ? "Congratulations! You won!" : "Game Over! You lost!";
//                }
//
//                // Hiển thị kết quả dựa vào hostId
//                if (clientId == hostId) {
//                    Client.gameQuickMathFrm.showGameOver(resultMessage,
//                            matchScore1, matchScore2, finalScore1, finalScore2);
//                } else {
//                    Client.gameQuickMathFrm.showGameOver(resultMessage,
//                            matchScore2, matchScore1, finalScore2, finalScore1);
//                }
//
//                // Có thể thêm hiển thị thông tin chi tiết về game
////            String detailMessage = getDetailMessage(matchScore1, matchScore2, finalScore1, finalScore2);
////            if (!detailMessage.isEmpty()) {
////                Client.gameQuickMathFrm.addGameLog(detailMessage);
//// Đợi một chút để đảm bảo UI đã cập nhật xong
//                Timer exitTimer = new Timer(1000, e -> {
//                    try {
//                        // Gửi left-room sau khi đã hiển thị kết quả
//                        write("left-room");
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                });
//                exitTimer.setRepeats(false);
//                exitTimer.start();
////            }
//            }
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//    }
    private void handleGameOver(String message) {
        String[] parts = message.split(";");
        if (parts.length != 6) {
            return;
        }
        try {
            int winnerId = Integer.parseInt(parts[1]);
            int matchScore1 = Integer.parseInt(parts[2]);
            int matchScore2 = Integer.parseInt(parts[3]);
            int finalScore1 = Integer.parseInt(parts[4]);
            int finalScore2 = Integer.parseInt(parts[5]);

            if (Client.gameQuickMathFrm != null) {
                String resultMessage;
                if (matchScore1 == matchScore2) {
                    resultMessage = "Game Over! It's a draw!";
                } else {
                    boolean isWinner = winnerId == clientId;
                    resultMessage = isWinner ? "Congratulations! You won!" : "Game Over! You lost!";
                }

                // Hiển thị kết quả game
                if (clientId == hostId) {
                    Client.gameQuickMathFrm.showGameOver(resultMessage,
                            matchScore1, matchScore2, finalScore1, finalScore2);
                } else {
                    Client.gameQuickMathFrm.showGameOver(resultMessage,
                            matchScore2, matchScore1, finalScore2, finalScore1);
                }

                // Sử dụng SwingUtilities để đảm bảo thứ tự thực hiện
                SwingUtilities.invokeLater(() -> {
                    Timer exitTimer = new Timer(2000, e -> {
                        try {
                            // Gửi message game-over riêng biệt
                            write("game-finish," + clientId);

                            Timer returnTimer = new Timer(1000, e2 -> {
                                Client.closeAllViews();
                                Client.openView(Client.View.HOMEPAGE);
                            });
                            returnTimer.setRepeats(false);
                            returnTimer.start();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                    exitTimer.setRepeats(false);
                    exitTimer.start();
                });
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void displayEndGameClientDisconnected(int bonusPoints, int bonusMatchScore) {
        currentScore = bonusPoints;
        if (Client.gameQuickMathFrm != null) {
            Client.gameQuickMathFrm.addGameLog("\n=== OPPONENT DISCONNECTED ===");
            Client.gameQuickMathFrm.addGameLog("You win by default!");
            Client.gameQuickMathFrm.addGameLog("Bonus points awarded: " + bonusPoints);
            Client.gameQuickMathFrm.addGameLog("Match score updated to: " + bonusMatchScore + " - 0");
            Client.gameQuickMathFrm.updateScores(currentScore, 0);
            Client.gameQuickMathFrm.updateMatchScore(bonusMatchScore, 0);

            // Hiển thị dialog thông báo
            SwingUtilities.invokeLater(() -> {
                Client.gameQuickMathFrm.showGameOver(
                        "You win by default! (Opponent disconnected)",
                        bonusMatchScore, 0, currentScore, 0
                );
            });
        }
    }

    private void displayFinalScores(String[] parts) {
        int player1Score = Integer.parseInt(parts[1]);
        int player2Score = Integer.parseInt(parts[2]);
        int player1MatchScore = Integer.parseInt(parts[4]);
        int player2MatchScore = Integer.parseInt(parts[5]);

        if (Client.gameQuickMathFrm != null) {
            if (clientId == hostId) {
                currentScore = player1Score;
                Client.gameQuickMathFrm.updateScores(player1Score, player2Score);
                Client.gameQuickMathFrm.updateMatchScore(player1MatchScore, player2MatchScore);

//                ));
            } else {
                currentScore = player2Score;
                Client.gameQuickMathFrm.updateScores(player2Score, player1Score);
                Client.gameQuickMathFrm.updateMatchScore(player2MatchScore, player1MatchScore);
            }
//       

        }
    }

    private boolean isValidExpression(String answer, int target) {
        if (answer == null || answer.equals("")) {
            return false;
        }
        if (!answer.matches("^\\d+(\\s*[-+*/]\\s*\\d+)*$")) {
            return false;
        }
        try {
            return evaluateExpression(answer) == target;
        } catch (Exception e) {
            return false;
        }
    }

    private int evaluateExpression(String expression) {
        String[] tokens = expression.trim().split("\\s+");
        Stack<Integer> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (String token : tokens) {
            if (token.matches("\\d+")) {
                values.push(Integer.valueOf(token));
            } else if (token.length() == 1 && "+-*".contains(token)) {
                while (!operators.isEmpty()
                        && precedence(operators.peek()) >= precedence(token.charAt(0))) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(token.charAt(0));
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private int precedence(char operator) {
        return switch (operator) {
            case '+', '-' ->
                1;
            case '*' ->
                2;
            default ->
                0;
        };
    }

    private int applyOperator(char operator, int b, int a) {
        return switch (operator) {
            case '+' ->
                a + b;
            case '-' ->
                a - b;
            case '*' ->
                a * b;
            default ->
                throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }

    public void write(String message) throws IOException {

        outputWriter.write(message);
        outputWriter.newLine();
        outputWriter.flush();

    }

    public Socket getSocketOfClient() {
        return socketOfClient;
    }

}
