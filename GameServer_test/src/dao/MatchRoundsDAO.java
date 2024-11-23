/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.MatchRound;

public class MatchRoundsDAO extends DAO {
    
    public MatchRoundsDAO() {
        super();
    }
    
    public int createRound(int matchId, int roundNumber, int timeLimit, int questionsPerRound) {
        try {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO match_rounds (match_id, round_number, time_limit, questions_per_round) " +
                "VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, matchId);
            ps.setInt(2, roundNumber);
            ps.setInt(3, timeLimit);
            ps.setInt(4, questionsPerRound);
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
    
    public List<MatchRound> getRoundsByMatch(int matchId) {
        List<MatchRound> rounds = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM match_rounds WHERE match_id = ? ORDER BY round_number"
            );
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                MatchRound round = new MatchRound(
                    rs.getInt("round_id"),
                    rs.getInt("match_id"),
                    rs.getInt("round_number"),
                    rs.getInt("time_limit"),
                    rs.getInt("questions_per_round")
                );
                rounds.add(round);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rounds;
    }
}

