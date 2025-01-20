package com.mycompany.javagameserver.handling;

import com.mycompany.javagameserver.Client;
import com.mycompany.networking.authentication.RegisterRequest;
import com.mycompany.networking.authentication.SignInRequest;
import com.mycompany.networking.authentication.SignInWithTokenRequest;
import com.mycompany.networking.authentication.SignOutRequest;

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
            // do some logic
            
            // ClientServiceImpl.getInstance(); --> return client
        } else if (request.getMessage() instanceof SignInRequest) {
            // do some logic
            
            // ClientServiceImpl.getInstance(); --> return client
        } else if (request.getMessage() instanceof SignInWithTokenRequest) {
            // do some logic
            
            // ClientServiceImpl.getInstance(); --> return client
        } else if (request.getMessage() instanceof SignOutRequest) {
            // do some logic
            
            // null username
        } else {
            handler.handle(request);
        }
    }

    @Override
    public void setNext(Handler handler) {
        this.handler = handler;
    }
}
