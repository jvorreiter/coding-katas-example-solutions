package game_algorithms.core.bomberman;

public interface BombermanPlayer {
    String getName();

    BombermanPlayerAction getNextAction(BombermanGameState gameState);
}

