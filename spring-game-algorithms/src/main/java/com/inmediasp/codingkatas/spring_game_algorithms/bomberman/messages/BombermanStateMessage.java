package com.inmediasp.codingkatas.spring_game_algorithms.bomberman.messages;

import de.inmediasp.game_algorithms.bomberman.core.BombermanGameState;

public record BombermanStateMessage(BombermanGameState state, boolean isGameOver) {
}