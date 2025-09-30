<script setup>
import { ref } from 'vue'
import { deleteItemById, editItem } from '@/libs/fetchUtils'
import { useCart } from '@/stores/cart'
import CartList from '@/components/CartList.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'

const cart = useCart()

const BASE_API_URL = import.meta.env.VITE_APP_URL + '/itb-mshop/v1/carts'
const isLoading = ref(false)

const deleteFromCart = async (cartId) => {
  try {
    isLoading.value = true
    const res = await deleteItemById(`${BASE_API_URL}/${cartId}`)
    if (res.code === 204) {
      cart.removeCart(cartId)
    }
  } catch (error) {
    console.error(error)
  } finally {
    isLoading.value = false
  }
}

const updateCart = async (cartItem) => {
  try {
    isLoading.value = true

    if (cartItem.quantity <= 0) {
      cart.removeCart(cartItem.id)
      await editItem(`${BASE_API_URL}/${cartItem.id}`, { quantity: 0 })
      return
    }

    const res = await editItem(`${BASE_API_URL}/${cartItem.id}`, {
      quantity: cartItem.quantity,
    })

    if (res.code === 200) {
      cart.updateCartItem({
        cartItemId: cartItem.id,
        newQuantity: cartItem.quantity,
      })
    } else if (res.code === 204) {
      cart.removeCart(cartItem.id)
    }
  } catch (error) {
    console.error(error)
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <LoadingSpinner v-if="isLoading" />
  <div v-else class="bg-[#F6F6F6] min-h-screen py-8 px-12">
    <h1 class="font-semibold text-2xl mt-4">Shopping Cart</h1>

    <div v-if="!cart.cartItems.length" class="text-center py-24">
      <p class="mb-4 text-xl">Your cart is empty</p>
      <router-link :to="{ name: 'SaleItemGallery' }">
        <button
          class="bg-[#0171E3] hover:bg-[#157DE6] text-white py-1 px-4 rounded mt-2"
        >
          Shop Now
        </button>
      </router-link>
    </div>

    <div v-else class="flex space-x-8 mt-6">
      <div class="w-2/3 space-y-4">
        <div class="bg-white rounded-lg px-8 py-4 flex items-center space-x-4">
          <input
            type="checkbox"
            class="itbms-select-all w-4 h-4"
            :checked="cart.isAllSelected"
            @change="cart.toggleSelectAll($event.target.checked)"
          />
          <span>Select All</span>
        </div>

        <CartList
          :cartItems="cart.cartItems"
          @deleteFromCart="deleteFromCart"
          @updateCart="updateCart"
        />
      </div>

      <div class="bg-white flex-1 rounded-lg px-8 pt-4 pb-6 h-fit">
        <h3 class="font-semibold text-lg mt-2">Cart Summary</h3>
        <hr class="mt-4 mb-6 border-t border-[#E2E2E2]" />
        <div class="space-y-4">
          <div class="flex justify-between font-medium">
            <p class="itbms-total-order-items">Total items:</p>
            <p>...</p>
          </div>
          <div class="flex justify-between font-medium">
            <p class="itbms-total-order-price">Total price:</p>
            <p>...</p>
          </div>
        </div>
        <hr class="my-6 border-t border-[#E2E2E2]" />
        <button
          class="itbms-place-order-button rounded-md text-white bg-[#0171E3] hover:bg-[#157DE6] active:bg-[#3A98F6] py-2 w-full"
        >
          Place Order
        </button>
      </div>
    </div>
  </div>
</template>
