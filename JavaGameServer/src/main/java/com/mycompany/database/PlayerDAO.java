/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.database;

import com.mycompany.database.PlayerDTO;
import com.mycompany.database.PlayerScoreUpdateDTO;
import java.util.List;

/**
 *
 * @author ArwaKhaled
 */
public interface PlayerDAO {
   List<PlayerDTO> getOnlinePlyers();
   void updatePlayerScore( PlayerScoreUpdateDTO playerScoreUpdate);
    
}
