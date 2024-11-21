package game_algorithms.core;

public interface Game<TState> {
    boolean nextTurn();
    TState getState();
}
