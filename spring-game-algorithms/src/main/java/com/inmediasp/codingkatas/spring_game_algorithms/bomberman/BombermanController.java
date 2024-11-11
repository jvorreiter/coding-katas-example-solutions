package com.inmediasp.codingkatas.spring_game_algorithms.bomberman;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BombermanController {

    private final BombermanGameService gameService;

    public BombermanController(BombermanGameService gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/bomberman/start")
    public void startNewGame() {
        gameService.startNewGame();
    }
}
