<script setup>
import { ref } from 'vue'
import { useAuth } from '@/stores/auth'
import { useCart } from '@/stores/cart'
import { useRouter } from 'vue-router'
import { userLogout } from '@/libs/fetchUtils'
import RightArrow from './icons/RightArrow.vue'

const router = useRouter()

const auth = useAuth()
const cart = useCart()

const isMenuOpen = ref(false)

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}

const showDropdown = ref(false)

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
}

const BASE_API_URL_auth = import.meta.env.VITE_APP_URL + '/itb-mshop/v2/auth'

const handleLogout = async () => {
  try {
    const res = await userLogout(`${BASE_API_URL_auth}/logout`)
    if (res.code === 204) {
      auth.clearAuth()
      cart.clearCart()
      showDropdown.value = false
      router.push('/')
    } else {
      console.error(res.body.error)
    }
  } catch (error) {
    console.error(error)
  }
}

// const handleLogout = () => {
//   auth.clearAuth()
//   cart.clearCart()
//   showDropdown.value = false
//   router.push('/')
// }
</script>

<template>
  <!-- top banner -->
  <div
    class="bg-[#3A383B] text-white text-center py-2.5 px-4 text-sm font-light hidden lg:block"
  >
    Apple products special prices, with discounts of up to 30%
  </div>

  <!-- sticky header -->
  <div
    class="sticky top-0 z-50 w-full bg-white py-4 px-6 md:px-10 lg:px-16 border-b-2 border-[#F4F3F3]"
  >
    <!-- desktop -->
    <div class="grid grid-cols-4 items-center">
      <div>
        <router-link
          :to="{ name: 'Home' }"
          class="hidden lg:block font-semibold text-xl"
        >
          ITB MShop
        </router-link>
      </div>
      <div class="hidden lg:flex items-center space-x-20">
        <router-link
          active-class="bg-[#F4F3F3] rounded-2xl"
          class="py-1 px-4 text-base cursor-pointer"
          :to="{ name: 'Home' }"
        >
          Home
        </router-link>
        <router-link
          active-class="bg-[#F4F3F3] rounded-2xl"
          class="py-1 px-4 text-base cursor-pointer"
          :to="{ name: 'SaleItemGallery' }"
        >
          Items
        </router-link>
      </div>
      <div v-if="!auth.isLoggedIn"></div>
      <div v-else class="hidden xl:block"></div>

      <div>
        <!-- Before login -->
        <div
          v-if="!auth.isLoggedIn"
          class="hidden lg:flex items-center justify-end space-x-2 font-medium"
        >
          <router-link class="px-4 py-1 rounded-full" :to="{ name: 'SignIn' }"
            >Sign In</router-link
          >
          <svg
            width="2"
            height="29"
            viewBox="0 0 2 29"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <line
              x1="1"
              y1="29"
              x2="0.999999"
              y2="4.37114e-08"
              stroke="#E0E0E0"
              stroke-width="2"
            />
          </svg>
          <router-link
            :to="{ name: 'RegisterForm' }"
            class="text-white bg-[#0171E3] hover:bg-[#157DE6] active:bg-[#3A98F6] px-4 py-1 rounded-full ml-2"
            >Register</router-link
          >
        </div>

        <!-- After login -->
        <div
          v-else
          class="hidden lg:flex items-center justify-between space-x-6 font-medium w-80 xl:w-90 ml-36 xl:ml-0"
        >
          <svg
            width="32"
            height="32"
            viewBox="0 0 32 32"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M14.1168 21.6417L14.0002 21.7583L13.8718 21.6417C8.33016 16.6133 4.66683 13.2883 4.66683 9.91667C4.66683 7.58333 6.41683 5.83333 8.75016 5.83333C10.5468 5.83333 12.2968 7 12.9152 8.58667H15.0852C15.7035 7 17.4535 5.83333 19.2502 5.83333C21.5835 5.83333 23.3335 7.58333 23.3335 9.91667C23.3335 13.2883 19.6702 16.6133 14.1168 21.6417ZM19.2502 3.5C17.2202 3.5 15.2718 4.445 14.0002 5.92667C12.7285 4.445 10.7802 3.5 8.75016 3.5C5.15683 3.5 2.3335 6.31167 2.3335 9.91667C2.3335 14.315 6.30016 17.92 12.3085 23.3683L14.0002 24.9083L15.6918 23.3683C21.7002 17.92 25.6668 14.315 25.6668 9.91667C25.6668 6.31167 22.8435 3.5 19.2502 3.5Z"
              fill="black"
            />
          </svg>

          <router-link
            :to="{ name: 'Cart' }"
            active-class="bg-[#F4F3F3] rounded-full"
            class="pt-2 pb-1 pl-2 pr-1 cursor-pointer"
          >
            <div class="relative w-8 h-8">
              <svg
                width="32"
                height="32"
                viewBox="0 0 32 32"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M19.8332 21C20.452 21 21.0455 21.2459 21.4831 21.6835C21.9207 22.121 22.1665 22.7145 22.1665 23.3334C22.1665 23.9522 21.9207 24.5457 21.4831 24.9833C21.0455 25.4209 20.452 25.6667 19.8332 25.6667C19.2143 25.6667 18.6208 25.4209 18.1833 24.9833C17.7457 24.5457 17.4998 23.9522 17.4998 23.3334C17.4998 22.0384 18.5382 21 19.8332 21ZM1.1665 2.33337H4.9815L6.07817 4.66671H23.3332C23.6426 4.66671 23.9393 4.78962 24.1581 5.00842C24.3769 5.22721 24.4998 5.52395 24.4998 5.83337C24.4998 6.03171 24.4415 6.23004 24.3598 6.41671L20.1832 13.965C19.7865 14.6767 19.0165 15.1667 18.1415 15.1667H9.44984L8.39984 17.0684L8.36484 17.2084C8.36484 17.2857 8.39557 17.3599 8.45026 17.4146C8.50496 17.4693 8.57915 17.5 8.6565 17.5H22.1665V19.8334H8.1665C7.54767 19.8334 6.95417 19.5875 6.51659 19.15C6.079 18.7124 5.83317 18.1189 5.83317 17.5C5.83317 17.0917 5.93817 16.7067 6.11317 16.38L7.69984 13.5217L3.49984 4.66671H1.1665V2.33337ZM8.1665 21C8.78534 21 9.37883 21.2459 9.81642 21.6835C10.254 22.121 10.4998 22.7145 10.4998 23.3334C10.4998 23.9522 10.254 24.5457 9.81642 24.9833C9.37883 25.4209 8.78534 25.6667 8.1665 25.6667C7.54767 25.6667 6.95417 25.4209 6.51659 24.9833C6.079 24.5457 5.83317 23.9522 5.83317 23.3334C5.83317 22.0384 6.8715 21 8.1665 21ZM18.6665 12.8334L21.9098 7.00004H7.16317L9.9165 12.8334H18.6665Z"
                  fill="black"
                />
              </svg>
              <span
                v-if="cart.totalQty > 0"
                class="itbms-cart-quantity absolute top-0 right-0 -mt-1 -mr-1 w-5 h-5 flex items-center justify-center text-xs text-white bg-[#0171E3] rounded-full"
              >
                {{ cart.totalQty }}
              </span>
            </div>
          </router-link>

          <div class="relative">
            <div
              class="border border-[#F4F3F3] py-2 px-4 min-w-[180px] max-w-[250px] rounded-xl flex justify-between items-center space-x-8 cursor-pointer"
              @click="toggleDropdown"
            >
              <p class="truncate max-w-[calc(100%-40px)]">
                {{ auth.userProfile?.nickName }}
              </p>
              <svg
                width="32"
                height="32"
                viewBox="0 0 32 32"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M7.79984 22.8C8.93317 21.9333 10.1998 21.25 11.5998 20.75C12.9998 20.25 14.4665 20 15.9998 20C17.5332 20 18.9998 20.25 20.3998 20.75C21.7998 21.25 23.0665 21.9333 24.1998 22.8C24.9776 21.8888 25.5832 20.8555 26.0165 19.7C26.4498 18.5444 26.6665 17.3111 26.6665 16C26.6665 13.0444 25.6276 10.5277 23.5498 8.44996C21.4721 6.37218 18.9554 5.33329 15.9998 5.33329C13.0443 5.33329 10.5276 6.37218 8.44984 8.44996C6.37206 10.5277 5.33317 13.0444 5.33317 16C5.33317 17.3111 5.54984 18.5444 5.98317 19.7C6.4165 20.8555 7.02206 21.8888 7.79984 22.8ZM15.9998 17.3333C14.6887 17.3333 13.5832 16.8833 12.6832 15.9833C11.7832 15.0833 11.3332 13.9777 11.3332 12.6666C11.3332 11.3555 11.7832 10.25 12.6832 9.34996C13.5832 8.44996 14.6887 7.99996 15.9998 7.99996C17.3109 7.99996 18.4165 8.44996 19.3165 9.34996C20.2165 10.25 20.6665 11.3555 20.6665 12.6666C20.6665 13.9777 20.2165 15.0833 19.3165 15.9833C18.4165 16.8833 17.3109 17.3333 15.9998 17.3333ZM15.9998 29.3333C14.1554 29.3333 12.4221 28.9833 10.7998 28.2833C9.17762 27.5833 7.7665 26.6333 6.5665 25.4333C5.3665 24.2333 4.4165 22.8222 3.7165 21.2C3.0165 19.5777 2.6665 17.8444 2.6665 16C2.6665 14.1555 3.0165 12.4222 3.7165 10.8C4.4165 9.17774 5.3665 7.76663 6.5665 6.56663C7.7665 5.36663 9.17762 4.41663 10.7998 3.71663C12.4221 3.01663 14.1554 2.66663 15.9998 2.66663C17.8443 2.66663 19.5776 3.01663 21.1998 3.71663C22.8221 4.41663 24.2332 5.36663 25.4332 6.56663C26.6332 7.76663 27.5832 9.17774 28.2832 10.8C28.9832 12.4222 29.3332 14.1555 29.3332 16C29.3332 17.8444 28.9832 19.5777 28.2832 21.2C27.5832 22.8222 26.6332 24.2333 25.4332 25.4333C24.2332 26.6333 22.8221 27.5833 21.1998 28.2833C19.5776 28.9833 17.8443 29.3333 15.9998 29.3333Z"
                  fill="#1D1B20"
                />
              </svg>
            </div>
            <div
              v-if="showDropdown"
              class="absolute right-0 mt-2 w-40 bg-white border border-gray-200 rounded-lg shadow-lg py-2 z-50"
            >
              <router-link
                :to="{ name: 'Profile' }"
                class="itbms-profile block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 cursor-pointer"
              >
                View Profile
              </router-link>
              <button
                @click="handleLogout"
                class="itbms-logout block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 cursor-pointer"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- mobile -->
    <div class="flex justify-between items-center pt-1">
      <div>
        <router-link
          :to="{ name: 'Home' }"
          class="lg:hidden font-semibold text-xl"
        >
          ITB MShop
        </router-link>
      </div>

      <button v-show="!isMenuOpen" class="lg:hidden" @click="toggleMenu()">
        <svg
          class="w-[32px] h-[32px] md:w-[36px] md:h-[36px]"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M5 17H19M5 12H19M5 7H19"
            stroke="black"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>
      <button v-show="isMenuOpen" class="lg:hidden" @click="toggleMenu()">
        <svg
          class="w-[30px] h-[30px] md:w-[32px] md:h-[32px]"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M12 13.3998L7.10005 18.2998C6.91672 18.4831 6.68338 18.5748 6.40005 18.5748C6.11672 18.5748 5.88338 18.4831 5.70005 18.2998C5.51672 18.1165 5.42505 17.8831 5.42505 17.5998C5.42505 17.3165 5.51672 17.0831 5.70005 16.8998L10.6 11.9998L5.70005 7.0998C5.51672 6.91647 5.42505 6.68314 5.42505 6.3998C5.42505 6.11647 5.51672 5.88314 5.70005 5.6998C5.88338 5.51647 6.11672 5.4248 6.40005 5.4248C6.68338 5.4248 6.91672 5.51647 7.10005 5.6998L12 10.5998L16.9 5.6998C17.0834 5.51647 17.3167 5.4248 17.6 5.4248C17.8834 5.4248 18.1167 5.51647 18.3 5.6998C18.4834 5.88314 18.575 6.11647 18.575 6.3998C18.575 6.68314 18.4834 6.91647 18.3 7.0998L13.4 11.9998L18.3 16.8998C18.4834 17.0831 18.575 17.3165 18.575 17.5998C18.575 17.8831 18.4834 18.1165 18.3 18.2998C18.1167 18.4831 17.8834 18.5748 17.6 18.5748C17.3167 18.5748 17.0834 18.4831 16.9 18.2998L12 13.3998Z"
            fill="black"
          />
        </svg>
      </button>
    </div>
  </div>

  <!-- blur bg -->
  <div
    v-if="isMenuOpen"
    class="fixed inset-0 z-40 bg-black/70 lg:hidden"
    @click="isMenuOpen = false"
  ></div>

  <!-- menu -->
  <transition name="slide-fade">
    <div
      v-if="isMenuOpen"
      class="fixed top-[66px] left-0 right-0 z-50 bg-white shadow-md px-6 md:px-10 py-6 space-y-5 md:space-y-7 lg:hidden"
    >
      <div class="flex items-center justify-between">
        <router-link
          :to="{ name: 'SaleItemGallery' }"
          class="block text-lg uppercase text-gray-800"
          @click="isMenuOpen = false"
        >
          Items
        </router-link>
        <router-link
          :to="{ name: 'SaleItemGallery' }"
          class="block text-lg uppercase text-gray-800 cursor-pointer"
          @click="isMenuOpen = false"
        >
          <RightArrow />
        </router-link>
      </div>
      <div class="flex items-center justify-between">
        <p class="cursor-pointer block text-lg uppercase text-gray-800">
          about us
        </p>
        <RightArrow />
      </div>

      <!-- Before login mobile -->
      <div v-if="!auth.isLoggedIn" class="space-y-5 md:space-y-7 lg:hidden">
        <div class="flex items-center justify-between">
          <router-link
            :to="{ name: 'SignIn' }"
            class="block text-lg uppercase text-gray-800"
            @click="isMenuOpen = false"
          >
            sign In
          </router-link>
          <router-link
            :to="{ name: 'SignIn' }"
            class="block text-lg uppercase text-gray-800 cursor-pointer"
            @click="isMenuOpen = false"
          >
            <RightArrow />
          </router-link>
        </div>
        <div class="flex items-center justify-between">
          <router-link
            :to="{ name: 'RegisterForm' }"
            class="block text-lg uppercase text-gray-800"
            @click="isMenuOpen = false"
          >
            register
          </router-link>
          <router-link
            :to="{ name: 'RegisterForm' }"
            class="block text-lg uppercase text-gray-800 cursor-pointer"
            @click="isMenuOpen = false"
          >
            <RightArrow />
          </router-link>
        </div>
      </div>

      <!-- After login mobile -->
      <div v-else class="space-y-5 md:space-y-7 lg:hidden">
        <div class="flex items-center justify-between">
          <router-link :to="{ name: 'Profile' }">
            <button
              class="itbms-profile block text-lg uppercase text-gray-800 cursor-pointer"
              @click="isMenuOpen = false"
            >
              profile
            </button>
          </router-link>

          <router-link :to="{ name: 'Profile' }">
            <button
              class="block text-lg uppercase text-gray-800 cursor-pointer"
              @click="isMenuOpen = false"
            >
              <RightArrow />
            </button>
          </router-link>
        </div>

        <div class="flex items-center justify-between">
          <button
            class="itbms-logout block text-lg uppercase text-gray-800 cursor-pointer"
            @click="handleLogout(), (isMenuOpen = false)"
          >
            log out
          </button>
          <button
            class="block text-lg uppercase text-gray-800 cursor-pointer"
            @click="handleLogout(), (isMenuOpen = false)"
          >
            <RightArrow />
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<style scoped>
.slide-fade-enter-active {
  transition: opacity 0.3s ease-out, transform 0.3s;
}
.slide-fade-leave-active {
  transition: opacity 0.1s ease-in, transform 0.2s ease-in;
}

.slide-fade-enter-from {
  transform: translateY(-20px);
  opacity: 0;
}
.slide-fade-enter-to {
  transform: translateY(0);
  opacity: 1;
}

.slide-fade-leave-from {
  transform: translateY(0);
  opacity: 1;
}
.slide-fade-leave-to {
  transform: translateY(-80px);
  opacity: 0;
}
</style>
