package de.inmediasp.game_algorithms.bomberman;

import de.inmediasp.game_algorithms.bomberman.core.BombermanPlayer;
import de.inmediasp.game_algorithms.bomberman.implementations.RandomBombermanPlayer;

import java.util.List;

public class BombermanGameConfig {

    public static final BombermanGameConfig config = new BombermanGameConfig(
            13,
            9,
            0.2,
            0.1,
            // add your player implementations here
            List.of(
                    new RandomBombermanPlayer("Random 1"),
                    new RandomBombermanPlayer("Random 2")
            )
    );

    //#region ignore below, seriously
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    private final int gridWidth;
    private final int gridHeight;

    private final double breakableObstacleRatio;
    private final double additionalBombChance;

    private final List<BombermanPlayer> players;

    private BombermanGameConfig(
            int gridWidth,
            int gridHeight,
            double breakableObstacleRatio,
            double additionalBombChance,
            List<BombermanPlayer> players
    ) {
        if (gridWidth % 2 == 0) {
            throw new IllegalArgumentException("Grid width must be odd");
        }
        if (gridHeight % 2 == 0) {
            throw new IllegalArgumentException("Grid height must be odd");
        }
        var minSide = 2 * players.size() - 1;
        if (gridWidth < minSide) {
            throw new IllegalArgumentException(String.format("Grid width must be at least %d", minSide));
        }
        if (gridHeight < minSide) {
            throw new IllegalArgumentException(String.format("Grid height must be at least %d", minSide));
        }

        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;

        if (additionalBombChance < 0 || additionalBombChance > 1) {
            throw new IllegalArgumentException("additionalBombChance must be between 0 (inclusive) and 1 (inclusive)");
        }
        this.additionalBombChance = additionalBombChance;

        if (breakableObstacleRatio < 0 || breakableObstacleRatio > 1) {
            throw new IllegalArgumentException("breakableObstacleRatio must be between 0 (inclusive) and 1 (inclusive)");
        }
        this.breakableObstacleRatio = breakableObstacleRatio;

        if(players.size() != 2 && config.players().size() != 4) {
            throw new IllegalArgumentException("Players list must contain 2 or 4 players");
        }
        this.players = players;
    }

    public int gridWidth() {
        return this.gridWidth;
    }

    public int gridHeight() {
        return this.gridHeight;
    }

    public double breakableObstacleRatio() {
        return this.breakableObstacleRatio;
    }

    public double additionalBombChance() {
        return this.additionalBombChance;
    }

    public List<BombermanPlayer> players() {
        return this.players;
    }
    //#endregion
}
