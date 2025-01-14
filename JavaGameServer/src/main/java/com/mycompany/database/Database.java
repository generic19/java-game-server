/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database;

import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author AhmedAli
 */
public class Database {
    private final Connection connection;
    private volatile static Database instance;
    
    private Database() throws SQLException {
        DriverManager.registerDriver(new Driver());
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javagame?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "");
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
