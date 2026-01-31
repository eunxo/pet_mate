import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SignupView from '../views/SignupView.vue'
import LoginView from '../views/LoginView.vue'
import MyPageView from '../views/MyPageView.vue'
import PetRegistrationView from '../views/PetRegistrationView.vue'

const routes = [
  { path: '/', name: 'home', component: HomeView },
  { path: '/signup', name: 'signup', component: SignupView },
  { path: '/login', name: 'login', component: LoginView },
  { path: '/mypage', name: 'mypage', component: MyPageView },
  { path: '/petregistration', name: 'pet-registration', component: PetRegistrationView }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router