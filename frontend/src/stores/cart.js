import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getItems } from '@/libs/fetchUtils'
import { useAuth } from '@/stores/auth'

export const useCart = defineStore('cart', () => {
  const cartItems = ref([])

  const setCartItems = (items) => {
    cartItems.value = items
  }

  const fetchCart = async () => {
    const auth = useAuth()
    if (!auth.user?.id) return
    try {
      const CART_ENDPOINT = import.meta.env.VITE_APP_URL + '/itb-mshop/v1/carts'
      const res = await getItems(`${CART_ENDPOINT}/${auth.user.id}`)
      if (res.code === 200) {
        cartItems.value = res.body
      } else {
        cartItems.value = []
      }
    } catch (err) {
      console.error(err)
      cartItems.value = []
    }
  }

  const removeCart = (cartItemId) => {
    cartItems.value = cartItems.value.map((seller) => {
      return {
        ...seller,
        cart: seller.cart.filter((item) => item.cartItemId !== cartItemId),
      }
    })
  }

  const updateCartItem = ({ cartItemId, newQuantity }) => {
    cartItems.value = cartItems.value
      .map((seller) => ({
        ...seller,
        cart: seller.cart
          .map((item) =>
            item.cartItemId === cartItemId
              ? { ...item, quantity: newQuantity }
              : item
          )
          .filter((item) => item.quantity > 0), // ลบ item ที่ quantity <= 0
      }))
      .filter((seller) => seller.cart.length > 0) // ลบ seller ที่ cart ว่าง
  }

  const totalQty = computed(() => {
    return cartItems.value.reduce((acc, seller) => {
      return (
        acc + seller.cart.reduce((sum, item) => sum + (item.quantity || 0), 0)
      )
    }, 0)
  })

  const isAllSelected = computed(() => {
    if (!cartItems.value.length) return false
    return cartItems.value.every((seller) =>
      seller.cart.every((item) => item.selected)
    )
  })

  const toggleSelectAll = (checked) => {
    cartItems.value.forEach((seller) => {
      seller.cart.forEach((item) => {
        item.selected = checked
      })
    })
  }

   const clearCart = () => {
    cartItems.value = []
  }

  return {
    cartItems,
    fetchCart,
    removeCart,
    updateCartItem,
    totalQty,
    setCartItems,
    isAllSelected,
    toggleSelectAll,
    clearCart
  }
})
