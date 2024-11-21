package game_algorithms.core.bomberman;

import java.util.List;

public record BombermanGameState(
        int width,
        int height,
        List<Obstacle> obstacles,
        List<Player> players,
        List<Explosion> explosions
) {

    public record Obstacle(CellPosition cell, boolean isBreakable) {}
    public record CellPosition(int x, int y) {}
    public record Player(int index, String name, boolean isAlive, CellPosition cell, List<Bomb> bombs, int maxBombCount) {}
    public record Bomb(CellPosition cell, boolean isTriggered) {}
    public record Explosion(List<CellPosition> cells) {}
}