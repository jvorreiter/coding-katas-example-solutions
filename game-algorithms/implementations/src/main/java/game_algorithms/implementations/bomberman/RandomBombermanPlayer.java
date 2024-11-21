package game_algorithms.implementations.bomberman;

import game_algorithms.core.bomberman.BombermanGameState;
import game_algorithms.core.bomberman.BombermanPlayer;
import game_algorithms.core.bomberman.BombermanPlayerAction;

import java.util.Random;

public class RandomBombermanPlayer implements BombermanPlayer {

    private final String name;
    private final Random random = new Random();

    public RandomBombermanPlayer(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BombermanPlayerAction getNextAction(BombermanGameState gameState) {
        return BombermanPlayerAction.values()[random.nextInt(BombermanPlayerAction.values().length)];
    }
}

