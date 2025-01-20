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
                InviteRequest msg= (InviteRequest)request.getMessage();
                opponent = getOnlinePlayer(msg.getUserName());
                
                Client opponentClient=  ClientService.getService().getClientByUsername(msg.getUserName());
                opponentClient.getMatchingHandler().setOpponent(player);
                opponentClient.sendMessage(new IncomingInviteRequest(authRequest.getUserName()));
            }else if(request.getMessage() instanceof IncomingInviteRespose ){
                IncomingInviteRespose msg = (IncomingInviteRespose)request.getMessage();
                String opponentUserName=  client.getMatchingHandler().getOpponent().getUsername();
                Client opponentClient = ClientService.getService().getClientByUsername(opponentUserName);
                opponentClient.sendMessage((new IncomingInviteRespose(msg.getResponse())));
                   if(msg.getResponse()==IncomingInviteRespose.Response.ACCEPTED){
                   // to do 
                   }
            }else if(opponent!=null){
                next.handle(request);
                
            
       }
    }
    }

    public OnlinePlayer getOpponent() {
        return opponent;
    }

    public void setOpponent(OnlinePlayer opponent) {
        this.opponent = opponent;
    }
    
    @Override
    public void setNext(Handler handler) {
       next=handler;
    }
      OnlinePlayer getOnlinePlayer(String userName){
        OnlinePlayerDTO onlinePlayerDTO =  PlayerDAO.getInstance().getOnlinePlayer(userName) ;
       return onlinePlayerDTO.getPlayer();
    }

    @Override
    public void onPlayerUpdate(String username, boolean isAdd, boolean isRemove, boolean isAvailable, boolean isInGame) {
        
        MatchingUpdateMessage.UpdateType updateType= isAdd?
        MatchingUpdateMessage.UpdateType.ADD:MatchingUpdateMessage.UpdateType.REMOVE;
        MatchingUpdateMessage.Target target =  isAvailable? 
        MatchingUpdateMessage.Target.AVAILABLE:MatchingUpdateMessage.Target.IN_GAME;
        MatchingUpdateMessage msg = new MatchingUpdateMessage( username,updateType,target);
        client.sendMessage(msg);
    }

    @Override
    public void bind(Client client) {
        this.client=client;
    }
    
    
}

