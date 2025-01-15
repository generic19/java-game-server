/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database;

/**
 *
 * @author ArwaKhaled
 */
public class PlayerDTOImpl implements PlayerDTO {
    String userName ;
    int score;
    boolean isAvailable ;

    public PlayerDTOImpl(String userName, int score, boolean isAvailable) {
        this.userName = userName;
        this.score = score;
        this.isAvailable = isAvailable;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public int getScore() {   
        return score;
    }

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }
    
}
