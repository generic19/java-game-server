package com.mycompany.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAOImpl implements UserDAO {
    
    private static UserDAOImpl instance;
    
    public static UserDAOImpl getInstance() {
        if (instance == null) {
            synchronized (UserDAOImpl.class) {
                if (instance == null) {
                    instance = new UserDAOImpl();
                }
            }
        }
        return instance;
    }
    
    private UserDAOImpl() {}
    
    @Override
    public RegisterResult register(UserDTO user) {
        String query = "INSERT INTO users (user_name, password_hash, token, is_online, is_available) VALUES (?, ?, ?, 1, 1)";
        
        RegisterResult registerResult = RegisterResult.DB_ERROR;
        
        if(checkUserExists(user.getUsername())){
            registerResult = RegisterResult.ALREADY_REGISTERD;
        } else {
            try {
                Connection connection = Database.getInstance().getConnection();
                
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, user.getUsername());
                    stmt.setString(2, user.getPasswordHash());
                    stmt.setString(3, user.getToken());
                    
                    int rowsAffected = stmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        registerResult = RegisterResult.REGISTERD_SUCCESSFULLY;
                    }
                }
            } catch (SQLException e) {
                registerResult = RegisterResult.DB_ERROR;
            }
        }
        
        return registerResult;
    }
    
    public boolean checkUserExists(String userName) {
        String query = "SELECT 1 FROM users WHERE user_name = ? LIMIT 1";
        
        try {
            Connection connection = Database.getInstance().getConnection();
            
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, userName);
                
                try (ResultSet result = stmt.executeQuery()) {
                    return result.next();
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private boolean setOnlineStatus(String userName, boolean isOnline){
        boolean isUpdate = true;
        String query = "UPDATE users set is_online = ? WHERE user_name = ?";
        
        try {
            Connection connection = Database.getInstance().getConnection();
            
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setBoolean(1, isOnline);
                stmt.setString(2, userName);
                
                stmt.executeUpdate();
                
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }
    
    private boolean setAvailableStatus(String userName, boolean isAvailable){
        String query = "UPDATE users set is_available = ? WHERE user_name = ?";
        
        try {
            Connection connection = Database.getInstance().getConnection();
            
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setBoolean(1, isAvailable);
                stmt.setString(2, userName);
                
                stmt.executeUpdate();
                
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }
    
    private boolean setToken(String userName, String token){
        String query = "UPDATE users set token = ? WHERE user_name = ?";
        
        try {
            Connection connection = Database.getInstance().getConnection();
            
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, token);
                stmt.setString(2, userName);
                
                stmt.executeUpdate();
                
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }
    
    @Override
    public LoginResult login(UserDTO user) {
        String query = "SELECT * FROM users WHERE user_name = ? AND password_hash = ?";
        try {
            Connection connection = Database.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                if(resultSet.getBoolean("is_online")){
                    loginResult = LoginResult.ALREADY_LOGGED_IN;
                } else{
                    loginResult = LoginResult.LOGGED_IN_SUCCESSFULLY;
                    setOnlineStatus(user.getUsername(), true);
                    setAvailableStatus(user.getUsername(), true);
                    setToken(user.getUsername(), user.getToken());
                }
            } else {
                loginResult = LoginResult.WRONG_USERNAME_OR_PASSWORD;
                System.out.println("Wrong UserName or Password");
            }
            
            stmt.close();
            resultSet.close();
        } catch (Exception e) {
            loginResult = LoginResult.DB_ERROR;
            System.out.println("Error DB");
            e.printStackTrace();
        }
        
        return loginResult;
    }
    
    @Override
    public boolean logOut(String userName) {
        setToken(userName, null);
        setAvailableStatus(userName, false);
        return setOnlineStatus(userName, false);
        
    }
    
    @Override
    public String loginWithToken(String token) {
        String userName = null;
        
        String query = "SELECT * FROM users WHERE token = ?";
        
        try {
            Connection connection = Database.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            
            preparedStatement.setString(1, token);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userName = resultSet.getString("user_name");
                setOnlineStatus(userName, true);
                setAvailableStatus(userName, true);
            } else {
                System.out.println("EXPIRED OR WRONG TOKEN");
            }
            
            preparedStatement.close();
            resultSet.close();
        } catch (Exception e) {
            System.out.println("Error DB");
            e.printStackTrace();
        }
        
        return userName;
    }
}
