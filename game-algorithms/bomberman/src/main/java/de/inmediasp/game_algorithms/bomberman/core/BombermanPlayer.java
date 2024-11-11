package de.inmediasp.game_algorithms.bomberman.core;

public interface BombermanPlayer {
    String getName();

    BombermanPlayerAction getNextAction(BombermanGameState gameState);
}

