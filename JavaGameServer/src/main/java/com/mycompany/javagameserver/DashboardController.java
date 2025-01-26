/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
*/
package com.mycompany.javagameserver;

import com.mycompany.database.PlayerDAO;
import com.mycompany.javagameserver.services.ClientService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;

/**
 * FXML Controller class
 *
 * @author basel
 */
public class DashboardController implements Initializable, Server.Listener, ClientService.ClientsListener, ClientService.ClientAvailabilityListener {
    
    @FXML
    private HBox indicatorsContainer;
    @FXML
    private Circle serverOnlineShape;
    @FXML
    private Circle serverOfflineShape;
    @FXML
    private Label lblServerStatus;
    @FXML
    private Canvas activePlayersCanvas;
    @FXML
    private Label lblActivePlayersCount;
    @FXML
    private Canvas onlinePlayersCanvas;
    @FXML
    private Label lblOnlinePlayersCount;
    @FXML
    private Canvas offlinePlayersCanvas;
    @FXML
    private Label lblOfflinePlayersCount;
    @FXML
    private TextArea txtLog;
    
    private Server server;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        server = Server.getInstance();
        server.setListener(this);
        
        serverOnlineShape.visibleProperty().bindBidirectional(serverOnlineShape.managedProperty());
        serverOfflineShape.visibleProperty().bindBidirectional(serverOfflineShape.managedProperty());
        
        serverOnlineShape.setVisible(false);
        serverOfflineShape.setVisible(true);
        lblServerStatus.setText("Server Offline");
        
        txtLog.setText("Server ready to start.");
    }
    
    @FXML
    private void onStartServer(ActionEvent event) {
        server.start();
        
        ClientService.getService().addClientsListener(this);
        ClientService.getService().addClientAvailabilityListener(this);
        
        serverOnlineShape.setVisible(true);
        serverOfflineShape.setVisible(false);
        lblServerStatus.setText("Server Online");
    }
    
    @FXML
    private void onStopServer(ActionEvent event) {
        server.stop();
        
        ClientService.getService().addClientsListener(this);
        ClientService.getService().addClientAvailabilityListener(this);
        
        serverOnlineShape.setVisible(false);
        serverOfflineShape.setVisible(true);
        lblServerStatus.setText("Server Offline");
    }
    
    private void updateGraphs() {
        int playerCount = ClientService.getService().getPlayerCount();
        int onlineCount = ClientService.getService().getOnlineCount();
        int inGameCount = ClientService.getService().getInGameCount();
        
        int offlineCount = playerCount - onlineCount;
        
        lblActivePlayersCount.setText("" + inGameCount);
        lblOnlinePlayersCount.setText("" + onlineCount);
        lblOfflinePlayersCount.setText("" + offlineCount);
        
        double activeFraction = 0;
        double onlineFraction = 0;
        double offlineFraction = 0;
        
        if (playerCount != 0) {
            activeFraction = inGameCount / (double) playerCount;
            onlineFraction = onlineCount / (double) playerCount;
            offlineFraction = offlineCount / (double) playerCount;
        }
        
        drawIndicator(activePlayersCanvas, activeFraction);
        drawIndicator(onlinePlayersCanvas, onlineFraction);
        drawIndicator(offlinePlayersCanvas, offlineFraction);
    }
    
    private void drawIndicator(Canvas canvas, double fraction) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        gc.clearRect(0, 0, 150, 150);
        
        gc.setStroke(Color.web("00A4E9"));
        gc.setLineCap(StrokeLineCap.BUTT);
        gc.setLineWidth(5);
        
        gc.strokeArc(5, 5, 140, 140, 90, -359.99 * fraction, ArcType.OPEN);
    }
    
    @Override
    public void onServerThreadStarted() {
        Platform.runLater(() -> {
            txtLog.appendText("\nServer started.");
            
            Platform.runLater(() -> {
                updateGraphs();
                indicatorsContainer.setVisible(true);
            });
        });
    }
    
    @Override
    public void onServerThreadStopping() {
        Platform.runLater(() -> {
            txtLog.appendText("\nServer stopped.");
            indicatorsContainer.setVisible(false);
        });
    }
    
    @Override
    public void onClientAdded(Client client) {
        Platform.runLater(() -> {
            txtLog.appendText("\nClient (" + client.getRepresentation() + ") connected.");
            updateGraphs();
        });
    }
    
    @Override
    public void onClientRemoved(Client client) {
        Platform.runLater(() -> {
            txtLog.appendText("\nClient (" + client.getRepresentation() + ") disconnected.");
            updateGraphs();
        });
    }
    
    @Override
    public void onClientAvailablilityChanged(Client client, boolean isAvailable) {
        Platform.runLater(() -> {
            String status = isAvailable ? "available" : "in game";
            txtLog.appendText("\nClient (" + client.getRepresentation() + ") is " + status + ".");
            
            updateGraphs();
        });
    }
}
