import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SignupView from '../views/SignupView.vue'
import LoginView from '../views/LoginView.vue' // 추가

const routes = [
  { path: '/', name: 'home', component: HomeView },
  { path: '/signup', name: 'signup', component: SignupView },
  { path: '/login', name: 'login', component: LoginView } // 추가
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router