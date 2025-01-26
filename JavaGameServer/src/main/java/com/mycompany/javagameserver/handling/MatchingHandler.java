/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
package com.mycompany.javagameserver.handling;

import com.mycompany.database.OnlinePlayerDTO;
import com.mycompany.database.PlayerDAO;
import com.mycompany.game.Game;
import com.mycompany.game.Player;
import com.mycompany.game.XOGame;
import com.mycompany.javagameserver.Client;
import com.mycompany.javagameserver.services.ClientService;
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
public class MatchingHandler implements Handler, ClientService.ClientAvailabilityListener, ClientService.ClientsListener {
    
    private OnlinePlayer player;
    private OnlinePlayer opponent = null;
    private Client client;
    private Handler next;
    
    private ClientMatchingEntryListener listener;
    
    @Override
    public void handle(Request request) {
        if (request.getMessage() instanceof MatchingMessage) {
            AuthenticatedRequest authRequest = (AuthenticatedRequest) request;
            String clientUsername = authRequest.getUserName();
            
            if (request.getMessage() instanceof MatchingSubscriptionRequest) {
                handleSubscriptionRequest((MatchingSubscriptionRequest) request.getMessage(), clientUsername);
            } else if (request.getMessage() instanceof InviteRequest) {
                handleInviteRequest((InviteRequest) request.getMessage(), clientUsername);
            } else if (request.getMessage() instanceof IncomingInviteRespose) {
                handleIncomingInviteResponse((IncomingInviteRespose) request.getMessage());
            }
        } else if (opponent != null) {
            next.handle(request);
        }
    }
    
    private void handleSubscriptionRequest(MatchingSubscriptionRequest msg, String clientUsername) throws IllegalStateException {
        player = getOnlinePlayer(clientUsername);
        
        if (msg.isSubscribe()) {
            List<OnlinePlayer> availablePlayers = ClientService.getService().getAvailable();
            List<OnlinePlayer> inGamePlayers = ClientService.getService().getInGame();
            
            listener.onClientEnteredMatching(client);
            
            client.sendMessage(new MatchingInitialStateMessage(availablePlayers, inGamePlayers));
            
            ClientService.getService().addClientsListener(this);
            ClientService.getService().addClientAvailabilityListener(this);
        } else {
            ClientService.getService().removeClientsListener(this);
            ClientService.getService().removeClientAvailabilityListener(this);
        }
    }
    
    private void handleInviteRequest(InviteRequest msg, String clientUsername) throws IllegalStateException {
        opponent = getOnlinePlayer(msg.getUserName());
        
        Client opponentClient = ClientService.getService().getClientByUsername(msg.getUserName());
        
        opponentClient.getMatchingHandler().setOpponent(player);
        opponentClient.sendMessage(new IncomingInviteRequest(clientUsername));
    }
    
    private void handleIncomingInviteResponse(IncomingInviteRespose msg) throws IllegalStateException {
        String opponentUserName = client.getMatchingHandler().getOpponent().getUsername();
        Client opponentClient = ClientService.getService().getClientByUsername(opponentUserName);
        
        opponentClient.sendMessage((new IncomingInviteRespose(msg.getResponse())));
        
        if (msg.getResponse() == IncomingInviteRespose.Response.ACCEPTED) {
            Game game = new XOGame();
            
            client.getGameHandler().startGame(game, player, opponent, Player.one);
            opponentClient.getGameHandler().startGame(game, opponent, player, Player.two);
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
        next = handler;
    }
    
    OnlinePlayer getOnlinePlayer(String userName) {
        OnlinePlayerDTO onlinePlayerDTO = PlayerDAO.getInstance().getOnlinePlayer(userName);
        return onlinePlayerDTO.getPlayer();
    }
    
    @Override
    public void bind(Client client) {
        this.client = client;
        
        if (client != null) {
            ClientService.getService().addClientsListener(this);
            ClientService.getService().addClientAvailabilityListener(this);
        } else {
            ClientService.getService().removeClientsListener(this);
            ClientService.getService().removeClientAvailabilityListener(this);
        }
    }
    
    @Override
    public String toString() {
        return "MatchingHandler{" + "player=" + player + ", opponent=" + opponent + '}';
    }
    
    @Override
    public void onClientAvailablilityChanged(Client client, boolean isAvailable) {
        if (client == this.client) {
            return;
        }
        
        String username = client.getAuthHandler().getUsername();
        
        if (username != null) {
            OnlinePlayer onlinePlayer = getOnlinePlayer(username);
            
            this.client.sendMessage(new MatchingUpdateMessage(
                onlinePlayer,
                isAvailable ? MatchingUpdateMessage.UpdateType.ADD : MatchingUpdateMessage.UpdateType.REMOVE,
                MatchingUpdateMessage.Target.AVAILABLE
            ));
            
            this.client.sendMessage(new MatchingUpdateMessage(
                onlinePlayer,
                isAvailable ? MatchingUpdateMessage.UpdateType.REMOVE : MatchingUpdateMessage.UpdateType.ADD,
                MatchingUpdateMessage.Target.IN_GAME
            ));
        }
    }
    
    @Override
    public void onClientAdded(Client client) {}
    
    @Override
    public void onClientRemoved(Client client) {
        if (client == this.client) {
            return;
        }
        
        String username = client.getAuthHandler().getUsername();
        
        if (username != null) {
            OnlinePlayer onlinePlayer = getOnlinePlayer(username);
            
            client.sendMessage(new MatchingUpdateMessage(
                onlinePlayer,
                MatchingUpdateMessage.UpdateType.REMOVE,
                MatchingUpdateMessage.Target.AVAILABLE
            ));
            
            client.sendMessage(new MatchingUpdateMessage(
                onlinePlayer,
                MatchingUpdateMessage.UpdateType.REMOVE,
                MatchingUpdateMessage.Target.IN_GAME
            ));
        }
    }
    
    public void setListener(ClientMatchingEntryListener listener) {
        this.listener = listener;
    }
}
