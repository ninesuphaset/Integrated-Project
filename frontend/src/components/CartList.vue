<script setup>
import { reactive } from 'vue'
import CartListModel from './CartListModel.vue'

defineProps({
  cartItems: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['deleteFromCart', 'updateCart'])

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

const formatPrice = (price) => {
  return Number(price).toLocaleString('th-TH')
}

const increaseQuantity = (item) => {
  const newQuantity = item.quantity + 1
  if (newQuantity <= item.stock) {
    emit('updateCart', { id: item.cartItemId, quantity: newQuantity })
  }
}

const decreaseQuantity = (item) => {
  emit('updateCart', { id: item.cartItemId, quantity: item.quantity - 1 })
}
</script>

<template>
  <div>
    <!-- loop ตาม seller -->
    <div v-for="seller in cartItems" :key="seller.seller_id" class="mb-4">
      <CartListModel
        v-if="cartItems.length > 0"
        :cartItems="seller.cart"
        :sellerName="seller.sellerName"
        @deleteFromCart="$emit('deleteFromCart', $event)"
      >
        <template #yourItem="{ itemInList }">
          <div class="itbms-item-row flex items-center text-[15px] mb-2 mt-6">
            <input
              type="checkbox"
              class="w-4 h-4 mr-4"
              v-model="itemInList.selected"
            />
            <div
              class="grid grid-cols-[100px_1fr_auto_auto_auto] items-center w-full gap-10"
            >
              <img :src="getItemImage(itemInList)" alt="" class="w-20" />
              <div class="text-left">
                <span class="itbms-item-description block">
                  <span class="font-medium">{{ itemInList.brandName }}</span>
                  {{ itemInList.model }} ({{ itemInList.storageGb || '-' }}GB,
                  {{ itemInList.color || '-' }})
                </span>
              </div>

              <!-- ปุ่ม quantity -->
              <div>
                <div
                  class="flex items-center space-x-4 border border-[#0171E3] text-[#0171E3] rounded-md py-1 px-4 font-medium justify-center"
                >
                  <button
                    class="itbms-dec-qty-button cursor-pointer"
                    @click="decreaseQuantity(itemInList)"
                  >
                    -
                  </button>
                  <span class="itbms-item-quantity">{{
                    itemInList.quantity
                  }}</span>
                  <button
                    class="itbms-inc-qty-button cursor-pointer"
                    @click="increaseQuantity(itemInList)"
                  >
                    +
                  </button>
                </div>
              </div>

              <span class="itbms-item-total-price font-medium text-right">
                Price: {{ formatPrice(itemInList.price) }}
              </span>

              <div
                @click="$emit('deleteFromCart', itemInList.cartItemId)"
                class="cursor-pointer"
              >
                <svg
                  width="16"
                  height="17"
                  viewBox="0 0 16 17"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M13.25 2.17969V14.3205C13.25 14.7541 12.8125 15.1877 12.375 15.1877H8H3.625C3.1875 15.1877 2.75 14.7541 2.75 14.3205V2.17969"
                    stroke="#E84954"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <path
                    d="M1 2.17969H15"
                    stroke="#E84954"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <path
                    d="M6.25 1.3125H9.75M6.25 5.64849V11.7189M9.75 5.64849V11.7189"
                    stroke="#E84954"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
              </div>
            </div>
          </div>
          <div class="flex justify-end">
            <p
              v-if="itemInList.quantity === itemInList.stock"
              class="text-red-500 text-xs"
            >
              You have reached the maximum quantity available for this item
            </p>
          </div>
        </template>
      </CartListModel>
    </div>
  </div>
</template>
