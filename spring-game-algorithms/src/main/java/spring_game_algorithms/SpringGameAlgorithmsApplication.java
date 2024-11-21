package spring_game_algorithms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"spring_game_algorithms", "game_algorithms.implementations"})
public class SpringGameAlgorithmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGameAlgorithmsApplication.class, args);
	}

}
