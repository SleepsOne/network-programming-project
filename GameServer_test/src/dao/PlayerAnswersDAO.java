/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.PlayerAnswer;

public class PlayerAnswersDAO extends DAO {
    
    public PlayerAnswersDAO() {
        super();
    }
    
    public void recordAnswer(int questionId, int playerId, boolean isCorrect, long answerTime, int pointsEarned) {
        try {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO player_answers (question_id, player_id, is_correct, answer_time, points_earned) " +
                "VALUES (?, ?, ?, ?, ?)"
            );
            ps.setInt(1, questionId);
            ps.setInt(2, playerId);
            ps.setBoolean(3, isCorrect);
            ps.setLong(4, answerTime);
            ps.setInt(5, pointsEarned);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<PlayerAnswer> getAnswersByQuestion(int questionId) {
        List<PlayerAnswer> answers = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM player_answers WHERE question_id = ?"
            );
            ps.setInt(1, questionId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                PlayerAnswer answer = new PlayerAnswer(
                    rs.getInt("answer_id"),
                    rs.getInt("question_id"),
                    rs.getInt("player_id"),
                    rs.getBoolean("is_correct"),
                    rs.getLong("answer_time"),
                    rs.getInt("points_earned")
                );
                answers.add(answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answers;
    }
    
    public List<PlayerAnswer> getAnswersByPlayer(int playerId, int matchId) {
        List<PlayerAnswer> answers = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement(
                "SELECT pa.* FROM player_answers pa " +
                "JOIN match_questions mq ON pa.question_id = mq.question_id " +
                "WHERE pa.player_id = ? AND mq.match_id = ?"
            );
            ps.setInt(1, playerId);
            ps.setInt(2, matchId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                PlayerAnswer answer = new PlayerAnswer(
                    rs.getInt("answer_id"),
                    rs.getInt("question_id"),
                    rs.getInt("player_id"),
                    rs.getBoolean("is_correct"),
                    rs.getLong("answer_time"),
                    rs.getInt("points_earned")
                );
                answers.add(answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answers;
    }
}

