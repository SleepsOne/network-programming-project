/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class MatchQuestion {
    private int questionId;
    private int matchId;
    private int roundId;
    private int questionNumber;
    private int targetNumber;
    private String operations;
    
    public MatchQuestion(int questionId, int matchId, int roundId, int questionNumber, 
                        int targetNumber, String operations) {
        this.questionId = questionId;
        this.matchId = matchId;
        this.roundId = roundId;
        this.questionNumber = questionNumber;
        this.targetNumber = targetNumber;
        this.operations = operations;
    }
    
    // Getters and setters
    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }
    
    // Add other getters and setters

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public int getTargetNumber() {
        return targetNumber;
    }

    public void setTargetNumber(int targetNumber) {
        this.targetNumber = targetNumber;
    }

    public String getOperations() {
        return operations;
    }

    public void setOperations(String operations) {
        this.operations = operations;
    }
    
    
}