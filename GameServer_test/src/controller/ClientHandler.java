/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.UserDAO;
import model.MatchHistory;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    private User user;
    private final Socket socketOfServer;
    private final int clientNumber; //id 
    private BufferedReader is;
    private BufferedWriter os;
    private boolean isClosed; // ><,isConnected
    private Room room;
    private final UserDAO userDAO;
    private final String clientIP;

    public ClientHandler(Socket socketOfServer, int clientNumber) {
        this.socketOfServer = socketOfServer;
        this.clientNumber = clientNumber;
        this.userDAO = new UserDAO();
        this.isClosed = false;
        room = null;
        if (this.socketOfServer.getInetAddress().getHostAddress().equals("127.0.0.1")) {
            clientIP = "127.0.0.1";
        } else {
            clientIP = this.socketOfServer.getInetAddress().getHostAddress();
        }
    }

    public BufferedReader getIs() {
        return is;
    }

    public BufferedWriter getOs() {
        return os;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void disconnect() {
        isClosed = true;
        try {
            if (room != null) {
                room.getGameRoom().clientDisconnected(this);
            }
            if (socketOfServer != null && !socketOfServer.isClosed()) {
                socketOfServer.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client connection: " + e.getMessage());
        }
    }

    public String getStringFromUser(User user1) {
        return user1.getID() + "," + user1.getUsername()
                + "," + user1.getPassword() + "," + user1.getNickname() + ","
                + user1.getAvatar() + "," + user1.getNumberOfGame() + ","
                + user1.getNumberOfWin() + "," + user1.getNumberOfDraw() + "," + user1.getRank() + "," + user1.getScore();
    }

    // vào phòng và gửi đến client thông tin đối thủ:
    // gui
    public void goToOwnRoom() throws IOException {

        // Gửi signal "pre-room-setup" trước
        write("pre-room-setup");
        room.getCompetitor(this.clientNumber).write("pre-room-setup");

        // Đợi một chút để đảm bảo client nhận được signal
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        write("go-to-room," + room.getId() + "," + room.getCompetitor(this.getClientNumber()).getClientIP() + ",1,"
                + getStringFromUser(room.getCompetitor(this.getClientNumber()).getUser()));
        room.getCompetitor(this.clientNumber).write("go-to-room," + room.getId() + "," + this.clientIP + ",0," + getStringFromUser(user));
    }

    public void goToPartnerRoom() throws IOException {
        // Gửi signal "pre-room-setup" trước
        write("pre-room-setup");
        room.getCompetitor(this.clientNumber).write("pre-room-setup");

        // Đợi một chút để đảm bảo client nhận được signal
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // đi vào phòng đối thủ thì mình đi sau
        write("go-to-room," + room.getId() + "," + room.getCompetitor(this.getClientNumber()).getClientIP() + ",0," + getStringFromUser(room.getCompetitor(this.getClientNumber()).getUser()));
        room.getCompetitor(this.clientNumber).write("go-to-room," + room.getId() + "," + this.clientIP + ",1," + getStringFromUser(user));

    }

    public void write(String message) {
        try {
            os.write(message);
            os.newLine();
            os.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    private String formatMatchHistory(List<MatchHistory> matchHistory) {
//        StringBuilder sb = new StringBuilder("match_history;");
//
//        if (matchHistory.isEmpty()) {
//            System.out.println("No matches found");
//            return "match_history;empty";  // Trả về marker rõ ràng khi không có dữ liệu
//        }
//
//        System.out.println("Formatting " + matchHistory.size() + " matches");
//        for (MatchHistory match : matchHistory) {
//            // In ra thông tin của từng trận để debug
//            System.out.println("Processing match ID: " + match.getMatchId());
//
//            String matchData = String.format("%d;%d;%s;%d;%s;%d;%d;%d;%d;%d;%d;%d;%s;%d,",
//                    match.getMatchId(),
//                    match.getPlayer1Id(),
//                    match.getPlayer1Name(),
//                    match.getPlayer2Id(),
//                    match.getPlayer2Name(),
//                    match.getWinnerId(),
//                    match.getStartTime().getTime(),
//                    match.getEndTime().getTime(),
//                    match.getMatchScoreP1(),
//                    match.getMatchScoreP2(),
//                    match.getFinalScoreP1(),
//                    match.getFinalScoreP2(),
//                    match.isIsDisconnected() ? "1" : "0",
//                    match.getDisconnectedPlayerId()
//            );
//
//            sb.append(matchData);
//            System.out.println("Added match data: " + matchData);
//        }
//
//        String result = sb.toString();
//        System.out.println("Final formatted string: " + result);
//        return result;
//    }
//    private String formatMatchHistory(List<MatchHistory> matchHistory) {
//        StringBuilder sb = new StringBuilder("match_history;");
//
//        if (matchHistory.isEmpty()) {
//            return "match_history;empty";
//        }
//
//        // Tạo mảng các trận đấu
//        List<String> matchStrings = new ArrayList<>();
//
//        for (MatchHistory match : matchHistory) {
//            // Format từng trận đấu thành một string riêng
//            String matchData = String.format("%d;%d;%s;%d;%s;%d;%d;%d;%d;%d;%d;%d;%s;%d",
//                    match.getMatchId(),
//                    match.getPlayer1Id(),
//                    match.getPlayer1Name(),
//                    match.getPlayer2Id(),
//                    match.getPlayer2Name(),
//                    match.getWinnerId(),
//                    match.getStartTime().getTime(),
//                    match.getEndTime().getTime(),
//                    match.getMatchScoreP1(),
//                    match.getMatchScoreP2(),
//                    match.getFinalScoreP1(),
//                    match.getFinalScoreP2(),
//                    match.isIsDisconnected() ? "1" : "0",
//                    match.getDisconnectedPlayerId()
//            );
//            matchStrings.add(matchData);
//        }
//
//        // Nối các trận đấu bằng ký tự phân cách đặc biệt (ví dụ: |)
//        sb.append(String.join("|", matchStrings));
//
//        return sb.toString();
//    }
    private String formatMatchHistory(List<MatchHistory> matchHistory) {
        StringBuilder sb = new StringBuilder("match_history;");

        if (matchHistory.isEmpty()) {
            return "match_history;empty";
        }

        List<String> matchStrings = new ArrayList<>();
        for (MatchHistory match : matchHistory) {
            StringBuilder matchBuilder = new StringBuilder();
            matchBuilder.append(match.getMatchId()).append(", ")
                    .append(match.getPlayer1Id()).append(", ")
                    .append(match.getPlayer1Name()).append(", ")
                    .append(match.getPlayer2Id()).append(", ")
                    .append(match.getPlayer2Name()).append(", ")
                    .append(match.getWinnerId()).append(", ")
                    .append(match.getStartTime().getTime()).append(", ")
                    .append(match.getEndTime().getTime()).append(", ")
                    .append(match.getMatchScoreP1()).append(", ")
                    .append(match.getMatchScoreP2()).append(", ")
                    .append(match.getFinalScoreP1()).append(", ")
                    .append(match.getFinalScoreP2()).append(", ")
                    .append(match.isIsDisconnected() ? "1" : "0").append(", ")
                    .append(match.getDisconnectedPlayerId());
            matchStrings.add(matchBuilder.toString());
        }

        sb.append(String.join("|", matchStrings));

        System.out.println("Sending formatted history: " + sb.toString());
        return sb.toString();
    }

    @Override
    public void run() {
        try {
            // Mở luồng vào ra trên Socket tại Server.
            is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
            System.out.println("Khời động luông mới thành công, ID là: " + clientNumber);
            write("server-send-id" + "," + clientNumber);

//            write("Thread ID: " + this.clientNumber);
            String message;
            while (!isClosed) {
                message = is.readLine();
                if (message == null) {
                    break;
                }
                String[] messageSplit = message.split(",");
                //Xác minh
                if (messageSplit[0].equals("client-verify")) {
                    System.out.println(message);
                    User user1 = userDAO.verifyUser(new User(messageSplit[1], messageSplit[2]));
                    if (user1 == null) {
                        write("wrong-user," + messageSplit[1] + "," + messageSplit[2]);
                    } else if (!user1.getIsOnline()) {
                        write("login-success," + getStringFromUser(user1));
                        this.user = user1;
                        write("Your ID: " + this.getUser().getID());
                        userDAO.updateToOnline(this.user.getID());
                        GameServer.serverThreadBus.boardCast(clientNumber, "chat-server," + user1.getNickname() + " đang online");
//                        GameServer.admin.addMessage("[" + user1.getID() + "] " + user1.getNickname() + " đang online");
                    } else if (!userDAO.checkIsBanned(user1)) {
                        write("dupplicate-login," + messageSplit[1] + "," + messageSplit[2]);
                    } else {
                        write("banned-user," + messageSplit[1] + "," + messageSplit[2]);
                    }
                }
                //Xử lý đăng kí
                if (messageSplit[0].equals("register")) {
                    boolean checkdup = userDAO.checkDuplicated(messageSplit[1]);
                    if (checkdup) {
                        write("duplicate-username,");
                    } else {
                        User userRegister = new User(messageSplit[1], messageSplit[2], messageSplit[3], messageSplit[4]);
                        userDAO.addUser(userRegister);
                        this.user = userDAO.verifyUser(userRegister);
                        userDAO.updateToOnline(this.user.getID());
                        GameServer.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getNickname() + " đang online");
                        write("login-success," + getStringFromUser(this.user));
                    }
                }
                //Xử lý người chơi đăng xuất
                if (messageSplit[0].equals("offline")) {
                    userDAO.updateToOffline(this.user.getID());
//                    GameServer.admin.addMessage("[" + user.getID() + "] " + user.getNickname() + " đã offline");
                    GameServer.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getNickname() + " đã offline");
                    this.user = null;
                }
                //Xử lý xem danh sách bạn bè => hàm này liên tục requested from client 
                if (messageSplit[0].equals("view-friend-list")) {
                    List<User> friends = userDAO.getListFriend(this.user.getID());
                    StringBuilder res = new StringBuilder("return-friend-list,");
                    for (User friend : friends) {
                        res.append(friend.getID()).append(",").append(friend.getNickname()).append(",").append(friend.getIsOnline() ? 1 : 0).append(",").append(friend.getIsPlaying() ? 1 : 0).append(",");
                    }
                    System.out.println(res);
                    write(res.toString());
                }

                //Xử lý vào phòng trong chức năng tìm kiếm phòng
                if (messageSplit[0].equals("go-to-room")) {
                    int roomName = Integer.parseInt(messageSplit[1]);
                    boolean isFinded = false;
                    for (ClientHandler serverThread : GameServer.serverThreadBus.getListClientHandlers()) {
                        if (serverThread.getRoom() != null && serverThread.getRoom().getId() == roomName) {
                            isFinded = true;
                            if (serverThread.getRoom().getNumberOfUser() == 2) {
                                write("room-fully,");
                            } else {
                                if (serverThread.getRoom().getPassword() == null || serverThread.getRoom().getPassword().equals(messageSplit[2])) {
                                    this.room = serverThread.getRoom();
                                    room.setUser2(this);
//                                    room.increaseNumberOfGame();
                                    this.userDAO.updateToPlaying(this.user.getID());
                                    goToPartnerRoom();
                                } else {
                                    write("room-wrong-password,");
                                }
                            }
                            break;
                        }
                    }
                    if (!isFinded) {
                        write("room-not-found,");
                    }
                }
                //Xử lý lấy danh sách bảng xếp hạng
                if (messageSplit[0].equals("get-rank-charts")) {
                    List<User> ranks = userDAO.getUserStaticRank();
                    StringBuilder res = new StringBuilder("return-get-rank-charts,");
                    for (User user : ranks) {
                        res.append(getStringFromUser(user)).append(",");
                    }
                    System.out.println(res);
                    write(res.toString());
                }
                //Xử lý tạo phòng
                if (messageSplit[0].equals("create-room")) {
                    room = new Room(this);
                    if (messageSplit.length == 2) {
                        room.setPassword(messageSplit[1]);
                        write("your-created-room," + room.getId() + "," + messageSplit[1]);
                        System.out.println("Tạo phòng mới thành công, password là " + messageSplit[1]);
                    } else {
                        write("your-created-room," + room.getId());
                        System.out.println("Tạo phòng mới thành công");
                    }
                    userDAO.updateToPlaying(this.user.getID());
                }
                //Xử lý xem danh sách phòng trống
                if (messageSplit[0].equals("view-room-list")) {
                    StringBuilder res = new StringBuilder("room-list,");
                    int number = 1;
                    for (ClientHandler serverThread : GameServer.serverThreadBus.getListClientHandlers()) {
                        if (number > 8) {
                            break;
                        }
                        if (serverThread.room != null && serverThread.room.getNumberOfUser() == 1) {
                            res.append(serverThread.room.getId()).append(",").append(serverThread.room.getPassword()).append(",");
                        }
                        number++;
                    }
                    write(res.toString());
                    System.out.println(res);
                }
                //Xử lý lấy thông tin kết bạn và rank
                if (messageSplit[0].equals("check-friend")) {
                    String res = "check-friend-response,";
                    res += (userDAO.checkIsFriend(this.user.getID(), Integer.parseInt(messageSplit[1])) ? 1 : 0);
                    write(res);
                }
                //Xử lý tìm phòng nhanh
                if (messageSplit[0].equals("quick-room")) {
                    boolean isFinded = false;
                    for (ClientHandler serverThread : GameServer.serverThreadBus.getListClientHandlers()) {
                        if (serverThread.room != null && serverThread.room.getNumberOfUser() == 1 && serverThread.room.getPassword().equals(" ")) {
                            serverThread.room.setUser2(this);
                            this.room = serverThread.room;
//                            room.increaseNumberOfGame();
                            System.out.println("Đã vào phòng " + room.getId());
                            goToPartnerRoom();

                            userDAO.updateToPlaying(this.user.getID());
                            isFinded = true;
                            //Xử lý phần mời cả 2 người chơi vào phòng
                            break;
                        }
                    }

                    if (!isFinded) {
                        this.room = new Room(this);
                        userDAO.updateToPlaying(this.user.getID());
                        System.out.println("Không tìm thấy phòng, tạo phòng mới");
                    }
                }
                //Xử lý không tìm được phòng
                if (messageSplit[0].equals("cancel-room")) {
                    userDAO.updateToNotPlaying(this.user.getID());
                    System.out.println("Đã hủy phòng");
                    this.room = null;
                }
                //Xử lý khi có người chơi thứ 2 vào phòng
                if (messageSplit[0].equals("join-room")) {
                    int ID_room = Integer.parseInt(messageSplit[1]);
                    for (ClientHandler serverThread : GameServer.serverThreadBus.getListClientHandlers()) {
                        if (serverThread.room != null && serverThread.room.getId() == ID_room) {
                            serverThread.room.setUser2(this);
                            this.room = serverThread.room;
                            System.out.println("Đã vào phòng " + room.getId());
//                            room.increaseNumberOfGame();
                            goToPartnerRoom();

                            userDAO.updateToPlaying(this.user.getID());
                            break;
                        }
                    }
                }
                //Xử lý yêu cầu kết bạn
                if (messageSplit[0].equals("make-friend")) {
                    GameServer.serverThreadBus.getClientHandlerByUserID(Integer.parseInt(messageSplit[1]))
                            .write("make-friend-request," + this.user.getID() + "," + userDAO.getNickNameByID(this.user.getID()));
                }
                //Xử lý xác nhận kết bạn
                if (messageSplit[0].equals("make-friend-confirm")) {
                    userDAO.makeFriend(this.user.getID(), Integer.parseInt(messageSplit[1]));
                    System.out.println("Kết bạn thành công");
                }
                //Xử lý khi gửi yêu cầu thách đấu tới bạn bè
                if (messageSplit[0].equals("duel-request")) {
                    GameServer.serverThreadBus.sendMessageToUserID(Integer.parseInt(messageSplit[1]),
                            "duel-notice," + this.user.getID() + "," + this.user.getNickname());
                }
                //Xử lý khi đối thủ đồng ý thách đấu
                if (messageSplit[0].equals("agree-duel")) {
                    this.room = new Room(this);
                    int ID_User2 = Integer.parseInt(messageSplit[1]);
                    ClientHandler user2 = GameServer.serverThreadBus.getClientHandlerByUserID(ID_User2);
                    room.setUser2(user2);
                    user2.setRoom(room);
//                    room.increaseNumberOfGame();
                    goToOwnRoom();
                    userDAO.updateToPlaying(this.user.getID());
                }
                //Xử lý khi không đồng ý thách đấu
                if (messageSplit[0].equals("disagree-duel")) {
                    GameServer.serverThreadBus.sendMessageToUserID(Integer.parseInt(messageSplit[1]), message);
                }

                if (messageSplit[0].equals("left-room")) {
                    if (room != null) {
//                        room.getGameRoom().removePlayer(this);
                        room.setUsersToNotPlaying();
//                        room.decreaseNumberOfGame();
//                        room.getCompetitor(clientNumber).write("left-room,");
                        room.getGameRoom().clientDisconnected(this);

                        room.getCompetitor(clientNumber).room = null;
                        this.room = null;
                    }
                }

                //new on game tinh nhanh
                if (message.startsWith("answer;")) {
                    String[] parts = message.split(";");
                    if (parts.length >= 3) {
                        int clientId = Integer.parseInt(parts[1]);
                        boolean isCorrect = Boolean.parseBoolean(parts[2]);
                        room.getGameRoom().processAnswer(this, isCorrect);
                    }
                }

                // Thêm case mới để xử lý client ready
                if (messageSplit[0].equals("client-ready")) {
                    if (room != null) {
                        room.getGameRoom().registerClientReady(this);
                    }
                }
                if (messageSplit[0].equals("get_match_history")) {
//                    int requestUserId = Integer.parseInt(messageSplit[1]);
//                    List<MatchHistory> matchHistory = new UserDAO().getMatchHistory(requestUserId);
//                    String historyMessage = formatMatchHistory(matchHistory);
//                    write(historyMessage);
                    System.out.println("Received request for match history");
                    int requestUserId = Integer.parseInt(messageSplit[1]);
                    System.out.println("Fetching history for user ID: " + requestUserId);

                    List<MatchHistory> matchHistory = new UserDAO().getMatchHistory(requestUserId);
                    System.out.println("Found matches: " + matchHistory.size());

                    String response = formatMatchHistory(matchHistory);
                    System.out.println("Sending response: " + response);  // In ra message thực tế được gửi
                    write(response);

                }
                // Thêm vào phần xử lý message trong run()
                if (messageSplit[0].equals("game-finish")) {
                    if (room != null) {
                        try {
                            // Xử lý kết thúc game
                            if (room.getGameRoom() != null) {
                                room.getGameRoom().clientDisconnected(this);
                            }

                            // Cập nhật trạng thái playing
                            userDAO.updateToNotPlaying(this.user.getID());

                            // Thông báo cho đối thủ
                            if (room.getCompetitor(clientNumber) != null) {
                                room.getCompetitor(clientNumber).write("opponent-finished," + clientNumber);
                                room.getCompetitor(clientNumber).room = null;
                            }

                            // Reset room cuối cùng
                            this.room = null;

                        } catch (Exception ex) {
                            System.err.println("Error handling game finish: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                }

            }
        } catch (IOException e) {
            //Thay đổi giá trị cờ để thoát luồng
            isClosed = true;
            //Cập nhật trạng thái của user
            if (this.user != null) {
                userDAO.updateToOffline(this.user.getID());
                userDAO.updateToNotPlaying(this.user.getID());
                GameServer.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getNickname() + " đã offline");
//                GameServer.admin.addMessage("[" + user.getID() + "] " + user.getNickname() + " đã offline");
            }

            //remove thread khỏi bus
            GameServer.serverThreadBus.remove(clientNumber);
            System.out.println(this.clientNumber + " đã thoát");
            if (room != null) {
                try {
                    if (room.getCompetitor(clientNumber) != null) {
//                        room.decreaseNumberOfGame();
                        room.getCompetitor(clientNumber).write("left-room,");
                        room.getCompetitor(clientNumber).room = null;
                    }
                    this.room = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } finally {
            disconnect();
        }

    }
}
