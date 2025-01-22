package com.mycompany.database;

/**
 *
 * @author ArwaKhaled
 */
public interface UserDAO {

    RegisterResult register(UserDTO user);
    LoginResult login(UserDTO user);
    String loginWithToken(String token);
    boolean logOut(String userName);
}
