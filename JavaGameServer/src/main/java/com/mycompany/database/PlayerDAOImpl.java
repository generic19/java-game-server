/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author basel
 */
public class PlayerDAOImpl implements PlayerDAO {

    @Override
    public List<PlayerDTO> getOnlinePlayers() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updatePlayerScore(PlayerScoreUpdateDTO playerScoreUpdate) {
        try {
            Connection conn = Database.getInstance().getConnection();
            
            PreparedStatement stmt = conn.prepareStatement("UPDATE Users SET score = ? WHERE user_name = ?");
            
            stmt.setInt(0, playerScoreUpdate.getScore());
            stmt.setString(1, playerScoreUpdate.getUsername());
            
            stmt.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(PlayerDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
