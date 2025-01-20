/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
*/
package com.mycompany.database;

import java.util.List;

/**
 *
 * @author ArwaKhaled
 */
public interface PlayerDAO {
    static PlayerDAO getInstance() {
        return PlayerDAOImpl.getInstance();
    }
    
    List<PlayerDTO> getOnlinePlayers();
    
    List<PlayerDTO> getAvailablePlayers();
    List<PlayerDTO> getInGamePlayers();
    
    void setPlayerOnline(String username, boolean isOnline);
    void setPlayerAvailable(String username, boolean isAvailable);
    
    void updatePlayerScore(PlayerScoreUpdateDTO playerScoreUpdate);
    
    void resetPlayersStatus();

    public int getScore(String username);
}
