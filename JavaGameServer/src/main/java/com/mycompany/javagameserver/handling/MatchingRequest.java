/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javagameserver.handling;

import com.mycompany.networking.Message;
import com.mycompany.networking.OnlinePlayer;

/**
 *
 * @author ArwaKhaled
 */
public class MatchingRequest extends AuthenticatedRequest {
     OnlinePlayer player;
     OnlinePlayer opponent;
     boolean isInGame;
      EndMatch endMatchHandler;

    public MatchingRequest(OnlinePlayer player, OnlinePlayer opponent, boolean isInGame, String userName, Message message ,EndMatch endMatchHandler) {
        super(userName, message);
        this.player = player;
        this.opponent = opponent;
        this.isInGame = isInGame;
        this.endMatchHandler= endMatchHandler;
    }

    public OnlinePlayer getPlayer() {
        return player;
    }

    public OnlinePlayer getOpponent() {
        return opponent;
    }

    public void endMatch() {
        endMatchHandler.endMatch();
        
    } 
    
// create interface 
// contain one method endmatch;
//
      
  public interface EndMatch{
  
      void endMatch();
  }  
     
}
