package com.mycompany.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAOImpl implements UserDAO {

 
    @Override
    public void register(UserDTO user) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        
        try (Connection connection =Database.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

           
            String hashedPassword = (user.getPasswordHash());

           
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, hashedPassword);

            
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User registered successfully.");
            } else {
                System.out.println("Error registering user.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
   public void login(UserDTO user) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (Connection connection =Database.getInstance().getConnection()){
             PreparedStatement preparedStatement = connection.prepareStatement(query) ;

       
            preparedStatement.setString(1, user.getUsername());

            
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String storedHashedPassword = resultSet.getString("password");

               
                if (user.getPasswordHash().equals(storedHashedPassword)) {
                    System.out.println("Login successful. Welcome, " + user.getUsername() + "!");
                } else {
                    System.out.println("Invalid username or password.");
                }
            } else {
                System.out.println("User not found.");
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    @Override
    public void logOut() {
        
    }

   
}
