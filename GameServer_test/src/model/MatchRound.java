/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class MatchRound {
    private int roundId;
    private int matchId;
    private int roundNumber;
    private int timeLimit;
    private int questionsPerRound;
    
    public MatchRound(int roundId, int matchId, int roundNumber, int timeLimit, int questionsPerRound) {
        this.roundId = roundId;
        this.matchId = matchId;
        this.roundNumber = roundNumber;
        this.timeLimit = timeLimit;
        this.questionsPerRound = questionsPerRound;
    }
    
    // Getters and setters
    public int getRoundId() { return roundId; }
    public void setRoundId(int roundId) { this.roundId = roundId; }
    
    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }
    
    // Add other getters and setters

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getQuestionsPerRound() {
        return questionsPerRound;
    }

    public void setQuestionsPerRound(int questionsPerRound) {
        this.questionsPerRound = questionsPerRound;
    }
    
    
    
    
}


