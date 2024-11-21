package game_algorithms.core.bomberman;

import game_algorithms.core.Game;

import java.util.*;

public class BombermanGame implements Game<BombermanGameState> {
    private final BombermanGameConfig config;
    private final BombermanGameInternalState state;

    private final Random random = new Random();

    private final int BOMB_RANGE = 3;
    private final int SPAWN_CLEAR_RANGE = 3;

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

        // place players in the corners
        var cornerCellPositions = List.of(
                new Position(0, 0),
                new Position(this.config.gridWidth() - 1, this.config.gridHeight() - 1),
                new Position(0, this.config.gridHeight() - 1),
                new Position(this.config.gridWidth() - 1, 0)
        );

        for (int x = 0; x < this.config.gridWidth(); x++) {
            for (int y = 0; y < this.config.gridHeight(); y++) {
                var cell = new Cell(x, y);
                cells[x][y] = cell;

                // walls such that there is a wall every other cell
                if (x % 2 == 1 && y % 2 == 1) {
                    cell.setObstacleType(ObstacleType.WALL);
                    continue;
                }

                // keep cells clear with a certain distance to a spawn point
                var x1 = x;
                var y1 = y;
                if (cornerCellPositions.stream().anyMatch(p -> getManhattanDistance(p.x(), p.y(), x1, y1) <= SPAWN_CLEAR_RANGE)) {
                    continue;
                }

                // place breakable obstacles randomly
                if (this.random.nextDouble() < this.config.breakableObstacleRatio()) {
                    cell.setObstacleType(ObstacleType.BREAKABLE);
                }
            }
        }

        // place players in the corners
        var cornerCells = cornerCellPositions.stream().map(pos -> cells[pos.x()][pos.y()]).toList();

        var randomPlayerOrder = new ArrayList<>(state.players());
        randomPlayerOrder.sort((a, b) -> random.nextInt(-1, 2));

        for (int i = 0; i < randomPlayerOrder.size(); i++) {
            var cornerCell = cornerCells.get(i);
            var player = randomPlayerOrder.get(i);

            cornerCell.setPlayer(player);
            player.setCurrentCell(cornerCell);
        }
    }

    private int getManhattanDistance(int x0, int y0, int x1, int y1) {
        return Math.abs(x0 - x1) + Math.abs(y0 - y1);
    }

    @Override
    public boolean nextTurn() {
        if (state.isGameOver()) {
            return false;
        }

        for (var player : state.players()) {
            if (!player.isAlive() && player.currentCell() != null) {
                player.currentCell().setPlayer(null);
                player.setCurrentCell(null);
            }
        }

        doPlayerTurns();
        updateDetonatingBombs();
        handleExplodedCells();

        return true;
    }

    private void doPlayerTurns() {
        var randomPlayerOrder = new ArrayList<>(state.players());
        randomPlayerOrder.sort((a, b) -> random.nextInt(-1, 2));

        var state = getState();

        for (var player : randomPlayerOrder) {
            if (!player.isAlive()) {
                continue;
            }

            try {
                var action = player.implementation().getNextAction(state);
                doPlayerAction(player, action);
            } catch (Throwable throwable) {
                // players are killed if they throw any exception
                player.setAlive(false);
            }
        }
    }

    private void handleExplodedCells() {
        var explodedCells = state.explosions().stream().flatMap(e -> e.cells().stream()).distinct().toList();
        for (var cell : explodedCells) {
            cell.player().ifPresent(player -> player.setAlive(false));

            cell.bomb().ifPresent(bomb -> {
                if (bomb.detonationTimer().isEmpty()) {
                    explode(bomb);
                }
            });

            if (cell.obstacleType().equals(ObstacleType.BREAKABLE)) {
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
            i--;
        }
    }

    private void explode(Bomb bomb) {
        var explosion = new Explosion();

        explode(bomb, 1, 0, explosion);
        explode(bomb, -1, 0, explosion);
        explode(bomb, 0, 1, explosion);
        explode(bomb, 0, -1, explosion);

        state.explosions().add(explosion);

        bomb.owner().bombs().remove(bomb);
        state.detonatingBombs().remove(bomb);
    }

    private void explode(Bomb bomb, int dx, int dy, Explosion explosion) {
        var cells = state.cells();

        int x = bomb.cell().x();
        int y = bomb.cell().y();

        while (x >= 0 && x < cells.length && y >= 0 && y < cells[0].length) {
            var cell = cells[x][y];
            if (cell.obstacleType().equals(ObstacleType.WALL)) {
                break;
            }

            x += dx;
            y += dy;

            explosion.addCell(cell);

            // maximum range reached
            if (Math.abs(bomb.cell().x() - x) > BOMB_RANGE
                    || Math.abs(bomb.cell().y() - y) > BOMB_RANGE) {
                break;
            }

            // explosions dont pass through breakable walls, or other bombs
            if (cell.obstacleType().equals(ObstacleType.BREAKABLE)) {
                break;
            }
            if (cell.bomb().isPresent() && !cell.bomb().get().equals(bomb)) {
                break;
            }
        }
    }

    private void doPlayerAction(Player player, BombermanPlayerAction action) {
        switch (action) {
            case NOTHING -> {
            }

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

        for (var row : state.cells()) {
            for (var cell : row) {
                var position = new BombermanGameState.CellPosition(cell.x(), cell.y());

                if (!cell.obstacleType().equals(ObstacleType.NONE)) {
                    var isBreakable = cell.obstacleType().equals(ObstacleType.BREAKABLE);
                    obstacles.add(new BombermanGameState.Obstacle(position, isBreakable));
                }
            }
        }

        for (int i = 0; i < state.players().size(); i++) {
            Player player = state.players().get(i);
            String name = player.implementation().getName();

            var playerPosition = player.isAlive()
                    ? new BombermanGameState.CellPosition(player.currentCell().x(), player.currentCell().y())
                    : null;

            var bombs = player.bombs().stream().map(bomb -> {
                var bombPosition = new BombermanGameState.CellPosition(bomb.cell().x(), bomb.cell().y());
                return new BombermanGameState.Bomb(bombPosition, bomb.detonationTimer().isPresent());
            }).toList();

            var playerState = new BombermanGameState.Player(i, name, player.isAlive(), playerPosition, bombs, player.maxBombCount());
            players.add(playerState);
        }

        for (var explosion : state.explosions()) {
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

record Position(int x, int y) {
}