package model;

import java.sql.Timestamp;

public class MatchHistory {

    private int matchId;
    private int player1Id;
    private String player1_name;
    private int player2Id;
    private String player2_name;
    private Integer winnerId;
    private Timestamp startTime;
    private Timestamp endTime;
    private int matchScoreP1;
    private int matchScoreP2;
    private int finalScoreP1;
    private int finalScoreP2;
    private boolean isDisconnected;
    private Integer disconnectedPlayerId;

    // Constructor
    public MatchHistory(int matchId, int player1Id, int player2Id) {
        this.matchId = matchId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
    }

    public MatchHistory(int matchId, int player1Id, int player2Id, Integer winnerId, Timestamp startTime, Timestamp endTime, int matchScoreP1, int matchScoreP2, int finalScoreP1, int finalScoreP2, boolean isDisconnected, Integer disconnectedPlayerId) {
        this.matchId = matchId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.winnerId = winnerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.matchScoreP1 = matchScoreP1;
        this.matchScoreP2 = matchScoreP2;
        this.finalScoreP1 = finalScoreP1;
        this.finalScoreP2 = finalScoreP2;
        this.isDisconnected = isDisconnected;
        this.disconnectedPlayerId = disconnectedPlayerId;
    }

    public MatchHistory(int matchId, int player1Id, String player1_name, int player2Id, String player2_name, Integer winnerId, Timestamp startTime, Timestamp endTime, int matchScoreP1, int matchScoreP2, int finalScoreP1, int finalScoreP2, boolean isDisconnected, Integer disconnectedPlayerId) {
        this.matchId = matchId;
        this.player1Id = player1Id;
        this.player1_name = player1_name;
        this.player2Id = player2Id;
        this.player2_name = player2_name;
        this.winnerId = winnerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.matchScoreP1 = matchScoreP1;
        this.matchScoreP2 = matchScoreP2;
        this.finalScoreP1 = finalScoreP1;
        this.finalScoreP2 = finalScoreP2;
        this.isDisconnected = isDisconnected;
        this.disconnectedPlayerId = disconnectedPlayerId;
    }

    public String getPlayer1Name() {
        return player1_name;
    }

    public void setPlayer1Name(String player1_name) {
        this.player1_name = player1_name;
    }

    public String getPlayer2Name() {
        return player2_name;
    }

    public void setPlayer2Name(String player2_name) {
        this.player2_name = player2_name;
    }
    
    
    
    
    

    // Getters and setters
    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(int player2Id) {
        this.player2Id = player2Id;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Integer winnerId) {
        this.winnerId = winnerId;
    }

    // Add other getters and setters
    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public int getMatchScoreP1() {
        return matchScoreP1;
    }

    public void setMatchScoreP1(int matchScoreP1) {
        this.matchScoreP1 = matchScoreP1;
    }

    public int getMatchScoreP2() {
        return matchScoreP2;
    }

    public void setMatchScoreP2(int matchScoreP2) {
        this.matchScoreP2 = matchScoreP2;
    }

    public int getFinalScoreP1() {
        return finalScoreP1;
    }

    public void setFinalScoreP1(int finalScoreP1) {
        this.finalScoreP1 = finalScoreP1;
    }

    public int getFinalScoreP2() {
        return finalScoreP2;
    }

    public void setFinalScoreP2(int finalScoreP2) {
        this.finalScoreP2 = finalScoreP2;
    }

    public boolean isIsDisconnected() {
        return isDisconnected;
    }

    public void setIsDisconnected(boolean isDisconnected) {
        this.isDisconnected = isDisconnected;
    }

    public Integer getDisconnectedPlayerId() {
        return disconnectedPlayerId;
    }

    public void setDisconnectedPlayerId(Integer disconnectedPlayerId) {
        this.disconnectedPlayerId = disconnectedPlayerId;
    }

    public String getResult(int userId) {
        if (isDisconnected) {
            if (disconnectedPlayerId == userId) {
                return "Disconnected";
            } else {
                return "Won (Opponent disconnected)";
            }
        }

        if (winnerId == userId) {
            return "Won";
        } else if (matchScoreP1 == matchScoreP2) {
            return "Draw";
        } else {
            return "Lost";
        }
    }

    public String getScore(int userId) {
        if (player1Id == userId) {
            return matchScoreP1 + " - " + matchScoreP2;
        } else {
            return matchScoreP2 + " - " + matchScoreP1;
        }
    }

}
