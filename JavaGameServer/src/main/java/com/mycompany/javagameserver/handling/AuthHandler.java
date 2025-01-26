package com.mycompany.javagameserver.handling;

import com.mycompany.database.LoginResult;
import com.mycompany.database.RegisterResult;
import static com.mycompany.database.RegisterResult.ALREADY_REGISTERD;
import static com.mycompany.database.RegisterResult.DB_ERROR;
import static com.mycompany.database.RegisterResult.REGISTERD_SUCCESSFULLY;
import com.mycompany.database.UserDAO;
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

    private ClientAuthListener listener;

    @Override
    public void bind(Client client) {
        this.client = client;
    }

    @Override
    public void handle(Request request) {
        Message message = request.getMessage();
        
        if (message instanceof RegisterRequest) {
            handleRegister((RegisterRequest) message);
        } else if (message instanceof SignInRequest) {
            handleSignIn((SignInRequest) message);
        } else if (message instanceof SignInWithTokenRequest) {
            handleSignInWithToken((SignInWithTokenRequest) message);
        } else if (message instanceof SignOutRequest) {
            handleSignOut();
        } else if (username != null) {
            handler.handle(new AuthenticatedRequest(username, message));
        }
    }

    private void handleRegister(RegisterRequest registerRequest) throws IllegalStateException {
        /*
            1. casting massege to registerRequest
            2. extract userName , password from registerRequest
            3. creating object from UserDto and give it userName , password
            4. creating object from dao to make register
            5. calling Register
            6. creating Response based on result of register
            7. sendMessage using client
         */

        // Hash the Password
        String hashedPassword = getHashedPassword(registerRequest.getPassword());
        // generate token
        String generateToken = generateToken();

        UserDTO user = new UserDTOImpl(registerRequest.getUsername(), hashedPassword, generateToken);
        RegisterResult result = UserDAO.getInstance().register(user);

        Message message;
        
        switch (result) {
            case REGISTERD_SUCCESSFULLY:
                message = new RegisterRespose(true, generateToken, null);
                username = registerRequest.getUsername();
                listener.onClientSignedIn(client);
                break;
            case DB_ERROR:
                message = new RegisterRespose(false, null, "Something went wrong");
                username = null;
                break;
            case ALREADY_REGISTERD:
                message = new RegisterRespose(false, null, "Already Registered. Login instead.");
                username = null;
                break;
            default:
                System.out.println("Unknown Error");
                username = null;
                message = new RegisterRespose(false, null, "Unknown Error");
                break;
        }

        client.sendMessage(message);
    }

    private void handleSignIn(SignInRequest signInRequest) {
        String hashedPassword = getHashedPassword(signInRequest.getPassword());

        String generateToken = generateToken();

        UserDTO user = new UserDTOImpl(signInRequest.getUserName(), hashedPassword, generateToken);
        LoginResult result = UserDAO.getInstance().login(user);

        Message message;
        switch (result) {
            case LOGGED_IN_SUCCESSFULLY:
                message = new SignInResponse(true, generateToken, null);
                username = signInRequest.getUserName();
                listener.onClientSignedIn(client);
                break;
                
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

            default:
                System.out.println("Unknown Error");
                username = null;
                message = new SignInResponse(false, null, "Unknown Error");
                break;
        }

        client.sendMessage(message);
    }

    private void handleSignInWithToken(SignInWithTokenRequest signInWithTokenRequest) throws IllegalStateException {
        username = UserDAO.getInstance().loginWithToken(signInWithTokenRequest.getToken());

        if (username == null) {
            client.sendMessage(new SignInWithTokenResponse(false));
        } else {
            Client usernameClient = ClientService.getService().getClientByUsername(username);

            if (usernameClient != null && usernameClient != client) {
                client.sendMessage(new SignInWithTokenResponse(false));
            } else {
                client.sendMessage(new SignInWithTokenResponse(true));
                listener.onClientSignedIn(client);
            }
        }
    }

    private void handleSignOut() throws IllegalStateException {
        if (username != null) {
            UserDAO.getInstance().logOut(username);

            listener.onClientWillSignOut(client);
            username = null;
            client.sendMessage(new SignOutRespons(true));
        }
    }

    @Override
    public void setNext(Handler handler) {
        this.handler = handler;
    }

    public String getUsername() {
        return username;
    }

    private String getHashedPassword(String password) {

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

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "AuthHandler{username=" + username + '}';
    }

    public void setListener(ClientAuthListener Listener) {
        this.listener = Listener;
    }
}
