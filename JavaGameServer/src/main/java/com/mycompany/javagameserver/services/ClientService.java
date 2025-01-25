package com.mycompany.javagameserver.services;

import com.mycompany.database.PlayerDAO;
import com.mycompany.javagameserver.Client;
import com.mycompany.networking.OnlinePlayer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 * @author basel
 */
public class ClientService {
    
    private static volatile ClientService service;
    
    private final Set<Client> clients = ConcurrentHashMap.<Client>newKeySet();
    private final Map<String, Client> clientByUsername = new ConcurrentHashMap();
    private final Map<Client, String> usernameByClient = new ConcurrentHashMap();
    
    public static ClientService getService() {
        if (service == null) {
            synchronized (ClientService.class) {
                if (service == null) {
                    service = new ClientService();
                }
            }
        }
        return service;
    }
    
    private ClientService() {}
    
    public void addClient(Client client) {
        clients.add(client);
    }
    
    public void removeClient(Client client) {
        String username = usernameByClient.getOrDefault(client, null);
        
        clients.remove(client);
        clientByUsername.values().remove(client);
        usernameByClient.remove(client);
        
        if (username != null) {
            PlayerDAO.getInstance().setPlayerOnline(username, false);
        }
    }
    
    public Collection<Client> getClients() {
        return Collections.unmodifiableCollection(clients);
    }
    
    public int getOnlineCount() {
        return PlayerDAO.getInstance().getOnlineCount();
    }
    
    public void setUsername(Client client, String username) {
        if (username != null) {
            clientByUsername.put(username, client);
            usernameByClient.put(client, username);
            
            PlayerDAO.getInstance().setPlayerOnline(username, true);
        } else {
            String oldUsername = usernameByClient.getOrDefault(client, null);
            
            if (oldUsername != null) {
                PlayerDAO.getInstance().setPlayerOnline(oldUsername, false);
            }
            
            clientByUsername.values().remove(client);
            usernameByClient.remove(client);
        }
    }
    
    public void setIsInGame(Client client, boolean isInGame) {
        String username = usernameByClient.getOrDefault(client, null);
        
        if (username == null) {
            throw new IllegalStateException("Cannot use setIsInGame before setting username for client.");
        }
        
        PlayerDAO.getInstance().setPlayerAvailable(username, !isInGame);
    }
    
    public int getPlayerCount() {
        return PlayerDAO.getInstance().getPlayerCount();
    }
    
    public int getInGameCount() {
        return PlayerDAO.getInstance().getInGameCount();
    }
    
    public List<OnlinePlayer> getAvailable() {
        return PlayerDAO
            .getInstance()
            .getAvailablePlayers()
            .stream()
            .map(dto -> new OnlinePlayer(dto.getUsername(), dto.getScore()))
            .collect(Collectors.toList());
    }
    
    public List<OnlinePlayer> getInGame() {
        return PlayerDAO
            .getInstance()
            .getInGamePlayers()
            .stream()
            .map(dto -> new OnlinePlayer(dto.getUsername(), dto.getScore()))
            .collect(Collectors.toList());
    }
    
    public Client getClientByUsername(String username) {
        return clientByUsername.getOrDefault(username, null);
    }
}
