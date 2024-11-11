package de.inmediasp.game_algorithms;

public interface Game<TState> {
    boolean nextTurn();
    TState getState();
}
