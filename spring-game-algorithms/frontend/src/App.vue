<script setup lang="ts">
import { ref } from "vue";
import Bomberman from "./bomberman/Bomberman.vue";
import { client } from "./client";

const isReady = ref(false);
client.onConnect = () => isReady.value = true;

client.activate();
</script>

<template>
  <Bomberman v-if="isReady" />
  <!-- <RouterView /> -->
</template>

<style scoped></style>
