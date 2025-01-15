package com.mycompany.database;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
      
    }

    @Override
    public void logOut() {
        
    }

   
}
