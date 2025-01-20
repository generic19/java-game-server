package com.mycompany.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAOImpl implements UserDAO {

    @Override
    public RegisterResult register(UserDTO user) {
        String query = "INSERT INTO users (user_name, password_hash, token) VALUES (?, ?, ?)";
        
        RegisterResult registerResult = RegisterResult.DB_ERROR;
        
        if(isUserExist(user.getUsername())){
           registerResult = RegisterResult.ALREADY_REGISTERD; 
        } else {
           try (Connection connection = Database.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPasswordHash());
                preparedStatement.setString(3, user.getToken());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    registerResult = RegisterResult.REGISTERD_SUCCESSFULLY;
                    
                    updateOnlineStatus(user.getUsername(), true);
                    updateAvailableStatus(user.getUsername(), true);
                    
                    System.out.println("User registered successfully.");
                } 
                
                preparedStatement.close();
            } catch (Exception e) {
                registerResult = RegisterResult.DB_ERROR;
                e.printStackTrace();
            } 
        }
        
        return registerResult;
    }
    
    public boolean isUserExist (String userName){
        boolean isExist = true;
        String query = "SELECT * FROM users WHERE user_name = ?";
        
        try {
            Connection connection = Database.getInstance().getConnection();
            PreparedStatement prepareStatement = connection.prepareStatement(query);
            
            prepareStatement.setString(1, userName);
            ResultSet result = prepareStatement.executeQuery();
            
            isExist = result.next();
            
            prepareStatement.close();
            result.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(UserDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return isExist;
    }
    
    private boolean updateOnlineStatus(String userName, boolean isOnline){
        boolean isUpdate = true;
        String query = "UPDATE users set is_online = ? WHERE user_name = ?";
        
        try (Connection connection = Database.getInstance().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setBoolean(1, isOnline);
            preparedStatement.setString(2, userName);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            isUpdate = false;
            e.printStackTrace();
        }
        
        return isUpdate;
    }
    
    private boolean updateAvailableStatus(String userName, boolean isAvailable){
        boolean isUpdate = true;
        String query = "UPDATE users set is_available = ? WHERE user_name = ?";
        
        try (Connection connection = Database.getInstance().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setBoolean(1, isAvailable);
            preparedStatement.setString(2, userName);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            isUpdate = false;
            e.printStackTrace();
        }
        
        return isUpdate;
    }
    
    private boolean updateToken(String userName, String token){
        boolean isUpdate = true;
        String query = "UPDATE users set token = ? WHERE user_name = ?";
        
        try (Connection connection = Database.getInstance().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, token);
            preparedStatement.setString(2, userName);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            isUpdate = false;
            e.printStackTrace();
        }
        
        return isUpdate;
    }


    @Override
    public LoginResult login(UserDTO user) {
        LoginResult loginResult = LoginResult.DB_ERROR;
        String query = "SELECT * FROM users WHERE user_name = ? AND password_hash = ?";
        try (Connection connection = Database.getInstance().getConnection()) {
            
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if(resultSet.getBoolean("is_online")){
                    loginResult = LoginResult.ALREADY_LOGGED_IN;
                } else{
                    loginResult = LoginResult.LOGGED_IN_SUCCESSFULLY;
                    updateOnlineStatus(user.getUsername(), true);
                    updateAvailableStatus(user.getUsername(), true);
                    updateToken(user.getUsername(), user.getToken());
                }
            } else {
                loginResult = LoginResult.WRONG_USERNAME_OR_PASSWORD;
                System.out.println("Wrong UserName or Password");
            }
            
            preparedStatement.close();
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
        updateToken(userName, null);
        return updateOnlineStatus(userName, false);
        
    }

    @Override
    public String loginWithToken(String token) {
        String userName = null;
        
        String query = "SELECT * FROM users WHERE token = ?"; 
        
        try (Connection connection = Database.getInstance().getConnection()) {
            
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, token);
            

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userName = resultSet.getString("user_name");
                updateOnlineStatus(userName, true);
                updateAvailableStatus(userName, true);
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
