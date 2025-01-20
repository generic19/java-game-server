/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javagameserver.handling;

import com.mycompany.database.OnlinePlayerDTO;
import com.mycompany.database.PlayerDAO;
import com.mycompany.database.PlayerDAOImpl;
import com.mycompany.javagameserver.Client;
import com.mycompany.javagameserver.services.ClientService;
import com.mycompany.javagameserver.services.ClientService.PlayerUpdateListener;
import com.mycompany.networking.OnlinePlayer;
import com.mycompany.networking.matching.IncomingInviteRequest;
import com.mycompany.networking.matching.IncomingInviteRespose;
import com.mycompany.networking.matching.InviteRequest;
import com.mycompany.networking.matching.MatchingInitialStateMessage;
import com.mycompany.networking.matching.MatchingMessage;
import com.mycompany.networking.matching.MatchingSubscriptionRequest;
import com.mycompany.networking.matching.MatchingUpdateMessage;
import java.util.List;




/**
 *
 * @author ArwaKhaled
 */
public class MatchingHandler implements Handler  , PlayerUpdateListener{
    
     OnlinePlayer   player;
     OnlinePlayer opponent = null;
     PlayerDAO playerDAO= new PlayerDAOImpl();
     boolean isSubescribe ;
     AuthenticatedRequest authRequest ;
     Client client ;
     Handler next;
     
    @Override
    public void handle(Request request) {
         if(request.getMessage() instanceof  MatchingMessage){
            this.authRequest = (AuthenticatedRequest) request;
            if(request.getMessage() instanceof MatchingSubscriptionRequest){
                player=getOnlinePlayer(authRequest.getUserName());
               MatchingSubscriptionRequest msg  = (MatchingSubscriptionRequest)request.getMessage();
               if(msg.isSubscribe()){
                   ClientService.getService().setIsOnline(client,true);
                   List<OnlinePlayer> availablePlayers=  ClientService.getService().getAvailable( );
                   List<OnlinePlayer> inGamePlayers=  ClientService.getService().getInGame();
                   MatchingInitialStateMessage message= new MatchingInitialStateMessage(availablePlayers, inGamePlayers);
                   client.sendMessage(message);
                   ClientService.getService().addPlayerUpdateListener(this);
                  
                }else {
                    ClientService.getService().removePlayerUpdateListener(this);
               }
                  this.isSubescribe= msg.isSubscribe();
            } else if(request.getMessage() instanceof InviteRequest){
               
            }else if(request.getMessage() instanceof IncomingInviteRespose ){
                
                

                 
}
            }
    }
    
    @Override
    public void setNext(Handler handler) {
       next=handler;
    }
      OnlinePlayer getOnlinePlayer(String userName){
        OnlinePlayerDTO onlinePlayerDTO =  playerDAO.getOnlinePlayer(userName) ;
       return onlinePlayerDTO.getPlayer();
    }

    @Override
    public void onPlayerUpdate(String username, boolean isAdd, boolean isRemove, boolean isAvailable, boolean isInGame) {
     
    }

    @Override
    public void bind(Client client) {
        this.client=client;
    }
    
    
}
