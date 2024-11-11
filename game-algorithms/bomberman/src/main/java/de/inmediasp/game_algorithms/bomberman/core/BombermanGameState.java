package de.inmediasp.game_algorithms.bomberman.core;

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
    public record Player(String name, boolean isAlive, CellPosition cell, List<Bomb> bombs) {}
    public record Bomb(CellPosition cell) {}
    public record Explosion(List<CellPosition> cells) {}
}