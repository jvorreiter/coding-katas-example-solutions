package de.inmediasp.game_algorithms.bomberman.implementations;

import de.inmediasp.game_algorithms.bomberman.core.BombermanGameState;
import de.inmediasp.game_algorithms.bomberman.core.BombermanPlayer;
import de.inmediasp.game_algorithms.bomberman.core.BombermanPlayerAction;

import java.util.List;
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
