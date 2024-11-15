package spring_game_algorithms.bomberman;


import game_algorithms.core.bomberman.BombermanGame;
import game_algorithms.core.bomberman.BombermanGameConfig;
import org.springframework.beans.factory.annotation.Autowired;
import spring_game_algorithms.bomberman.messages.BombermanStateMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spring_game_algorithms.utils.Sleeper;

import java.util.logging.Logger;

@Service
public class BombermanGameService {
    private final Logger logger = Logger.getLogger(BombermanGameService.class.getName());

    private final SimpMessagingTemplate simpTemplate;
    private final BombermanGameConfig config;

    private boolean isRunning = false;

    @Autowired
    public BombermanGameService(SimpMessagingTemplate simpTemplate, BombermanGameConfig config) {
        this.simpTemplate = simpTemplate;
        this.config = config;
    }

    @Async
    public void startNewGame() {
        if (this.isRunning) {
            return;
        }

        isRunning = true;
        try {
            var game = new BombermanGame(config);
            logger.info("Starting new game");

            do {
                logger.info("Playing turn, sending state.");
                var state = game.getState();

                var stateMessage = new BombermanStateMessage(state, false);
                simpTemplate.convertAndSend("/game/bomberman", stateMessage);

                Sleeper.sleep(1000);
            } while (game.nextTurn());

            simpTemplate.convertAndSend("/game/bomberman", new BombermanStateMessage(null, true));

            logger.info("Game over");
        } finally {
            isRunning = false;
        }
    }
}
