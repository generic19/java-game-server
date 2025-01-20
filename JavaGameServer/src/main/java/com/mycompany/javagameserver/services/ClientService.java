package com.mycompany.javagameserver.services;

import com.mycompany.database.PlayerDAO;
import com.mycompany.javagameserver.Client;
import com.mycompany.networking.OnlinePlayer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collector;
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
    
    private final List<PlayerUpdateListener> listeners = new CopyOnWriteArrayList<>();
    
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
        clients.remove(client);
        clientByUsername.values().remove(client);
    }
    
    public Collection<Client> getClients() {
        return Collections.unmodifiableCollection(clients);
    }
    
    public void setUsername(Client client, String username) {
        if (username != null) {
            clientByUsername.put(username, client);
            usernameByClient.put(client, username);
        } else {
            clientByUsername.values().remove(client);
            usernameByClient.remove(client);
        }
    }
    
    public void setIsOnline(Client client, boolean isOnline) {
        String username = usernameByClient.getOrDefault(client, null);
        
        if (username == null) {
            throw new IllegalStateException("Cannot use setIsOnline before setting username for client.");
        }
        
        PlayerDAO.getInstance().setPlayerOnline(username, isOnline);
        
    }
    
    public void setIsInGame(Client client, boolean isInGame) {
        String username = usernameByClient.getOrDefault(client, null);
        
        if (username == null) {
            throw new IllegalStateException("Cannot use setIsInGame before setting username for client.");
        }
        
        PlayerDAO.getInstance().setPlayerOnline(username, isInGame);
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
    
    public void addPlayerUpdateListener(PlayerUpdateListener listener) {
        listeners.add(listener);
    }
    
    public void removePlayerUpdateListener(PlayerUpdateListener listener) {
        listeners.remove(listener);
    }
    
    public Client getClientByUsername(String username) {
        return clientByUsername.getOrDefault(username, null);
    }
    
    @FunctionalInterface
    public interface PlayerUpdateListener {
        void onPlayerUpdate(String username, boolean isAdd, boolean isRemove, boolean isAvailable, boolean isInGame);
    }
}
