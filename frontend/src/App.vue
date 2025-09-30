<script setup>
import { onMounted, watch } from 'vue'
import { useCart } from '@/stores/cart'
import { useAuth } from '@/stores/auth'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import Notification from '@/components/Notification.vue'

const cart = useCart()
const auth = useAuth()

onMounted(async () => {
  if (auth.user?.id) {
    await cart.fetchCart(auth.user.id)
  }
})

watch(
  () => auth.user,
  async (newUser) => {
    if (newUser?.id) {
      await cart.fetchCart(newUser.id)
    }
  }
)
</script>

<template>
  <div class="font-poppins">
    <Header />
    <div>
      <router-view />
    </div>

    <Notification />
    <Footer />
  </div>
</template>
