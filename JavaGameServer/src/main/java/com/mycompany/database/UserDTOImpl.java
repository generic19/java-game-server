package com.mycompany.database;


public class UserDTOImpl implements UserDTO {
    private String username;
    private String passwordHash;
    private String token;

   
    public UserDTOImpl(String username, String passwordHash, String token) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.token = token;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String getToken() {
        return token;
    }
}
