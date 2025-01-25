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
    private String username;

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
            RegisterResult result = UserDAO.getInstance().register(user);
            
            Message message;
            switch (result) {
                case DB_ERROR:
                    message = new RegisterRespose(false, null, "Something went wrong");
                    username = null;
                    break;
                case ALREADY_REGISTERD:
                    message = new RegisterRespose(false, null, "Already Registered. Login instead.");
                    username = null;
                    break;
                case REGISTERD_SUCCESSFULLY:
                    message = new RegisterRespose(true, generateToken, null);
                    username = registerRequest.getUsername();
                    ClientService.getService().setUsername(client, registerRequest.getUsername());
                    break;
                default:
                    System.out.println("Unknown Error");
                    username = null;
                    message = new RegisterRespose(false, null, "Unknown Error");
                    break;
            }
            
            client.sendMessage(message);
        } else if (request.getMessage() instanceof SignInRequest) {
            
            SignInRequest signInRequest = (SignInRequest) request.getMessage();
            
            String hashedPassword = getHashedPassword(signInRequest.getPassword());
            
            String generateToken = generateToken();
            
            UserDTO user = new UserDTOImpl(signInRequest.getUserName(), hashedPassword, generateToken);
            LoginResult result = UserDAO.getInstance().login(user);
            
            Message message;
            switch (result) {
                case DB_ERROR:
                    message = new SignInResponse(false, null, "Something went wrong");
                    username = null;
                    break;
                case WRONG_USERNAME_OR_PASSWORD:
                    message = new SignInResponse(false, null, "WRONG USERNAME OR PASSWORD.");
                    username = null;
                    break;
                case ALREADY_LOGGED_IN:
                    message = new SignInResponse(false, null, "ALREADY LOGGED IN.");
                    username = null;
                    break;
                case LOGGED_IN_SUCCESSFULLY:
                    message = new SignInResponse(true, generateToken, null);
                    username = signInRequest.getUserName();
                    ClientService.getService().setUsername(client, signInRequest.getUserName());
                    break;
                default:
                    System.out.println("Unknown Error");
                    username = null;
                    message = new SignInResponse(false, null, "Unknown Error");
                    break;
            }
            
            client.sendMessage(message);
        } else if (request.getMessage() instanceof SignInWithTokenRequest) {
            
            Message message;
            SignInWithTokenRequest signInWithTokenRequest = (SignInWithTokenRequest) request.getMessage();
            
            username = UserDAO.getInstance().loginWithToken(signInWithTokenRequest.getToken());
            
            if(username == null){
                message = new SignInWithTokenResponse(false);
            } else if (ClientService.getService().getClientByUsername(username)!=null&&ClientService.getService().getClientByUsername(username)!=client){
                 message = new SignInWithTokenResponse(false);
                 System.out.println("Login attempt denied. User " + username + " is already logged in.");
            }
            else {
                
                message = new SignInWithTokenResponse(true);
                ClientService.getService().setUsername(client, username);
            }
            
            client.sendMessage(message);
        } else if (request.getMessage() instanceof SignOutRequest) {            
            if (username != null) {
                boolean result = UserDAO.getInstance().logOut(username);
                
                Message message = new SignOutRespons(result);
                
                client.sendMessage(message);
                
                ClientService.getService().setUsername(client, null);
            }
        } else if (username != null) {
            handler.handle(new AuthenticatedRequest(username, request.getMessage()));
        }
    }

    @Override
    public void setNext(Handler handler) {
        this.handler = handler;
    }

    public String getUsername() {
        return username;
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
