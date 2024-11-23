/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.MatchQuestion;


public class MatchQuestionsDAO extends DAO {
    
    public MatchQuestionsDAO() {
        super();
    }
    
    public int createQuestion(int matchId, int roundId, int questionNumber, int targetNumber, String operations) {
        try {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO match_questions (match_id, round_id, question_number, target_number, operations) " +
                "VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, matchId);
            ps.setInt(2, roundId);
            ps.setInt(3, questionNumber);
            ps.setInt(4, targetNumber);
            ps.setString(5, operations);
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public List<MatchQuestion> getQuestionsByRound(int roundId) {
        List<MatchQuestion> questions = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM match_questions WHERE round_id = ? ORDER BY question_number"
            );
            ps.setInt(1, roundId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                MatchQuestion question = new MatchQuestion(
                    rs.getInt("question_id"),
                    rs.getInt("match_id"),
                    rs.getInt("round_id"),
                    rs.getInt("question_number"),
                    rs.getInt("target_number"),
                    rs.getString("operations")
                );
                questions.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }
}

