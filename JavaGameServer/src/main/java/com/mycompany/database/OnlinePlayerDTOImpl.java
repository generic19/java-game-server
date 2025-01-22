/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database;
import com.mycompany.networking.OnlinePlayer;

/**
 *
 * @author ArwaKhaled
 */
public class OnlinePlayerDTOImpl implements OnlinePlayerDTO {
    OnlinePlayer player;

    public OnlinePlayerDTOImpl(OnlinePlayer player) {
        this.player = player;
    }

    public OnlinePlayer getPlayer() {
        return player;
    }
    
    
}
