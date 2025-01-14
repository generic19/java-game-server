/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.database;

/**
 *
 * @author ArwaKhaled
 */
public interface UserDAO {
    
     void register(UserDTO user);
     void login(UserDTO user);
     void logOut();
     
    
    
}
