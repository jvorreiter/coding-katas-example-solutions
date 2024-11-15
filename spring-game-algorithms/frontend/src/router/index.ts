import Bomberman from '@/bomberman/Bomberman.vue'
import HomeView from '@/HomeView.vue'
import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/bomberman',
      name: 'bomberman',
      component: Bomberman,
    },
  ],
})

export default router
