<script setup>
import { useRouter } from 'vue-router'
import { addItem } from '@/libs/fetchUtils'
import { useAuth } from '@/stores/auth'
import { useCart } from '@/stores/cart'
import GalleryModel from '@/components/GalleryModel.vue'

defineProps({
  items: {
    type: Array,
    required: true,
  },
})

const router = useRouter()
const auth = useAuth()
const cart = useCart()

const formatPrice = (price) => {
  return Number(price).toLocaleString('th-TH')
}

const APP_URL = import.meta.env.VITE_APP_URL
const basePath = `${APP_URL}/itb-mshop/v2/images/`

const getItemImage = (item) => {
  const images = item.saleItemImages || []
  if (images.length > 0 && images[0]?.fileName) {
    return `${basePath}${images[0].fileName}?t=${Date.now()}`
  }
  return 'http://intproj24.sit.kmutt.ac.th/kp2/item-image/item1.jpeg'
  // return 'http://intproj24.sit.kmutt.ac.th/kp2/item-image/no-image.png'
}

const getCartQuantity = (itemId) => {
  return cart.cartItems.reduce((acc, seller) => {
    const item = seller.cart.find((i) => i.saleitemId === itemId)
    return item ? acc + item.quantity : acc
  }, 0)
}

const addItemToCart = async (item) => {
  if (!auth.user?.id) {
    router.push({ name: 'SignIn' })
    return
  }
    try {
      const res = await addItem(`${APP_URL}/itb-mshop/v1/carts`, {
        userId: auth.user.id,
        saleItemId: item.id,
        quantity: 1,
      })

      if (res.code === 200 || res.code === 201) {
        console.log(res.body)
        cart.fetchCart()
      } else {
        console.error(res.body.error)
      }
    } catch (error) {
      console.error(error)
    }
}
</script>

<template>
  <div class="mx-8 md:mx-14 lg:mx-18">
    <GalleryModel v-if="items.length > 0" :items="items">
      <template #yourItem="{ itemInList }">
        <div
          class="itbms-row rounded-2xl shadow-md pt-4 pb-1 px-4 md:p-6 lg:p-6 bg-white hover:shadow-lg transition max-w-60 md:max-w-58 lg:max-w-62 h-[330px] md:h-[400px] lg:h-[380px] xl:h-[450px] flex flex-col"
        >
          <router-link
            :to="{
              name: 'SaleItemDetail',
              params: { saleItemId: itemInList.id },
            }"
          >
            <div class="flex justify-center">
              <img
                :src="getItemImage(itemInList)"
                alt="item-image"
                class="w-3/4 object-contain"
              />
            </div>
            <div class="mt-4 md:mt-3 lg:mt-6 flex-1 overflow-hidden">
              <p class="itbms-brand text-base md:text-lg lg:text-lg font-bold">
                {{ itemInList.brandName }}
              </p>
              <p
                class="text-sm md:text-base lg:text-base itbms-model font-medium"
              >
                {{ itemInList.model }}
              </p>
              <p class="text-sm md:text-base lg:text-base">
                <span class="itbms-ramGb">{{ itemInList.ramGb ?? '-' }}</span
                >/<span class="itbms-storageGb">{{
                  itemInList.storageGb ?? '-'
                }}</span>
                <span class="itbms-storageGb-unit">GB</span>
              </p>
              <p class="text-sm md:text-base lg:text-base itbms-color">
                {{ itemInList.color ?? '-' }}
              </p>
              <p
                class="itbms-price text-gray-600 mt-1 md:mt-2 lg:mt-2 text-sm md:text-base lg:text-base"
              >
                <span
                  class="itbms-price-unit font-medium md:font-semibold lg:font-semibold"
                  >Baht</span
                >
                {{ formatPrice(itemInList.price) }}
              </p>
            </div>
          </router-link>

          <button
            class="itbms-add-to-cart-button mt-2 bg-[#0171E3] hover:bg-[#157DE6] active:bg-[#3A98F6] text-sm xl:text-base text-white py-1.5 rounded-full cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-[#0171E3] disabled:active:bg-[#0171E3]"
            :disabled="getCartQuantity(itemInList.id) >= itemInList.quantity"
            @click="addItemToCart(itemInList)"
          >
            <div class="flex items-center justify-center space-x-3">
              <div class="hidden xl:block">
                <svg
                  width="16"
                  height="16"
                  viewBox="0 0 16 16"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M12.75 12.5C13.1478 12.5 13.5294 12.658 13.8107 12.9393C14.092 13.2206 14.25 13.6022 14.25 14C14.25 14.3978 14.092 14.7794 13.8107 15.0607C13.5294 15.342 13.1478 15.5 12.75 15.5C12.3522 15.5 11.9706 15.342 11.6893 15.0607C11.408 14.7794 11.25 14.3978 11.25 14C11.25 13.1675 11.9175 12.5 12.75 12.5ZM0.75 0.5H3.2025L3.9075 2H15C15.1989 2 15.3897 2.07902 15.5303 2.21967C15.671 2.36032 15.75 2.55109 15.75 2.75C15.75 2.8775 15.7125 3.005 15.66 3.125L12.975 7.9775C12.72 8.435 12.225 8.75 11.6625 8.75H6.075L5.4 9.9725L5.3775 10.0625C5.3775 10.1122 5.39725 10.1599 5.43242 10.1951C5.46758 10.2302 5.51527 10.25 5.565 10.25H14.25V11.75H5.25C4.85217 11.75 4.47064 11.592 4.18934 11.3107C3.90803 11.0294 3.75 10.6478 3.75 10.25C3.75 9.9875 3.8175 9.74 3.93 9.53L4.95 7.6925L2.25 2H0.75V0.5ZM5.25 12.5C5.64782 12.5 6.02935 12.658 6.31066 12.9393C6.59196 13.2206 6.75 13.6022 6.75 14C6.75 14.3978 6.59196 14.7794 6.31066 15.0607C6.02935 15.342 5.64782 15.5 5.25 15.5C4.85217 15.5 4.47064 15.342 4.18934 15.0607C3.90803 14.7794 3.75 14.3978 3.75 14C3.75 13.1675 4.4175 12.5 5.25 12.5ZM12 7.25L14.085 3.5H4.605L6.375 7.25H12Z"
                    fill="white"
                  />
                </svg>
              </div>
              <span>Add to cart</span>
            </div>
          </button>
        </div>
      </template>
    </GalleryModel>

    <div
      v-else
      class="flex justify-center items-center h-[200px] md:h-[300px] lg:h-[500px]"
    >
      <p class="text-lg md:text-xl lg:text-2xl">no sale item</p>
    </div>
  </div>
</template>
