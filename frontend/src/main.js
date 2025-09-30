import './assets/main.css'

import { createApp } from 'vue'

import App from './App.vue'
import { createPinia } from 'pinia'
import router from './router'
import { useAuth } from '@/stores/auth'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.mount('#app')

const auth = useAuth()
auth.loadFromStorage()