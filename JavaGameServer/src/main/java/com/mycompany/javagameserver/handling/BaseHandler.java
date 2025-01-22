package com.mycompany.javagameserver.handling;

import com.mycompany.javagameserver.Client;

/**
 *
 * @author basel
 */
public final class BaseHandler implements Handler {
    private Client client;
    private Handler next;
    
    @Override
    public void bind(Client client) {
        this.client = client;
    }
    
    @Override
    public void handle(Request request) {
        if (next != null) {
            next.handle(request);
        }
    }
    
    @Override
    public void setNext(Handler handler) {
        this.next = handler;
    }
}
