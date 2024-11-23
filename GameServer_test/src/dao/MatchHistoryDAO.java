/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;

public class MatchHistoryDAO extends DAO {
    
    public MatchHistoryDAO() {
        super();
    }
    
    public int createMatch(int player1Id, int player2Id) {
        try {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO match_history (player1_id, player2_id) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, player1Id);
            ps.setInt(2, player2Id);
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
    
    public void endMatch(int matchId, int winnerId, int scoreP1, int scoreP2, int finalScoreP1, int finalScoreP2) {
        try {
            PreparedStatement ps = con.prepareStatement(
                "UPDATE match_history SET winner_id = ?, end_time = CURRENT_TIMESTAMP, " +
                "match_score_p1 = ?, match_score_p2 = ?, final_score_p1 = ?, final_score_p2 = ? " +
                "WHERE match_id = ?"
            );
            ps.setInt(1, winnerId);
            ps.setInt(2, scoreP1);
            ps.setInt(3, scoreP2);
            ps.setInt(4, finalScoreP1);
            ps.setInt(5, finalScoreP2);
            ps.setInt(6, matchId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void recordDisconnection(int matchId, int disconnectedPlayerId) {
        try {
            PreparedStatement ps = con.prepareStatement(
                "UPDATE match_history SET is_disconnected = true, disconnected_player_id = ? " +
                "WHERE match_id = ?"
            );
            ps.setInt(1, disconnectedPlayerId);
            ps.setInt(2, matchId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
