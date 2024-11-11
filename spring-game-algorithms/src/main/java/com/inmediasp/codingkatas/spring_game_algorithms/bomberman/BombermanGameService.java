package com.inmediasp.codingkatas.spring_game_algorithms.bomberman;


import com.inmediasp.codingkatas.spring_game_algorithms.bomberman.messages.BombermanStateMessage;
import de.inmediasp.game_algorithms.bomberman.BombermanGameConfig;
import de.inmediasp.game_algorithms.bomberman.core.BombermanGame;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class BombermanGameService {
    private final Logger logger = Logger.getLogger(BombermanGameService.class.getName());

    private final SimpMessagingTemplate simpTemplate;

    private boolean isRunning = false;

    public BombermanGameService(SimpMessagingTemplate simpTemplate) {
        this.simpTemplate = simpTemplate;
    }

    @Async
    public void startNewGame() {
        if (this.isRunning) {
            return;
        }

        isRunning = true;
        try {
            var game = new BombermanGame(BombermanGameConfig.config);
            logger.info("Starting new game");

            while (game.nextTurn()) {
                logger.info("Turn was played, sending updated state.");
                var state = game.getState();

                var stateMessage = new BombermanStateMessage(state, false);
                simpTemplate.convertAndSend("/game/bomberman", stateMessage);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            simpTemplate.convertAndSend("/game/bomberman", new BombermanStateMessage(null, false));

            logger.info("Game over");
        } finally {
            isRunning = false;
        }
    }
}
