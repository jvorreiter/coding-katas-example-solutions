package game_algorithms.implementations.bomberman.utils;

import game_algorithms.core.bomberman.BombermanGameState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BombermanPlayerTestUtils {
    private BombermanPlayerTestUtils() {
    }

    /**
     * Creates a BombermanGameState instance from a multiline string.<br/>
     * Each row is divided by a newline character (<code>\n</code>), and the cells in a row are divided by a comma (<code>,</code>).<br/>
     * Specific characters have different meanings as follows:
     * <br/>
     * Any whitespace character except the newline character is seen as filler character and ignored.<br/>
     * <code>.</code> is an empty cell<br/>
     * <code>#</code> is a non-destroyable wall<br/>
     * <code>x</code> is a destroyable wall<br/>
     * <code>0</code> is the current player (isSelf() returns true)<br/>
     * <code>1</code>, <code>2</code>, <code>3</code> are the other players<br/>
     * <code>a</code> is a bomb of the current player (0), <code>A</code> if the bomb is triggered<br/>
     * <code>b</code> is a bomb of player 1, <code>B</code> if the bomb is triggered<br/>
     * <code>c</code> is a bomb of player 2, <code>C</code> if the bomb is triggered<br/>
     * <code>d</code> is a bomb of player 3, <code>D</code> if the bomb is triggered
     *
     * @param board
     * @return the constructed game state
     * @example <code>
     * a ,. ,. ,. ,. ,. ,. ,. ,1<br/>
     * 0 ,# ,. ,# ,. ,# ,. ,# ,.<br/>
     * . ,. ,. ,. ,. ,. ,. ,. ,.<br/>
     * . ,# ,. ,# ,. ,# ,. ,# ,.<br/>
     * . ,. ,2C,. ,. ,. ,. ,. ,.<br/>
     * </code>
     */
    public static BombermanGameState stringToState(String board) {
        var lines = board.split("\n");

        var width = lines[0].split(",").length;
        var height = lines.length;

        var obstacles = new ArrayList<BombermanGameState.Obstacle>();
        var players = new ArrayList<BombermanGameState.Player>();
        players.add(null);
        players.add(null);
        players.add(null);
        players.add(null);

        var bombsByPlayer = List.<List<BombermanGameState.Bomb>>of(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        for (var y = 0; y < height; y++) {
            var line = lines[y];
            var cols = line.split(",");

            for (var x = 0; x < width; x++) {
                var cellChars = cols[x];
                var position = new BombermanGameState.CellPosition(x, y);

                for (var c : cellChars.toCharArray()) {
                    if (Character.isWhitespace(c)) {
                        continue;
                    }

                    switch (c) {
                        case '.' -> {
                        }
                        case '#' -> obstacles.add(new BombermanGameState.Obstacle(position, false));
                        case 'x' -> obstacles.add(new BombermanGameState.Obstacle(position, true));
                        case '0' -> {
                            if (players.get(0) != null) {
                                throw new IllegalArgumentException("Malformed input string, player character '0' occurs multiple times.");
                            }
                            players.set(0, new BombermanGameState.Player(true, 0, "You", true, position, bombsByPlayer.get(0), 1));
                        }
                        case '1' -> {
                            if (players.get(1) != null) {
                                throw new IllegalArgumentException("Malformed input string, player character '1' occurs multiple times.");
                            }
                            players.set(1, new BombermanGameState.Player(false, 1, "Opponent 1", true, position, bombsByPlayer.get(1), 1));
                        }
                        case '2' -> {
                            if (players.get(2) != null) {
                                throw new IllegalArgumentException("Malformed input string, player character '2' occurs multiple times.");
                            }
                            players.set(2, new BombermanGameState.Player(false, 2, "Opponent 2", true, position, bombsByPlayer.get(2), 1));
                        }
                        case '3' -> {
                            if (players.get(3) != null) {
                                throw new IllegalArgumentException("Malformed input string, player character '3' occurs multiple times.");
                            }
                            players.set(3, new BombermanGameState.Player(false, 3, "Opponent 3", true, position, bombsByPlayer.get(3), 1));
                        }
                        case 'a', 'A' -> bombsByPlayer.get(0).add(new BombermanGameState.Bomb(position, c == 'A'));
                        case 'b', 'B' -> bombsByPlayer.get(1).add(new BombermanGameState.Bomb(position, c == 'B'));
                        case 'c', 'C' -> bombsByPlayer.get(2).add(new BombermanGameState.Bomb(position, c == 'C'));
                        case 'd', 'D' -> bombsByPlayer.get(3).add(new BombermanGameState.Bomb(position, c == 'D'));
                        default ->
                                throw new IllegalArgumentException(String.format("Malformed input string, unknown character '%s'", c));
                    }
                }
            }
        }

        if (players.get(0) == null) {
            throw new IllegalArgumentException("Malformed input string, your player (0) did not occur in the string.");
        }

        for (var i = 1; i < players.size(); i++) {
            if (players.get(i) == null) {
                players.set(i, new BombermanGameState.Player(false, i, "Opponent " + i, false, null, bombsByPlayer.get(i), 1));
            }
        }

        if (players.stream().filter(p -> p.isAlive()).count() <= 1) {
            throw new IllegalArgumentException("malformed input, at least 2 players must be alive.");
        }

        return new BombermanGameState(width, height, obstacles, players, List.of());
    }

    /**
     * Reconstructs a parseable string from the given game state.
     *
     * @param state
     * @return
     */
    public static String stateToString(BombermanGameState state) {
        var stateString = new String[state.height()][state.width()];

        for (var y = 0; y < state.height(); y++) {
            for (var x = 0; x < state.width(); x++) {
                stateString[y][x] = "";
            }
        }

        for (var player : state.players()) {
            if (player.isAlive()) {
                stateString[player.cell().y()][player.cell().x()] += Integer.toString(player.index()).charAt(0);
            }

            var bombChar = "abcd" .charAt(player.index());
            for (var bomb : player.bombs()) {
                stateString[bomb.cell().y()][bomb.cell().x()] += bomb.isTriggered() ? Character.toUpperCase(bombChar) : bombChar;
            }
        }

        for (var obstacle : state.obstacles()) {
            stateString[obstacle.cell().y()][obstacle.cell().x()] = obstacle.isBreakable() ? "x" : "#";
        }

        var lines = Arrays.stream(stateString)
                .map(lineCells -> String.join(",", Arrays.stream(lineCells).map(cell -> cell.isEmpty() ? "." : cell).toList()))
                .toList();

        return String.join("\n", lines);
    }

    @Test
    public void testCorrectness() {
        var input = """
                .,.,.,.,.,.,a
                .,#,.,#,.,#,.
                0,.,A  ,x,.,.,.
                .,#,.   ,#,x,#,.
                .,.,B,x,.   ,.,3     
                .,#,., #,.,#,.
                .,.,.,2c,.,.,.
                """;
        var expectedOutput = input.trim().replaceAll("[ ]", "");

        var state = stringToState(input);

        assertEquals(7, state.width());
        assertEquals(7, state.height());

        assertEquals(12, state.obstacles().size());

        {
            assertEquals(9, state.obstacles().stream().filter(o -> !o.isBreakable()).count());
            var wallPositions = List.of(
                    new BombermanGameState.CellPosition(1, 1),
                    new BombermanGameState.CellPosition(3, 1),
                    new BombermanGameState.CellPosition(5, 1),
                    new BombermanGameState.CellPosition(1, 3),
                    new BombermanGameState.CellPosition(3, 3),
                    new BombermanGameState.CellPosition(5, 3),
                    new BombermanGameState.CellPosition(1, 5),
                    new BombermanGameState.CellPosition(3, 5),
                    new BombermanGameState.CellPosition(5, 5)
            );
            for (var wallPosition : wallPositions) {
                assertTrue(state.obstacles().stream().anyMatch(o -> !o.isBreakable() && o.cell().equals(wallPosition)));
            }
        }
        {
            assertEquals(3, state.obstacles().stream().filter(o -> o.isBreakable()).count());
            var breakableWallPositions = List.of(
                    new BombermanGameState.CellPosition(3, 2),
                    new BombermanGameState.CellPosition(4, 3),
                    new BombermanGameState.CellPosition(3, 4)
            );
            for (var breakableWallPosition : breakableWallPositions) {
                assertTrue(state.obstacles().stream().anyMatch(o -> o.isBreakable() && o.cell().equals(breakableWallPosition)));
            }
        }

        assertEquals(4, state.players().size());
        assertEquals(3, state.players().stream().filter(p -> p.isAlive()).count());
        assertEquals(1, state.players().stream().filter(p -> !p.isAlive()).count());
        {
            var player0 = state.players().get(0);
            assertEquals(0, player0.index());
            assertTrue(player0.isSelf());
            assertTrue(player0.isAlive());
            assertEquals(new BombermanGameState.CellPosition(0, 2), player0.cell());

            assertEquals(2, player0.bombs().size());

            assertEquals(new BombermanGameState.CellPosition(6, 0), player0.bombs().get(0).cell());
            assertFalse(player0.bombs().get(0).isTriggered());

            assertEquals(new BombermanGameState.CellPosition(2, 2), player0.bombs().get(1).cell());
            assertTrue(player0.bombs().get(1).isTriggered());
        }
        {
            var player1 = state.players().get(1);
            assertEquals(1, player1.index());
            assertFalse(player1.isSelf());
            assertFalse(player1.isAlive());
            assertNull(player1.cell());

            assertEquals(1, player1.bombs().size());
            assertEquals(new BombermanGameState.CellPosition(2, 4), player1.bombs().get(0).cell());
            assertTrue(player1.bombs().get(0).isTriggered());
        }
        {
            var player2 = state.players().get(2);
            assertEquals(2, player2.index());
            assertFalse(player2.isSelf());
            assertTrue(player2.isAlive());
            assertEquals(new BombermanGameState.CellPosition(3, 6), player2.cell());

            assertEquals(1, player2.bombs().size());
            assertEquals(new BombermanGameState.CellPosition(3, 6), player2.bombs().get(0).cell());
            assertFalse(player2.bombs().get(0).isTriggered());
        }
        {
            var player3 = state.players().get(3);
            assertEquals(3, player3.index());
            assertFalse(player3.isSelf());
            assertTrue(player3.isAlive());
            assertEquals(new BombermanGameState.CellPosition(6, 4), player3.cell());

            assertEquals(0, player3.bombs().size());
        }

        var restringifiedState = stateToString(state);
        assertEquals(expectedOutput.length(), restringifiedState.length());
        for (var i = 0; i < expectedOutput.length(); i++) {
            assertEquals(expectedOutput.charAt(i), stateToString(state).charAt(i));
        }
    }
}
