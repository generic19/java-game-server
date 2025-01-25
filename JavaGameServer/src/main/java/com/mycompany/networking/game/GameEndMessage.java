/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.networking.game;


public class GameEndMessage implements GameMessage {

    private final boolean winner;
    private final boolean loser;
    private final int score;

 
    public GameEndMessage(boolean isWinner, boolean isLoser, int score) {
        this.winner = isWinner;
        this.loser = isLoser;
        this.score = score;
    }

    public boolean isWinner() {
        return winner;
    }

    public boolean isLoser() {
        return loser;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "GameEndMessage{" + "winner=" + winner + ", loser=" + loser + ", score=" + score + '}';
    }
}

