/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database;

import java.sql.*;
import org.mariadb.jdbc.Driver;
/**
 *
 * @author AhmedAli
 */
public class Database {
    private final Connection connection;
    private volatile static Database instance;
    
    private Database() throws SQLException {
        DriverManager.registerDriver(new Driver());
        connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/JavaGame", "JavaGame", "JavaGame");
        
        prepareDatabase();
    }
    
    private void prepareDatabase() throws SQLException {
        String sql = "UPDATE users SET is_online = false, is_available = true";
                    
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
    
    public static Database getInstance() throws SQLException{
        if(instance == null){
            synchronized (Database.class) {
                if(instance == null){
                    instance = new Database();
                }  
            }
            
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }    
}
