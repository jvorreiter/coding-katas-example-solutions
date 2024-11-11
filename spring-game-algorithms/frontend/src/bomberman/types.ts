export type BombermanStateMessage = {
    state: BombermanGameState;
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
    name: string;
    bombs: Bomb[];
} & (
    | { isAlive: true;  cell: CellPosition } 
    | { isAlive: false; cell?: undefined }
);

export type Bomb = {
    cell: CellPosition;
}

export type Explosion = {
    cells: CellPosition[];
}