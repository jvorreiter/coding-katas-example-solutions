package com.inmediasp.codingkatas.spring_game_algorithms.bomberman;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BombermanController {

    @MessageMapping("/bomberman/start")
    public void startNewGame() {
        //TODO: start new game in background and send messages regularly
    }
}
