<script setup lang="ts">
import { ref } from "vue";
import { client } from "./client";

const isReady = ref(false);
client.onConnect = () => (isReady.value = true);

client.activate();
</script>

<template>
  <RouterLink to="/">Home</RouterLink>
  <hr />
  <RouterView v-if="isReady" />
</template>

<style>
:root {
  font-family: Calibri, sans-serif;
}
</style>
