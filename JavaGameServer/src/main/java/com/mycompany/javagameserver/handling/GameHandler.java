/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javagameserver.handling;

import com.mycompany.database.PlayerDAO;
import com.mycompany.database.PlayerScoreUpdateDTOImp;
import com.mycompany.game.*;
import com.mycompany.javagameserver.Client;
import com.mycompany.javagameserver.services.ClientService;
import com.mycompany.networking.Message;
import com.mycompany.networking.OnlinePlayer;
import com.mycompany.networking.game.*;

/**
 *
 * @author basel
 */
public class GameHandler implements Handler, Game.Listener {

    private Client client;
    private Handler next;

    private Game game;
    private OnlinePlayer player;
    private OnlinePlayer opponent;
    private Player playerTurn;
    private Client opponentClient;

    public GameHandler() {

    }

    @Override
    public void bind(Client client) {
        this.client = client;
    }

    @Override
    public void handle(Request request) {
        if (game != null) {
            Message message = request.getMessage();

            if (message instanceof GameMoveMessage) {
                GameMove move = ((GameMoveMessage) message).getMove();
                GameState state = game.getState();
                
                if (state.isValidMove(move)) {
                    game.play(move);
                }
            } else if (message instanceof GameLeaveMessage) {
                endGame(false, true);
                opponentClient.getGameHandler().endGame(true, false);
            }
        }
    }

    @Override
    public void setNext(Handler handler) {
        this.next = handler;
    }

    protected void setOpponent(OnlinePlayer opponent) {
        this.opponent = opponent;
    }

    public void startGame(Game game, OnlinePlayer player, OnlinePlayer opponent, Player playerTurn) {
        this.game = game;
        this.player = player;
        this.opponent = opponent;
        this.playerTurn = playerTurn;
        this.opponentClient = ClientService.getService().getClientByUsername(opponent.getUsername());

        this.opponentClient.getGameHandler().setOpponent(player);

        this.game.addListener(this);

        client.sendMessage(new GameStartMessage(player, opponent, playerTurn));
    }

    public void endGame(boolean isWinner, boolean isLoser) {
        int score = PlayerDAO.getInstance().getScore(player.getUsername());

        if (isWinner) {
            score += 25;
            PlayerDAO.getInstance().updatePlayerScore(new PlayerScoreUpdateDTOImp(player.getUsername(), score));
        }

        client.sendMessage(new GameEndMessage(isWinner, isLoser, score));

        this.game.removeListener(this);

        this.game = null;
        this.player = null;
        this.opponent = null;
        this.playerTurn = null;
        this.opponentClient = null;
    }

    @Override
    public void onStateChange(GameState newState) {
        client.sendMessage(new GameStateMessage(newState));

        if (newState.isEndState()) {
            Player winnerPlayer = newState.getWinner();

            boolean isWinner = winnerPlayer == playerTurn;
            boolean isLoser = winnerPlayer != null && winnerPlayer != playerTurn;

            endGame(isWinner, isLoser);
        }
    }
}
