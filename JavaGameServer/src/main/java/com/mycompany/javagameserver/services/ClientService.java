/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
package com.mycompany.javagameserver.services;

import com.mycompany.javagameserver.Client;
import com.mycompany.networking.OnlinePlayer;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author basel
 */
public interface ClientService {
    static ClientService getService() {
        return new ClientServiceImpl();
    }
    
    void addClient(Client client);
    void removeClient(Client client);
    
    Collection<Client> getClients();
    
    void setUsername(Client client, String username);
    
    void setIsOnline(Client client, boolean isOnline);
    void setIsInGame(Client client, boolean isInGame);
    
    List<OnlinePlayer> getAvailable();
    List<OnlinePlayer> getInGame();
    
    void addPlayerUpdateListener(PlayerUpdateListener listener);
    void removePlayerUpdateListener(PlayerUpdateListener listener);
    
    Client getClientByUsername(String username);
    
    @FunctionalInterface
        interface PlayerUpdateListener {
        void onPlayerUpdate(String username, boolean isAdd, boolean isRemove, boolean isAvailable, boolean isInGame);
    }
}
