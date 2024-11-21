export type BombermanStateMessage = {
    state: BombermanGameState;
    isGameOver: boolean;
}

export type BombermanGameState = {
    width: number;
    height: number;
    obstacles: Obstacle[];
    players: Player[];
    explosions: Explosion[];
}

export type CellPosition = {
    x: number;
    y: number;
}

export type Obstacle = {
    cell: CellPosition;
    isBreakable: boolean;
}

export type Player = {
    index: number;
    name: string;
    bombs: Bomb[];
    maxBombCount: number;
} & (
    | { isAlive: true;  cell: CellPosition } 
    | { isAlive: false; cell?: undefined }
);

export type Bomb = {
    cell: CellPosition;
    isTriggered: boolean;
}

export type Explosion = {
    cells: CellPosition[];
}