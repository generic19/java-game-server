package com.mycompany.javagameserver.handling;

import com.mycompany.database.LoginResult;
import com.mycompany.database.RegisterResult;
import static com.mycompany.database.RegisterResult.ALREADY_REGISTERD;
import static com.mycompany.database.RegisterResult.DB_ERROR;
import static com.mycompany.database.RegisterResult.REGISTERD_SUCCESSFULLY;
import com.mycompany.database.UserDAO;
import com.mycompany.database.UserDAOImpl;
import com.mycompany.database.UserDTO;
import com.mycompany.database.UserDTOImpl;
import com.mycompany.javagameserver.Client;
import com.mycompany.javagameserver.services.ClientService;
import com.mycompany.networking.Message;
import com.mycompany.networking.authentication.RegisterRequest;
import com.mycompany.networking.authentication.RegisterRespose;
import com.mycompany.networking.authentication.SignInRequest;
import com.mycompany.networking.authentication.SignInResponse;
import com.mycompany.networking.authentication.SignInWithTokenRequest;
import com.mycompany.networking.authentication.SignInWithTokenResponse;
import com.mycompany.networking.authentication.SignOutRequest;
import com.mycompany.networking.authentication.SignOutRespons;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

/**
 *
 * @author AhmedAli
 */
public class AuthHandler implements Handler {
    
    private Client client;
    private Handler handler;

    @Override
    public void bind(Client client) {
        this.client = client;
    }

    @Override
    public void handle(Request request) {
        if (request.getMessage() instanceof RegisterRequest) {
            /*
            1. casting massege to registerRequest
            2. extract userName , password from registerRequest
            3. creating object from UserDto and give it userName , password
            4. creating object from dao to make register
            5. calling Register
            6. creating Response based on result of register
            7. sendMessage using client
             */
            RegisterRequest registerRequest = (RegisterRequest) request.getMessage();
            
            // Hash the Password
            String hashedPassword = getHashedPassword(registerRequest.getPassword());
            // generate token
            String generateToken = generateToken();
            
            UserDTO user = new UserDTOImpl(registerRequest.getUsername(), hashedPassword, generateToken);
            UserDAO userDAO = new UserDAOImpl();
            RegisterResult result = userDAO.register(user);
            
            Message message;
            switch (result) {
                case DB_ERROR:
                    message = new RegisterRespose(false, null, "Something went wrong");
                    break;
                case ALREADY_REGISTERD:
                    message = new RegisterRespose(false, null, "Already Registered. Login instead.");
                    break;
                case REGISTERD_SUCCESSFULLY:
                    message = new RegisterRespose(true, generateToken, null);
                    ClientService.getService().setUsername(client, registerRequest.getUsername());
                    break;
                default:
                    System.out.println("Unknown Error");
                    message = new RegisterRespose(false, null, "Unknown Error");
                    break;
            }
            
            client.sendMessage(message);
        } else if (request.getMessage() instanceof SignInRequest) {
            
            SignInRequest signInRequest = (SignInRequest) request.getMessage();
            
            String hashedPassword = getHashedPassword(signInRequest.getPassword());
            
            String generateToken = generateToken();
            
            UserDTO user = new UserDTOImpl(signInRequest.getUserName(), hashedPassword, generateToken);
            UserDAO userDAO = new UserDAOImpl();
            LoginResult result = userDAO.login(user);
            
            Message message;
            switch (result) {
                case DB_ERROR:
                    message = new SignInResponse(false, null, "Something went wrong");
                    break;
                case WRONG_USERNAME_OR_PASSWORD:
                    message = new SignInResponse(false, null, "WRONG USERNAME OR PASSWORD.");
                    break;
                case ALREADY_LOGGED_IN:
                    message = new SignInResponse(false, null, "ALREADY LOGGED IN.");
                    break;
                case LOGGED_IN_SUCCESSFULLY:
                    message = new SignInResponse(true, generateToken, null);
                    ClientService.getService().setUsername(client, signInRequest.getUserName());
                    break;
                default:
                    System.out.println("Unknown Error");
                    message = new SignInResponse(false, null, "Unknown Error");
                    break;
            }
            
            client.sendMessage(message);
        } else if (request.getMessage() instanceof SignInWithTokenRequest) {
            
            Message message;
            SignInWithTokenRequest signInWithTokenRequest = (SignInWithTokenRequest) request.getMessage();
            
            UserDAO userDAO = new UserDAOImpl();
            String userName = userDAO.loginWithToken(signInWithTokenRequest.getToken());
            
            if(userName == null){
                message = new SignInWithTokenResponse(false);
            } else {
                message = new SignInWithTokenResponse(true);
                ClientService.getService().setUsername(client, userName);
            }
            
            client.sendMessage(message);
        } else if (request.getMessage() instanceof SignOutRequest) {
            
            SignOutRequest signOutRequest = (SignOutRequest) request.getMessage();
            
            UserDAO userDAO = new UserDAOImpl();
            boolean result = userDAO.logOut(signOutRequest.getUserName());
            
            Message message = new SignOutRespons(result);
            
            client.sendMessage(message);
            
            ClientService.getService().setUsername(client, null);
        } else {
            handler.handle(request);
        }
    }

    @Override
    public void setNext(Handler handler) {
        this.handler = handler;
    }
    
    private String getHashedPassword(String password){
        
        String hashPassword;
        try {
            // get password Hashed
            hashPassword = hashPasswordBase64(password);
        } catch (NoSuchAlgorithmException ex) {
            hashPassword = password;
            System.out.println("password can not be hashed");
        }
        return hashPassword;
    }
    
    private String hashPasswordBase64(String password) throws NoSuchAlgorithmException {
        // Create a MessageDigest instance for SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Hash the password (get the byte array of the hashed password)
        byte[] hashedBytes = digest.digest(password.getBytes());

        // Return the base64-encoded version of the hashed bytes
        return Base64.getEncoder().encodeToString(hashedBytes);
    }
    
    private String generateToken(){
        return UUID.randomUUID().toString();
    }
}
