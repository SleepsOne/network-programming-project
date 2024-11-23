/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;


public class PlayerAnswer {
    private int answerId;
    private int questionId;
    private int playerId;
    private boolean isCorrect;
    private long answerTime;
    private int pointsEarned;
    
    public PlayerAnswer(int answerId, int questionId, int playerId, boolean isCorrect, 
                       long answerTime, int pointsEarned) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.playerId = playerId;
        this.isCorrect = isCorrect;
        this.answerTime = answerTime;
        this.pointsEarned = pointsEarned;
    }
    
    // Getters and setters
    public int getAnswerId() { return answerId; }
    public void setAnswerId(int answerId) { this.answerId = answerId; }
    
    // Add other getters and setters

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public boolean isIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public long getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(long answerTime) {
        this.answerTime = answerTime;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }
    
    
}