package de.inmediasp.game_algorithms.bomberman.core;

import de.inmediasp.game_algorithms.Game;
import de.inmediasp.game_algorithms.bomberman.BombermanGameConfig;

import java.util.*;

public class BombermanGame implements Game<BombermanGameState> {
    private final BombermanGameConfig config;
    private final BombermanGameInternalState state;

    private final Random random = new Random();

    /* TODO: These rules should be introduced, as they are more limited and
        differ from the original rules for the sake of allowing more strategies

        RULES:
        - player can only one action per turn: move (any direction), place a bomb, trigger detonation of placed bomb(s), do nothing
            - players do their action in a random order for each turn, but all decide their turn at the same time
        - players cannot move through obstacles, bombs, or other players
            - exception: a player and a bomb can be in the same cell if the player just placed the bomb
        - player can only move a single cell per turn
        - players have a single life, if a player dies it is out of the game
        - players can only place a single bomb at a time at the start, there are hidden power-ups that increase this number
        - bombs don't detonate immediately, they detonate with a random delay of 1 to 5 turns
        - bombs blast through the whole row/column in both directions until an obstacle or player is hit
        - bomb blasts trigger other bombs (with the same random delay as if they were triggered by the owning player)
     */

    public BombermanGame(BombermanGameConfig config) {
        if (config.players().stream().distinct().count() != config.players().size()) {
            throw new IllegalArgumentException("All bomberman players need to be distinct objects");
        }
        if (config.players().size() != 2 && config.players().size() != 4) {
            throw new IllegalArgumentException("Bomberman needs either 2 or 4 player");
        }

        this.config = config;

        var players = this.config.players().stream().map(Player::new).toList();
        this.state = new BombermanGameInternalState(config.gridWidth(), config.gridHeight(), players);
        this.initBoard();
    }

    private void initBoard() {
        var cells = this.state.cells();
        var players = this.state.players();

        for (int x = 0; x < this.config.gridWidth(); x++) {
            for (int y = 0; y < this.config.gridHeight(); y++) {
                var cell = new Cell(x, y);
                cells[x][y] = cell;

                // walls such that there is a wall every other cell
                if (x % 2 == 1 && y % 2 == 1) {
                    cell.setObstacleType(ObstacleType.WALL);
                    continue;
                }

                // keep outer rows and columns clear
                if (x == 0 || x == this.config.gridWidth() - 1 || y == 0 || y == this.config.gridHeight() - 1) {
                    continue;
                }

                // place breakable obstacles randomly
                if (this.random.nextDouble() < this.config.breakableObstacleRatio()) {
                    cell.setObstacleType(ObstacleType.BREAKABLE);
                }
            }
        }

        // place players in the corners
        var cornerCells = List.of(
                cells[0][0], // top left
                cells[this.config.gridWidth() - 1][this.config.gridHeight() - 1], // bottom right
                cells[0][this.config.gridHeight() - 1], // bottom left
                cells[this.config.gridWidth() - 1][0] // top right
        );
        for (int i = 0; i < players.size(); i++) {
            var cornerCell = cornerCells.get(i);
            var player = players.get(i);

            cornerCell.setPlayer(player);
            player.setCurrentCell(cornerCell);
        }
    }

    @Override
    public boolean nextTurn() {
        if (state.isGameOver()) {
            return false;
        }

        for(var player: state.players()) {
            if(!player.isAlive() && player.currentCell() != null) {
                player.currentCell().setPlayer(null);
                player.setCurrentCell(null);
            }
        }

        updateDetonatingBombs();
        handleExplodedCells();
        doPlayerTurns();

        return true;
    }

    private void doPlayerTurns() {
        var randomPlayerOrder = new ArrayList<>(state.players());
        randomPlayerOrder.sort((a, b) -> random.nextInt(-1, 2));

        var state = getState();

        for (var player : randomPlayerOrder) {
            if(!player.isAlive()) {
                continue;
            }

            var action = player.implementation().getNextAction(state);
            doPlayerAction(player, action);
        }
    }

    private void handleExplodedCells() {
        var explodedCells = state.explosions().stream().flatMap(e -> e.cells().stream()).distinct().toList();
        for(var cell : explodedCells) {
            cell.player().ifPresent(player -> player.setAlive(false));

            cell.bomb().ifPresent(bomb -> triggerBomb(bomb));

            if(cell.obstacleType().equals(ObstacleType.BREAKABLE)) {
                cell.setObstacleType(ObstacleType.NONE);
            }
        }
    }

    private void updateDetonatingBombs() {
        state.explosions().clear();
        for (int i = 0; i < state.detonatingBombs().size(); i++) {
            var bomb = state.detonatingBombs().get(i);
            if (bomb.detonationTimer().get() > 0) {
                bomb.setDetonationTimer(bomb.detonationTimer().get() - 1);
                continue;
            }

            explode(bomb);

            bomb.owner().bombs().remove(bomb);
            state.detonatingBombs().remove(i--);
        }
    }

    private void explode(Bomb bomb) {
        var explosion = new Explosion();

        explode(bomb, 1, 0, explosion);
        explode(bomb, -1, 0, explosion);
        explode(bomb, 0, 1, explosion);
        explode(bomb, 0, -1, explosion);

        state.explosions().add(explosion);
    }

    private void explode(Bomb bomb, int dx, int dy, Explosion explosion) {
        var cells = state.cells();

        int x = bomb.cell().x();
        int y = bomb.cell().y();

        while(x >= 0 && x < cells.length && y >= 0 && y < cells[0].length) {
            var cell = cells[x][y];
            if(cell.obstacleType().equals(ObstacleType.WALL)) {
                break;
            }

            x += dx;
            y += dy;

            explosion.addCell(cell);
        }
    }

    private void doPlayerAction(Player player, BombermanPlayerAction action) {
        switch (action) {
            case NOTHING -> {}

            case MOVE_LEFT -> movePlayer(player, -1, 0);
            case MOVE_RIGHT -> movePlayer(player, 1, 0);
            case MOVE_UP -> movePlayer(player, 0, -1);
            case MOVE_DOWN -> movePlayer(player, 0, 1);

            case PLACE_BOMB -> placeBomb(player);
            case TRIGGER_DETONATION -> triggerBombs(player);
        }
    }

    private void triggerBombs(Player player) {
        for (var bomb : player.bombs()) {
            triggerBomb(bomb);
        }
    }

    private void triggerBomb(Bomb bomb) {
        var detonationTimer = random.nextInt(1, 6);
        bomb.setDetonationTimer(detonationTimer);

        state.detonatingBombs().add(bomb);
    }

    private void placeBomb(Player player) {
        // player cannot place any more bombs
        if (player.bombs().size() >= player.maxBombCount()) {
            return;
        }

        var bomb = new Bomb(player);
        player.currentCell().setBomb(bomb);
        player.bombs().add(bomb);
    }

    private void movePlayer(Player player, int dx, int dy) {
        var targetX = player.currentCell().x() + dx;
        var targetY = player.currentCell().y() + dy;

        // player would move out of bounds, don't move the player
        if (targetX < 0 || targetX >= this.config.gridWidth() || targetY < 0 || targetY >= this.config.gridHeight()) {
            return;
        }

        var targetCell = state.cells()[targetX][targetY];
        var canMoveOntoCell = targetCell.bomb().isEmpty()
                && targetCell.player().isEmpty()
                && targetCell.obstacleType().equals(ObstacleType.NONE);

        if (!canMoveOntoCell) {
            return;
        }

        player.currentCell().setPlayer(null);

        targetCell.setPlayer(player);
        player.setCurrentCell(targetCell);
    }

    @Override
    public BombermanGameState getState() {
        var obstacles = new ArrayList<BombermanGameState.Obstacle>();
        var players = new ArrayList<BombermanGameState.Player>();
        var explosions = new ArrayList<BombermanGameState.Explosion>();

        for(var row : state.cells()) {
            for(var cell : row) {
                var position = new BombermanGameState.CellPosition(cell.x(), cell.y());

                if(!cell.obstacleType().equals(ObstacleType.NONE)) {
                    var isBreakable = cell.obstacleType().equals(ObstacleType.BREAKABLE);
                    obstacles.add(new BombermanGameState.Obstacle(position, isBreakable));
                }
            }
        }

        for (Player player : state.players()) {
            String name = player.implementation().getName();
            var playerPosition = new BombermanGameState.CellPosition(player.currentCell().x(), player.currentCell().y());

            var bombs = player.bombs().stream().map(bomb -> {
                var bombPosition = new BombermanGameState.CellPosition(bomb.cell().x(), bomb.cell().y());
                return new BombermanGameState.Bomb(bombPosition);
            }).toList();

            var playerState = new BombermanGameState.Player(name, player.isAlive(), playerPosition, bombs);
            players.add(playerState);
        }

        for(var explosion : state.explosions()) {
            var cells = explosion.cells().stream().map(cell -> new BombermanGameState.CellPosition(cell.x(), cell.y())).toList();
            explosions.add(new BombermanGameState.Explosion(cells));
        }

        return new BombermanGameState(config.gridWidth(), config.gridHeight(), obstacles, players, explosions);
    }

}

class BombermanGameInternalState {
    private final Cell[][] cells;
    private final List<Player> players;
    private final List<Bomb> detonatingBombs = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();

    public BombermanGameInternalState(int width, int height, List<Player> players) {
        this.cells = new Cell[width][height];
        this.players = players;
    }

    public boolean isGameOver() {
        return this.players.stream().filter(Player::isAlive).count() <= 1;
    }

    public Cell[][] cells() {
        return cells;
    }

    public List<Player> players() {
        return players;
    }

    public List<Bomb> detonatingBombs() {
        return detonatingBombs;
    }

    public List<Explosion> explosions() {
        return explosions;
    }
}

class Explosion {
    private final Set<Cell> cells = new HashSet<>();


    public Set<Cell> cells() {
        return cells;
    }

    public boolean addCell(Cell cell) {
        return cells.add(cell);
    }
}

class Cell {
    private final int x;
    private final int y;

    private ObstacleType obstacleType = ObstacleType.NONE;

    private Player player = null;
    private Bomb bomb = null;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public ObstacleType obstacleType() {
        return obstacleType;
    }

    public void setObstacleType(ObstacleType obstacleType) {
        this.obstacleType = obstacleType;
    }

    public Optional<Player> player() {
        return Optional.ofNullable(player);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Optional<Bomb> bomb() {
        return Optional.ofNullable(bomb);
    }

    public void setBomb(Bomb bomb) {
        this.bomb = bomb;
    }
}

class Player {
    private final BombermanPlayer implementation;
    private final List<Bomb> bombs = new ArrayList<>();
    private int maxBombCount = 1;
    private Cell currentCell;
    private boolean isAlive = true;

    public Player(BombermanPlayer implementation) {
        this.implementation = implementation;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public BombermanPlayer implementation() {
        return implementation;
    }

    public List<Bomb> bombs() {
        return bombs;
    }

    public int maxBombCount() {
        return maxBombCount;
    }

    public Player setMaxBombCount(int maxBombCount) {
        this.maxBombCount = maxBombCount;
        return this;
    }

    public Cell currentCell() {
        return currentCell;
    }

    public void setCurrentCell(Cell currentCell) {
        this.currentCell = currentCell;
    }
}

class Bomb {
    private final Player owner;
    private final Cell cell;

    private Integer detonationTimer = null;

    public Bomb(Player owner) {
        this.owner = owner;
        this.cell = owner.currentCell();
    }

    public Player owner() {
        return owner;
    }

    public Cell cell() {
        return cell;
    }

    public Optional<Integer> detonationTimer() {
        return Optional.ofNullable(detonationTimer);
    }

    public void setDetonationTimer(int detonationTimer) {
        this.detonationTimer = detonationTimer;
    }
}

enum ObstacleType {
    WALL,
    BREAKABLE,
    NONE;
}