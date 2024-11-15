package game_algorithms.implementations.bomberman;

import game_algorithms.core.bomberman.BombermanGameConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ConfigBean {
    @Bean
    public BombermanGameConfig getConfig() {
        return new BombermanGameConfig(
                13,
                9,
                0.5,
                0.1,
                // add your player implementations here
                List.of(
                        new RandomBombermanPlayer("Random 1"),
                        new RandomBombermanPlayer("Random 2"),
                        new RandomBombermanPlayer("Random 3"),
                        new RandomBombermanPlayer("Random 4")
                )
        );
    }
}
