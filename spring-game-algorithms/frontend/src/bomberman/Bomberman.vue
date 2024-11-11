<script setup lang="ts">
import { client } from "@/client";
import type { BombermanGameState, BombermanStateMessage } from "./types";
import { computed, ref } from "vue";

client.subscribe("/game/bomberman", (message) => {
  const json: BombermanStateMessage = JSON.parse(message.body);
  state.value = json.state;
  console.log(`Received: `, json);
});

client.publish({ destination: "/game/bomberman/start" });

const state = ref<BombermanGameState>();
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
      .flatMap((p) => p.bombs)
      .find((b) => b.cell.x == x && b.cell.y == y);
    const explosion = explosions
      .flatMap((e) => e.cells)
      .find((cell) => cell.x == x && cell.y == y);

    return {
      x,
      y,
      wall: wall != null,
      breakable: wall?.isBreakable == true,
      player: playerIndex,
      bomb: bomb != null,
      explosion: explosion != null,
    };
  });
});
</script>

<template>
  <div v-if="state != null">
    <div
      class="grid"
      :style="{
        '--width': state.width,
        '--height': state.height,
      }"
    >
      <div v-for="cell in cells" :data-x="cell.x" :data-y="cell.y" class="cell">
        <div
          v-if="cell.wall"
          class="wall"
          :class="{ 'wall--breakable': cell.breakable }"
        />
        <div v-if="cell.explosion" class="explosion" />
        <div v-if="cell.bomb" class="bomb" />
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
  </div>
</template>

<style scoped>
.grid {
  --cell-size: 50px;

  display: grid;
  grid-template-columns: repeat(var(--width), var(--cell-size));
  grid-template-rows: repeat(var(--height), var(--cell-size));
  gap: 6px;
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
  background: repeating-linear-gradient(
      45deg,
      #333 25%,
      transparent 25%,
      transparent 75%,
      #333 75%,
      #333
    ),
    repeating-linear-gradient(
      45deg,
      #333 25%,
      transparent 25%,
      transparent 75%,
      #333 75%,
      #333
    );
  background-position: 0 0, 5px 5px;
  background-size: 10px 10px;
}

.player {
  background: rgb(62, 194, 255);
}
.player--0::after {
  content: "0";
}
.player--1::after {
  content: "1";
}
.player--2::after {
  content: "2";
}
.player--3::after {
  content: "3";
}
.player--dead {
  background: linear-gradient(
      45deg,
      transparent 0,
      transparent 48%,
      black 48%,
      black 52%,
      transparent 52%
    ),
    linear-gradient(
      -45deg,
      transparent 0,
      transparent 48%,
      black 48%,
      black 52%,
      transparent 52%
    );
}

.bomb {
  background: red;
}

.explosion {
  background: radial-gradient(circle, #ffa000 0%, #ff0000 100%);
}
</style>
