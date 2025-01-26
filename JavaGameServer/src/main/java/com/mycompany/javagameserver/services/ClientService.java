package com.mycompany.javagameserver.services;

import com.mycompany.database.PlayerDAO;
import com.mycompany.javagameserver.Client;
import com.mycompany.javagameserver.handling.*;
import com.mycompany.networking.OnlinePlayer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 * @author basel
 */
public class ClientService implements ClientAuthListener, ClientMatchingEntryListener, ClientGameStatusListener {
    
    private static volatile ClientService service;
    
    private final Set<Client> clients = ConcurrentHashMap.<Client>newKeySet();
    private final Map<String, Client> clientByUsername = new ConcurrentHashMap<>();
    private final Map<Client, String> usernameByClient = new ConcurrentHashMap<>();
    
    final List<ClientsListener> clientsListeners = Collections.synchronizedList(new ArrayList<>());
    final List<ClientAvailabilityListener> clientAvailabilityListeners = Collections.synchronizedList(new ArrayList<>());
    
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
        
        synchronized (clientsListeners) {
            clientsListeners.forEach(l -> l.onClientAdded(client));
        }
        
        client.getAuthHandler().setListener(this);
        client.getMatchingHandler().setListener(this);
        client.getGameHandler().setListener(this);
    }
    
    public void removeClient(Client client) {
        String username = client.getAuthHandler().getUsername();
        
        clients.remove(client);
        clientByUsername.values().remove(client);
        usernameByClient.remove(client);
        
        client.getAuthHandler().setListener(null);
        client.getMatchingHandler().setListener(null);
        client.getGameHandler().setListener(null);
        
        if (username != null) {
            PlayerDAO.getInstance().setPlayerOnline(username, false);
        }
        
        synchronized (clientsListeners) {
            clientsListeners.forEach(l -> l.onClientRemoved(client));
        }
    }
    
    public Collection<Client> getClients() {
        return Collections.unmodifiableCollection(clients);
    }
    
    public int getOnlineCount() {
        return PlayerDAO.getInstance().getOnlineCount();
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
    
    @Override
    public String toString() {
        return "ClientService{" + "clients=" + clients + ", clientByUsername.keys=" + clientByUsername.keySet() + ", usernameByClient.values=" + usernameByClient.values() + '}';
    }
    
    public void addClientsListener(ClientsListener listener) {
        this.clientsListeners.add(listener);
    }
    
    public void removeClientsListener(ClientsListener listener) {
        this.clientsListeners.remove(listener);
    }
    
    public void addClientAvailabilityListener(ClientAvailabilityListener listener) {
        this.clientAvailabilityListeners.add(listener);
    }
    
    public void removeClientAvailabilityListener(ClientAvailabilityListener listener) {
        this.clientAvailabilityListeners.remove(listener);
    }
    
    @Override
    public void onClientStartedGame(Client client) {
        String username = client.getAuthHandler().getUsername();
        
        if (username != null) {
            PlayerDAO.getInstance().setPlayerAvailable(username, false);
        }
        
        synchronized (clientAvailabilityListeners) {
            clientAvailabilityListeners.forEach(l -> l.onClientAvailablilityChanged(client, false));
        }
    }
    
    @Override
    public void onClientEndedGame(Client client) {
        String username = client.getAuthHandler().getUsername();
        
        if (username != null) {
            PlayerDAO.getInstance().setPlayerAvailable(username, true);
        }
        
        synchronized (clientAvailabilityListeners) {
            clientAvailabilityListeners.forEach(l -> l.onClientAvailablilityChanged(client, true));
        }
    }
    
    @Override
    public void onClientSignedIn(Client client) {
        String username = client.getAuthHandler().getUsername();
        
        usernameByClient.put(client, username);
        clientByUsername.put(username, client);
        
        PlayerDAO.getInstance().setPlayerOnline(username, true);
    }
    
    @Override
    public void onClientWillSignOut(Client client) {
        usernameByClient.remove(client);
        clientByUsername.values().remove(client);
        
        String username = client.getAuthHandler().getUsername();
        
        if (username != null) {
            PlayerDAO.getInstance().setPlayerOnline(username, false);
        }
    }
    
    @Override
    public void onClientEnteredMatching(Client client) {
        synchronized (clientAvailabilityListeners) {
            clientAvailabilityListeners.forEach(l -> l.onClientAvailablilityChanged(client, true));
        }
    }
    
    public interface ClientsListener {
        void onClientAdded(Client client);
        void onClientRemoved(Client client);
    }
    
    public interface ClientAvailabilityListener {
        void onClientAvailablilityChanged(Client client, boolean isAvailable);
    }
}
