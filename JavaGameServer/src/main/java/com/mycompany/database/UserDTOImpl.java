package com.mycompany.database;


public class UserDTOImpl implements UserDTO {
    private String username;
    private String passwordHash;

   
    public UserDTOImpl(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPasswordHash() {
        return passwordHash;
    }

 
    
}
