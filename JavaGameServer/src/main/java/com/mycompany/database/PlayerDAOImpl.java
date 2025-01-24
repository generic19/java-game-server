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
public class PlayerDAOImpl implements PlayerDAO {
    
    private static volatile PlayerDAO instance;
    
    public static PlayerDAO getInstance() {
        if (instance == null) {
            synchronized (PlayerDAOImpl.class) {
                if (instance == null) {
                    instance = new PlayerDAOImpl();
                }
            }
        }
        return instance;
    }
    
    private PlayerDAOImpl() {}
    
    @Override
    public List<PlayerDTO> getOnlinePlayers() {
        List<PlayerDTO> onlinePlayers = new ArrayList<>();
        
        String query = "SELECT * FROM USERS WHERE is_online != 0"; // != may be <>
        try {
            Connection connection = Database.getInstance().getConnection();
            PreparedStatement prepareStatement = connection.prepareStatement(query);
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
            
            stmt.setInt(1, playerScoreUpdate.getScore());
            stmt.setString(2, playerScoreUpdate.getUsername());
            
            stmt.executeUpdate();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public OnlinePlayerDTO getOnlinePlayer(String userName) {
        try {
            Connection connection = Database.getInstance().getConnection();
            PreparedStatement prepareStatement = connection.prepareStatement(
                "SELECT score FROM USERS WHERE user_name = ?",
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
            );
            prepareStatement.setString(1, userName);
            ResultSet res = prepareStatement.executeQuery();
            
            if (res.next()) {
                int score =res.getInt("score");
                return new OnlinePlayerDTOImpl(new OnlinePlayer(userName,score));
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public List<PlayerDTO> getAvailablePlayers() {
        return getOnlinePlayersByAvailability(true);
    }
    
    @Override
    public List<PlayerDTO> getInGamePlayers() {
        return getOnlinePlayersByAvailability(false);
    }
    
    private List<PlayerDTO> getOnlinePlayersByAvailability(boolean available) {
        try {
            Connection conn = Database.getInstance().getConnection();
            
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT user_name, score FROM Users WHERE is_online = 1 AND is_available = ?"
            );
            
            stmt.setBoolean(1, available);
            
            ResultSet res = stmt.executeQuery();
            
            ArrayList<PlayerDTO> players = new ArrayList<>();
            
            while (res.next()) {
                String username = res.getString("user_name");
                int score = res.getInt("score");
                
                players.add(new PlayerDTOImpl(username, score, available));
            }
            
            return players;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void setPlayerOnline(String username, boolean isOnline) {
        try {
            Connection conn = Database.getInstance().getConnection();
            
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE users SET is_online = ? WHERE user_name = ?"
            );
            
            stmt.setBoolean(1, isOnline);
            stmt.setString(2, username);
            
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void setPlayerAvailable(String username, boolean isAvailable) {
        try {
            Connection conn = Database.getInstance().getConnection();
            
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE users SET is_available = ? WHERE user_name = ?"
            );
            
            stmt.setBoolean(1, isAvailable);
            stmt.setString(2, username);
            
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void resetPlayersStatus() {
        try {
            Database
                .getInstance()
                .getConnection()
                .prepareStatement("UPDATE users SET is_available = 0, is_online = 0")
                .executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public int getScore(String username) {
        try {
            Connection conn = Database.getInstance().getConnection();
            
            PreparedStatement stmt = conn.prepareStatement("SELECT score FROM users WHERE user_name = ?");
            
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("score");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return 0;
    }
    
    
}
