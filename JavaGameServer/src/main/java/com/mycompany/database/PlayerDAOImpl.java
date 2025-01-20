/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database;

import com.mycompany.networking.OnlinePlayer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AhmedAli
 */
public class PlayerDAOImpl implements PlayerDAO{
    
        
    @Override
    public List<PlayerDTO> getOnlinePlayers() {
        List<PlayerDTO> onlinePlayers = new ArrayList<>();
        
        String query = "SELECT * FROM USERS WHERE is_online != 0"; // != may be <>
        try {
           Connection connection = Database.getInstance().getConnection();
           PreparedStatement prepareStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
           ResultSet res = prepareStatement.executeQuery();
           
           while(res.next()){
               String userName = res.getString("user_name");
               int score = res.getInt("score");
               boolean isAvailable = res.getBoolean("is_available");
               
               onlinePlayers.add(new PlayerDTOImpl(userName, score, isAvailable));
           }
        } catch (SQLException ex) {
            Logger.getLogger(PlayerDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return onlinePlayers;
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

    @Override
    public OnlinePlayerDTO getOnlinePlayer(String userName) {
        
         OnlinePlayerDTO  onlinePlayerDTO  =null ;
         
          try {
           Connection connection = Database.getInstance().getConnection();
           PreparedStatement prepareStatement = connection.prepareStatement("SELECT score FROM USERS WHERE user_name = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           prepareStatement.setString(1, userName);
           ResultSet res = prepareStatement.executeQuery();
           int score =res.getInt("score");
            onlinePlayerDTO = new OnlinePlayerDTOImpl(new OnlinePlayer(userName,score));
        } catch (SQLException ex) {
            Logger.getLogger(PlayerDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return onlinePlayerDTO;
        
    }
    }
    
