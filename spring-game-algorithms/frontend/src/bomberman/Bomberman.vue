<script setup lang="ts">
import { client } from "@/client";
import type { BombermanGameState, BombermanStateMessage } from "./types";
import { computed, ref } from "vue";

client.subscribe("/game/bomberman", (message) => {
  const json: BombermanStateMessage = JSON.parse(message.body);
  console.log("[bomberman]", json);
  isGameOver.value = json.isGameOver;

  if (!isGameOver.value) {
    state.value = json.state;
  }
});

const startGame = () => {
  client.publish({ destination: "/game/bomberman/start" });
};
startGame();

const state = ref<BombermanGameState>();
const isGameOver = ref(false);
const winner = computed(() => {
  if (!isGameOver.value) {
    return undefined;
  }

  return state.value?.players.find((p) => p.isAlive);
});

const cells = computed(() => {
  if (state.value == null) {
    return [];
  }

  const { width, height, obstacles, players, explosions } = state.value;
  return Array.from({ length: width * height }).map((_, i) => {
    const x = i % width;
    const y = Math.trunc(i / width);

    const wall = obstacles.find((o) => o.cell.x == x && o.cell.y == y);
    const playerIndex = players.findIndex(
      (p) => p.cell?.x == x && p.cell.y == y
    );
    const bomb = players
      .flatMap((p) => p.bombs.map((bomb) => ({ bomb, owner: p })))
      .find((b) => b.bomb.cell.x == x && b.bomb.cell.y == y);
    const explosion = explosions
      .flatMap((e) => e.cells)
      .find((cell) => cell.x == x && cell.y == y);

    return {
      x,
      y,
      wall: wall != null,
      breakable: wall?.isBreakable == true,
      player: playerIndex,
      bomb: bomb,
      explosion: explosion != null,
    };
  });
});


async function copyStateStringForTests() {
  if (state.value == null) {
    return;
  }

  const gameState = state.value;

  const chars: string[][] = Array.from({ length: gameState.height }).map((_) =>
    Array.from({ length: gameState.width }).map((_) => ".")
  );

  for (const player of gameState.players) {
    if (player.isAlive) {
      chars[player.cell.y][player.cell.x] = player.index.toString()[0];
    }

    const bombChar = "abcd"[player.index];
    for (const bomb of player.bombs) {
      chars[bomb.cell.y][bomb.cell.x] = bomb.isTriggered
        ? bombChar.toUpperCase()
        : bombChar;
    }
  }

  for (const obstacle of gameState.obstacles) {
    chars[obstacle.cell.y][obstacle.cell.x] = obstacle.isBreakable ? "x" : "#";
  }

  const lines = chars.map((lineChars) => lineChars.join("")).join("\n");

  await navigator.clipboard.writeText(lines);
}
</script>

<template>
  <main>
    <div v-if="state != null" class="bomberman">
      <div
        class="grid"
        :style="{
          '--width': state.width,
          '--height': state.height,
        }"
      >
        <div
          v-for="cell in cells"
          :data-x="cell.x"
          :data-y="cell.y"
          class="cell"
        >
          <div
            v-if="cell.wall"
            class="wall"
            :class="{ 'wall--breakable': cell.breakable }"
          />
          <div v-if="cell.explosion" class="explosion" />
          <div
            v-if="cell.bomb != null"
            class="bomb"
            :class="{ 'bomb--triggered': cell.bomb.bomb.isTriggered }"
          >
            <div class="bomb-background" />
            <div
              class="bomb-owner"
              :class="`owner--${cell.bomb.owner.index}`"
            />
          </div>
          <div
            v-if="cell.player >= 0"
            class="player"
            :class="[
              `player--${cell.player}`,
              {
                'player--dead': cell.player >= 0 && cell.explosion,
              },
            ]"
          />
        </div>
      </div>

      <div v-if="isGameOver" class="game-over">
        <div>Game Over!</div>
        <div v-if="winner == null">The game ends in a draw!</div>
        <div v-else>
          The winner is: {{ winner.name }} (Player {{ winner.index }})!
        </div>
      </div>
    </div>

    <div v-if="state != null">
      <u>Maximum bombs per player: </u>
      <div v-for="player in state.players">
        <div
          v-text="
            `${player.name} (Player ${player.index}): ${player.maxBombCount} bomb(s)`
          "
          :style="{
            'text-decoration': player.isAlive ? '' : 'line-through',
          }"
        />
      </div>
    </div>

    <button
      type="button"
      v-if="state == null || isGameOver"
      @click="() => startGame()"
    >
      Start new game
    </button>
    <button type="button" v-else @click="() => copyStateStringForTests()">
      Copy state as string for tests
    </button>
  </main>
</template>

<style scoped>
main {
  display: inline-grid;
  grid-template-columns: auto auto;
  gap: 16px;
}

.bomberman {
  position: relative;
  display: inline-block;
}
.game-over {
  position: absolute;
  top: 0;
  left: 0;
  right: -2px;
  bottom: -2px;

  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(2px);

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  color: white;
  font-size: 25px;
}

.grid {
  --cell-size: 50px;

  display: grid;
  grid-template-columns: repeat(var(--width), var(--cell-size));
  grid-template-rows: repeat(var(--height), var(--cell-size));
  gap: 4px;
}

.cell {
  border: 1px solid black;
  height: 100%;
  width: 100%;
  position: relative;
}
.cell > * {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  width: 100%;
}

.wall {
  background: #333;
}
.wall--breakable {
  --brick-color: #dd7600;

  background: repeating-linear-gradient(
      45deg,
      var(--brick-color) 25%,
      transparent 25%,
      transparent 75%,
      var(--brick-color) 75%,
      var(--brick-color)
    ),
    repeating-linear-gradient(
      45deg,
      var(--brick-color) 25%,
      transparent 25%,
      transparent 75%,
      var(--brick-color) 75%,
      var(--brick-color)
    );
  background-position: 0 0, 5px 5px;
  background-size: 10px 10px;
}

.player {
  display: flex;
  align-items: center;
  justify-content: center;

  font-size: calc(var(--cell-size) * 0.75);
  color: var(--color);
  text-shadow: 0 0 2px black, 0 0 2px black, 0 0 4px black, 0 0 4px black;
}
.player--0,
.owner--0 {
  --color: rgb(62, 194, 255);
}
.player--0::after {
  content: "0";
}
.player--1,
.owner--1 {
  --color: rgb(62, 255, 165);
}
.player--1::after {
  content: "1";
}
.player--2,
.owner--2 {
  --color: rgb(197, 255, 62);
}
.player--2::after {
  content: "2";
}
.player--3,
.owner--3 {
  --color: rgb(200, 62, 255);
}
.player--3::after {
  content: "3";
}
.owner--0,
.owner--1,
.owner--2,
.owner--3 {
  background: var(--color);
}
.player--dead {
  background-image: url(./assets/death.png);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.bomb {
  position: relative;
}
.bomb-background {
  width: 100%;
  height: 100%;

  background-image: url(./assets/bomb.png);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}
.bomb.bomb--triggered .bomb-background {
  animation: bomb-animation linear 2s infinite;
}

@keyframes bomb-animation {
  0% {
    filter: saturate(20) hue-rotate(160deg);
  }
  50% {
    filter: saturate(20) hue-rotate(120deg);
  }
  100% {
    filter: saturate(20) hue-rotate(160deg);
  }
}

.bomb-owner {
  position: absolute;
  bottom: 2px;
  right: 2px;

  width: 8px;
  height: 8px;
  border-radius: 100%;
}

.explosion {
  background-image: url(./assets/explosion.png);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}
</style>
