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
   List<PlayerDTO> getOnlinePlayers();
  void updatePlayerScore( PlayerScoreUpdateDTO playerScoreUpdate);
  
  OnlinePlayerDTO  getOnlinePlayer(String userName);
  
}
