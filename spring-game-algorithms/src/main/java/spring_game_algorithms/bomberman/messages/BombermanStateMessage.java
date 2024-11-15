package spring_game_algorithms.bomberman.messages;

import game_algorithms.core.bomberman.BombermanGameState;

public record BombermanStateMessage(BombermanGameState state, boolean isGameOver) {
}