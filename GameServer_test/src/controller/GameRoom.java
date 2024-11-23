package controller;

import dao.MatchHistoryDAO;
import dao.MatchQuestionsDAO;
import dao.MatchRoundsDAO;
import dao.PlayerAnswersDAO;
import dao.UserDAO;
import model.User;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameRoom {

    private final String roomId; // checked
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final Map<Integer, Integer> scores = new ConcurrentHashMap<>();
    private final Map<Integer, Long> answerTimes = new ConcurrentHashMap<>();
    private final Map<Integer, Boolean> answerCorrectness = new ConcurrentHashMap<>(); // Thêm map mới để lưu trạng thái đúng/sai
    private final Map<Integer, Integer> matchScore = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> currentQuestionPoints = new ConcurrentHashMap<>(); // theo dõi điểm được cộng trong từng câu hỏi
    private volatile boolean isGameActive = false;
    private final ScheduledExecutorService gameTimer = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> currentQuestionTimer;
    private static final int RESULT_DISPLAY_TIME = 3; // Thời gian hiển thị kết quả (giây)
    private CompletableFuture<Void> currentQuestionFuture;
    private volatile boolean shouldMoveToNextQuestion = false;
    private int hostId;
    private final Map<Integer, Boolean> clientReadyStatus = new ConcurrentHashMap<>();

    // Add new fields for DAOs
    private final MatchHistoryDAO matchHistoryDAO;
    private final MatchRoundsDAO matchRoundsDAO;
    private final MatchQuestionsDAO matchQuestionsDAO;
    private final PlayerAnswersDAO playerAnswersDAO;

    // Add fields to track database IDs
    private int currentMatchId;
    private int currentRoundId;
    private int currentQuestionId;

    public GameRoom(String roomId) {
        this.roomId = roomId;

        // Initialize DAOs
        this.matchHistoryDAO = new MatchHistoryDAO();
        this.matchRoundsDAO = new MatchRoundsDAO();
        this.matchQuestionsDAO = new MatchQuestionsDAO();
        this.playerAnswersDAO = new PlayerAnswersDAO();
    }

    public synchronized void registerClientReady(ClientHandler client) {
        clientReadyStatus.put(client.getClientNumber(), true);

        // Nếu cả hai client đã sẵn sàng, bắt đầu game
        if (clientReadyStatus.size() == 2 && clientReadyStatus.values().stream().allMatch(ready -> ready)) {
            startGame();
        }
    }

    public void removePlayer(ClientHandler client) {
        clients.remove(client);
    }

    public synchronized void addClient(ClientHandler client) {
        clients.add(client);
        scores.put(client.getUser().getID(), 0);
        matchScore.put(client.getUser().getID(), 0); // Khởi tạo tỉ số 0-0
        if (clients.size() == 2) {
            // Create match record when 2 players join
            int player1Id = clients.get(0).getUser().getID();
            int player2Id = clients.get(1).getUser().getID();
            currentMatchId = matchHistoryDAO.createMatch(player1Id, player2Id);
            broadcastMessage("hostId;" + hostId);
//            startGame();

        }
    }

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public List<ClientHandler> getClients() {
        return clients;
    }

    public void startGame() {
        isGameActive = true;
        broadcastMessage("Game starting...;");

        CompletableFuture.runAsync(() -> {
            try {
                runGameRounds();
            } catch (Exception e) {
                System.err.println("Error in game room " + roomId + ": " + e.getMessage());
                endGame("Game ended due to an error");
            }
        });
    }

    private void runGameRounds() {
        Random random = new Random();
        int numberOfRounds = 2;
        int questionsPerRound = 2;

        for (int round = 1; round <= numberOfRounds && isGameActive; round++) {
            int timeLimit = (round == 1) ? 15 : 30;

            // Create round record
            currentRoundId = matchRoundsDAO.createRound(
                    currentMatchId,
                    round,
                    timeLimit,
                    questionsPerRound
            );

            for (int question = 1; question <= questionsPerRound && isGameActive; question++) {
                int target = random.nextInt((round == 1) ? 10 : 30) + 1;
                QuestionGenerator questionGen = new QuestionGenerator(target, round);
                String operations = questionGen.printChoices();

                // Create question record
                currentQuestionId = matchQuestionsDAO.createQuestion(
                        currentMatchId,
                        currentRoundId,
                        question,
                        target,
                        operations
                );

                answerTimes.clear();
                shouldMoveToNextQuestion = false;
                broadcastQuestion(target, operations, round, timeLimit);

                // Đợi cho đến khi hết thời gian hoặc cả hai người chơi trả lời
                currentQuestionFuture = new CompletableFuture<>();
                try {
                    CompletableFuture.anyOf(
                            currentQuestionFuture,
                            CompletableFuture.runAsync(() -> {
                                waitForAnswers(timeLimit);
                            })
                    ).get();

                    // Đợi thêm thời gian để hiển thị kết quả
                    Thread.sleep(RESULT_DISPLAY_TIME * 1000L);

                } catch (InterruptedException | ExecutionException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        endGame("Game completed!");
    }
//  "question;target;operations;round;timeLimit"

    private void broadcastQuestion(int target, String operations, int round, int timeLimit) {
        // Reset maps cho câu hỏi mới
        answerTimes.clear();
        answerCorrectness.clear();

        String question = String.format("question;%d;%s;%d;%d", target, operations, round, timeLimit);
        System.out.println("question choices: " + question);
        broadcastMessage(question);

        // Lưu reference của timer mới
        currentQuestionTimer = gameTimer.schedule(() -> {
            processQuestionEnd();
        }, timeLimit, TimeUnit.SECONDS);
    }

//  "question_result;id1;answered1;correct1;time1;id2;answered2;correct2;time2"
//    kết quả khi submit 1 câu của 2 người chơi
//    private void processQuestionEnd() {
//        if (clients.size() == 2) {
//            int client1Id = clients.get(0).getUser().getID();
//            int client2Id = clients.get(1).getUser().getID();
//
//            boolean client1Answered = answerCorrectness.containsKey(client1Id);
//            boolean client2Answered = answerCorrectness.containsKey(client2Id);
//
//            boolean client1Correct = client1Answered && answerCorrectness.get(client1Id);
//            boolean client2Correct = client2Answered && answerCorrectness.get(client2Id);
//
//            // Cập nhật tỉ số
//            if (client1Correct || client2Correct) {
//                if (client1Correct && !client2Correct) {
//                    matchScore.put(client1Id, matchScore.getOrDefault(client1Id, 0) + 1);
//                } else if (client2Correct && !client1Correct) {
//                    matchScore.put(client2Id, matchScore.getOrDefault(client2Id, 0) + 1);
//                } else if (client1Correct && client2Correct) {
//                    // Nếu cả hai đều đúng, người trả lời trước thắng
//                    long time1 = answerTimes.get(client1Id);
//                    long time2 = answerTimes.get(client2Id);
//                    if (time1 < time2) {
//                        matchScore.put(client1Id, matchScore.getOrDefault(client1Id, 0) + 1);
//                    } else {
//                        matchScore.put(client2Id, matchScore.getOrDefault(client2Id, 0) + 1);
//                    }
//                }
//            }
//
//            // Gửi kết quả chi tiết cho cả hai người chơi
//            StringBuilder resultMessage = new StringBuilder("question_result;");
//            // Thêm thông tin người chơi 1
//            resultMessage.append(client1Id).append(";")
//                    .append(client1Answered ? "1" : "0").append(";")
//                    .append(client1Correct ? "1" : "0").append(";")
//                    .append(client1Answered ? answerTimes.get(client1Id) : "0").append(";");
//            // Thêm thông tin người chơi 2
//            resultMessage.append(client2Id).append(";")
//                    .append(client2Answered ? "1" : "0").append(";")
//                    .append(client2Correct ? "1" : "0").append(";")
//                    .append(client2Answered ? answerTimes.get(client2Id) : "0");
//
//            broadcastMessage(resultMessage.toString());
//        }
//
//        broadcastFinalScores();
//    }
    private void processQuestionEnd() {
        if (clients.size() == 2) {
            int client1Id = clients.get(0).getUser().getID();
            int client2Id = clients.get(1).getUser().getID();

            boolean client1Answered = answerCorrectness.containsKey(client1Id);
            boolean client2Answered = answerCorrectness.containsKey(client2Id);

            boolean client1Correct = client1Answered && answerCorrectness.get(client1Id);
            boolean client2Correct = client2Answered && answerCorrectness.get(client2Id);

            // Cập nhật tỉ số - sửa lại logic
            if (client1Correct || client2Correct) {
                if (client1Correct && client2Correct) {
                    // Nếu cả hai đều đúng, người trả lời trước thắng
                    long time1 = answerTimes.get(client1Id);
                    long time2 = answerTimes.get(client2Id);
                    int winnerId = (time1 < time2) ? client1Id : client2Id;
                    matchScore.put(winnerId, matchScore.getOrDefault(winnerId, 0) + 1);
                } else {
                    // Chỉ 1 người trả lời đúng
                    int winnerId = client1Correct ? client1Id : client2Id;
                    matchScore.put(winnerId, matchScore.getOrDefault(winnerId, 0) + 1);
                }
            }

            // Gửi kết quả chi tiết cho cả hai người chơi
            StringBuilder resultMessage = new StringBuilder("question_result;");
            // Thêm thông tin người chơi 1
            resultMessage.append(client1Id).append(";")
                    .append(client1Answered ? "1" : "0").append(";")
                    .append(client1Correct ? "1" : "0").append(";")
                    .append(client1Answered ? answerTimes.get(client1Id) : "0").append(";");
            // Thêm thông tin người chơi 2
            resultMessage.append(client2Id).append(";")
                    .append(client2Answered ? "1" : "0").append(";")
                    .append(client2Correct ? "1" : "0").append(";")
                    .append(client2Answered ? answerTimes.get(client2Id) : "0");

            broadcastMessage(resultMessage.toString());
        }

        broadcastFinalScores();
    }

//  "final_scores;score1;score2;match;matchScore1;matchScore2"
    private void broadcastFinalScores() {
        StringBuilder message = new StringBuilder("final_scores");
        // Add accumulated scores
        for (ClientHandler client : clients) {
            message.append(";").append(scores.getOrDefault(client.getUser().getID(), 0));
        }
        // Add match scores
        message.append(";match");
        for (ClientHandler client : clients) {
            message.append(";").append(matchScore.getOrDefault(client.getUser().getID(), 0));
        }

        System.out.println(message.toString());

        broadcastMessage(message.toString());

    }

    private void waitForAnswers(int timeLimit) {
        try {
            Thread.sleep(timeLimit * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // bang nay oke
    public synchronized void processAnswer(ClientHandler client, boolean isCorrect) {
        if (isGameActive) {
            int clientId = client.getUser().getID();
            long currentTime = System.currentTimeMillis();
            answerTimes.putIfAbsent(clientId, currentTime);
            answerCorrectness.put(clientId, isCorrect);

            if (isCorrect) {
                int points = calculatePoints(clientId);
                int currentScore = scores.getOrDefault(clientId, 0);
                scores.put(clientId, currentScore + points);
                notifyPointsEarned(clientId, points);

                // Record answer in database
                playerAnswersDAO.recordAnswer(
                        currentQuestionId,
                        clientId,
                        isCorrect,
                        currentTime,
                        points
                );
            } else {
                // Record incorrect answer
                playerAnswersDAO.recordAnswer(
                        currentQuestionId,
                        clientId,
                        false,
                        currentTime,
                        0
                );
            }

            // Kiểm tra nếu tất cả client đã trả lời
            if (clients.size() == answerCorrectness.size() && !shouldMoveToNextQuestion) {
                shouldMoveToNextQuestion = true;
                // Hủy timer hiện tại
                if (currentQuestionTimer != null) {
                    currentQuestionTimer.cancel(false);
                }
                // Xử lý kết thúc câu hỏi ngay lập tức
                processQuestionEnd();
                // Thông báo để chuyển sang câu hỏi tiếp theo sau khi hiển thị kết quả
                currentQuestionFuture.complete(null);
            }
        }
    }

    // Thông báo cho client biết họ vừa được cộng bao nhiêu điểm
//    "points_earned;points"
    private void notifyPointsEarned(int clientId, int points) {
        String message = String.format("points_earned;%d", points);
        broadcastMessageToAClient(message, clientId);
    }

    private int calculatePoints(int clientId) {
        long clientAnswerTime = answerTimes.get(clientId);

        // Chỉ xem xét những câu trả lời đúng để xác định người trả lời đúng đầu tiên
        boolean isFirstCorrectAnswer = answerTimes.entrySet().stream()
                .filter(entry -> answerCorrectness.getOrDefault(entry.getKey(), false)) // Chỉ lọc các câu trả lời đúng
                .filter(entry -> entry.getValue() < clientAnswerTime)
                .count() == 0;

        return isFirstCorrectAnswer ? 10 : 5;
    }
//  "opponent_disconnected;bonusPoints;matchScore"
//   bonusPoints = 40, matchScore = 5

    public void clientDisconnected(ClientHandler client) {
        clients.remove(client);
        if (isGameActive) {
            // Xử lý người chơi còn lại thắng
            if (clients.size() == 1) {
                ClientHandler remainingPlayer = clients.get(0);
                int remainingPlayerId = remainingPlayer.getUser().getID();
                int disconnectedPlayerId = client.getUser().getID();
                // Record disconnection in database
                matchHistoryDAO.recordDisconnection(currentMatchId, disconnectedPlayerId);

                // Cộng 40 điểm cho người chơi còn lại
//                int currentScore = scores.getOrDefault(remainingPlayerId, 0);
                scores.put(remainingPlayerId, 40);

                // Set tỉ số 5-0 cho người thắng
                matchScore.put(remainingPlayerId, 5);
                // Update match history with final scores
                matchHistoryDAO.endMatch(
                        currentMatchId,
                        remainingPlayerId,
                        5, // matchScoreP1
                        0, // matchScoreP2
                        scores.getOrDefault(remainingPlayerId, 0),
                        scores.getOrDefault(disconnectedPlayerId, 0)
                );

                // Thông báo người chơi còn lại thắng do đối thủ thoát
                String victoryMessage = String.format("opponent_disconnected;%d;%d", 40, 5);
                remainingPlayer.write(victoryMessage);

                // Kết thúc game với thông báo đặc biệt
                endGameDueToDisconnect(client.getUser().getID(), remainingPlayerId);
            }
        }

    }

//  "game_over;disconnect,winnerId,5,0,winnerScore,disconnectedScore"
    private void endGameDueToDisconnect(int disconnectedId, int winnerId) {
        isGameActive = false;

        // Xây dựng thông báo kết thúc game với format mới
        StringBuilder endMessage = new StringBuilder("game_over;");
        endMessage.append("disconnect;")
                .append(winnerId).append(";")
                .append("5;0;") // match score
                .append(scores.getOrDefault(winnerId, 0)).append(";")
                .append(scores.getOrDefault(disconnectedId, 0));

        // Gửi thông báo cho người chơi còn lại
        broadcastMessage(endMessage.toString());

        // Thêm delay nhỏ để đảm bảo trigger đã chạy xong
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Gửi thông tin user đã cập nhật cho người thắng
        sendUpdatedUserInfo(winnerId);

        if (currentQuestionTimer != null) {
            currentQuestionTimer.cancel(false);
        }

        gameTimer.shutdown();
        resetRoom();
        try {
            if (!gameTimer.awaitTermination(5, TimeUnit.SECONDS)) {
                gameTimer.shutdownNow();
            }
        } catch (InterruptedException e) {
            gameTimer.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

//  "game_over;winnerId,matchScore1,matchScore2,finalScore1,finalScore2"
    private void endGame(String reason) {
        System.out.println(reason);
        isGameActive = false;
        if (currentQuestionTimer != null) {
            currentQuestionTimer.cancel(false);
        }

        int client1Id = clients.get(0).getUser().getID();
        int client2Id = clients.get(1).getUser().getID();

        int finalScore1 = scores.getOrDefault(client1Id, 0);
        int finalScore2 = scores.getOrDefault(client2Id, 0);

        int matchScore1 = matchScore.getOrDefault(client1Id, 0);
        int matchScore2 = matchScore.getOrDefault(client2Id, 0);

        // Xác định người thắng
        int winnerId;
        if (matchScore1 > matchScore2) {
            winnerId = client1Id;
        } else if (matchScore2 > matchScore1) {
            winnerId = client2Id;
        } else {
            winnerId = finalScore1 > finalScore2 ? client1Id : client2Id;
        }

        // Update match history with final results
        matchHistoryDAO.endMatch(
                currentMatchId,
                winnerId,
                matchScore1,
                matchScore2,
                finalScore1,
                finalScore2
        );

        // Format mới: game_over;winner_id,match_score1,match_score2,final_score1,final_score2
        StringBuilder endMessage = new StringBuilder("game_over;");
        endMessage.append(winnerId).append(";")
                .append(matchScore1).append(";")
                .append(matchScore2).append(";")
                .append(finalScore1).append(";")
                .append(finalScore2);

        broadcastMessage(endMessage.toString());

        // Thêm delay nhỏ để đảm bảo trigger đã chạy xong
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

        }
        sendUpdatedUserInfo(client1Id);
        sendUpdatedUserInfo(client2Id);

        resetRoom();

        gameTimer.shutdown();
        try {
            if (!gameTimer.awaitTermination(5, TimeUnit.SECONDS)) {
                gameTimer.shutdownNow();
            }
        } catch (InterruptedException e) {
            gameTimer.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.write(message);
        }
    }

    private void broadcastMessageToAClient(String message, int id) {
        for (ClientHandler client : clients) {
            if (client.getUser().getID() == id) {
                client.write(message);
            }
        }
    }

    private void resetRoom() {
        // Reset all game state maps
        scores.clear();
        answerTimes.clear();
        answerCorrectness.clear();
        matchScore.clear();
        currentQuestionPoints.clear();

        clientReadyStatus.clear();

        // Reset game state variables
        isGameActive = false;
        shouldMoveToNextQuestion = false;

        // Cancel any ongoing timers
        if (currentQuestionTimer != null) {
            currentQuestionTimer.cancel(false);
            currentQuestionTimer = null;
        }

        // Reset database tracking IDs
        currentMatchId = 0;
        currentRoundId = 0;
        currentQuestionId = 0;

        // Reset completable future if exists
        if (currentQuestionFuture != null) {
            currentQuestionFuture.complete(null);
            currentQuestionFuture = null;
        }

        // Initialize scores for existing clients
        for (ClientHandler client : clients) {
            scores.put(client.getUser().getID(), 0);
            matchScore.put(client.getUser().getID(), 0);
        }

        //        
    }

    private void sendUpdatedUserInfo(int userId) {
        // Tạo một thông điệp chứa thông tin user đã cập nhật
        User updatedUser = new UserDAO().getUserById(userId);
        if (updatedUser != null) {
            String userInfoMessage = String.format("update_user_info;%d;%s;%s;%s;%s;%d;%d;%d;%d;%d;%d;%d",
                    updatedUser.getID(), // ID
                    updatedUser.getUsername(), // username
                    updatedUser.getPassword(), // password
                    updatedUser.getNickname(), // nickname
                    updatedUser.getAvatar(), // avatar
                    updatedUser.getNumberOfGame(), // numberOfGame
                    updatedUser.getNumberOfWin(), // numberOfWin 
                    updatedUser.getNumberOfDraw(), // numberOfDraw
                    updatedUser.getIsOnline() ? 1 : 0, // isOnline
                    updatedUser.getIsPlaying() ? 1 : 0,// isPlaying
                    updatedUser.getRank(), // rank
                    updatedUser.getScore() // score
            );

            // Gửi thông tin cập nhật cho client tương ứng
            broadcastMessageToAClient(userInfoMessage, userId);
        }
    }
}
