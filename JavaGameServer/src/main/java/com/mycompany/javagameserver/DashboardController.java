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
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;

/**
 * FXML Controller class
 *
 * @author basel
 */
public class DashboardController implements Initializable, Server.Listener {

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

    private final Server server = new Server(5005);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        server.setListener(this);

        PlayerDAO.getInstance().addListener((username, isAdd, isRemove, isAvailable, isInGame) -> {
            Platform.runLater(() -> {
                updateGraphs();

                StringBuilder b = new StringBuilder(username + " is ");

                if (isRemove) {
                    b.append("not ");
                }

                b.append(isAvailable ? "available" : "in game");
                b.append(".\n");

                txtLog.appendText(b.toString());
            });
        });

        serverOnlineShape.visibleProperty().bindBidirectional(serverOnlineShape.managedProperty());
        serverOfflineShape.visibleProperty().bindBidirectional(serverOfflineShape.managedProperty());

        serverOnlineShape.setVisible(false);
        serverOfflineShape.setVisible(true);
        lblServerStatus.setText("Server Offline");

        updateGraphs();
    }

    @FXML
    private void onStartServer(ActionEvent event) {
        server.start();

        serverOnlineShape.setVisible(true);
        serverOfflineShape.setVisible(false);
        lblServerStatus.setText("Server Online");
    }

    @FXML
    private void onStopServer(ActionEvent event) {
        server.stop();

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

        double activeFraction = inGameCount / (double) playerCount;
        double onlineFraction = onlineCount / (double) playerCount;
        double offlineFraction = offlineCount / (double) playerCount;

        drawIndicator(activePlayersCanvas, activeFraction);
        drawIndicator(onlinePlayersCanvas, onlineFraction);
        drawIndicator(offlinePlayersCanvas, offlineFraction);
    }

    private void drawIndicator(Canvas canvas, double fraction) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.WHITE);
        gc.fill();

        gc.setStroke(Color.web("00A4E9"));
        gc.setLineCap(StrokeLineCap.BUTT);
        gc.setLineWidth(5);

        gc.strokeArc(5, 5, 140, 140, 0, 360 * fraction, ArcType.OPEN);
    }

    @Override
    public void onServerThreadStarted() {
    }

    @Override
    public void onServerThreadStopping() {
    }
}
