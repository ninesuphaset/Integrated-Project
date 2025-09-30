<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCart } from '@/stores/cart'
import { useAuth } from '@/stores/auth'
import Breadcrumb from '@/components/Breadcrumb.vue'
import ImageGallery from '@/components/ImageGallery.vue'
import PopupModal from '@/components/PopupModal.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import { getItemById, addItem } from '@/libs/fetchUtils'
import NotFound from '@/components/NotFound.vue'

const emit = defineEmits(['deleteSaleItem'])

const route = useRoute()
const router = useRouter()

const cart = useCart()
const auth = useAuth()

const { saleItemId } = route.params

const APP_URL = import.meta.env.VITE_APP_URL
const selectedItem = ref({})
const isLoading = ref(true)
const notFound = ref(false)

const baseImagePath = 'http://intproj24.sit.kmutt.ac.th/kp2/item-image/'
const basePath = `${APP_URL}/itb-mshop/v2/images/`

const fetchItem = async (id) => {
  try {
    isLoading.value = true
    const res = await getItemById(`${APP_URL}/itb-mshop/v2/sale-items/${id}`)
    if (res.code === 200) {
      selectedItem.value = res.body
      notFound.value = false
    } else {
      console.error(res.body.error)
      selectedItem.value = {}
      notFound.value = true
    }
  } catch (error) {
    console.error('Error loading item:', error)
    selectedItem.value = {}
    notFound.value = true
  } finally {
    isLoading.value = false
  }
}

onMounted(() => fetchItem(saleItemId))

// watch route.params เพื่อ reload ทุกครั้งที่กลับมาหน้านี้
watch(
  () => route.params.saleItemId,
  (newId) => {
    if (newId) fetchItem(newId)
  }
)

// thumbnails
const thumbnails = computed(() => {
  if (
    !selectedItem.value.saleItemImages ||
    selectedItem.value.saleItemImages.length === 0
  ) {
    return [
      `${baseImagePath}/item1.jpeg`,
      `${baseImagePath}/item2.jpeg`,
      `${baseImagePath}/item3.jpeg`,
      `${baseImagePath}/item4.jpeg`,
    ]
  }

  return selectedItem.value.saleItemImages.map(
    (img) => `${basePath}${img.fileName}?t=${Date.now()}` // ใช้ Date.now() เพื่อบังคับ browser โหลดใหม่
  )
})

const priceFormatted = computed(() => {
  if (
    selectedItem.value.price === null ||
    selectedItem.value.price === undefined
  )
    return ''
  return Number(selectedItem.value.price).toLocaleString('th-TH')
})

const isPopupOpen = ref(false)
const handleDelete = () => {
  emit('deleteSaleItem', saleItemId)
  isPopupOpen.value = false
  router.push('/sale-items')
}

const quantity = ref(1)

const cartQuantity = computed(() => {
  const selectedId = selectedItem.value?.id
  if (!selectedId) return 0

  return cart.cartItems.reduce((acc, seller) => {
    const item = seller.cart.find(i => i.saleitemId === selectedId)
    return item ? item.quantity : acc
  }, 0)
})

const maxValue = computed(() => {
  const stock = selectedItem.value?.quantity ?? 0
  return stock - cartQuantity.value
})

const increaseQuantity = () => {
  if (quantity.value < maxValue.value) {
    quantity.value += 1
  }
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value -= 1
  }
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
      quantity: quantity.value,
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
  <div class="mx-10 md:mx-14 lg:mx-18">
    <div>
      <PopupModal
        :show="isPopupOpen"
        @close="isPopupOpen = false"
        @confirm="handleDelete"
      >
        <template #title>
          <h3 class="itbms-message">Do you want to delete this sale item?</h3>
        </template>

        <template #message>
          <p>This action is permanent and cannot be undone.</p>
        </template>
      </PopupModal>
    </div>
    <LoadingSpinner v-if="isLoading" />

    <NotFound
      v-else-if="notFound"
      title="Item"
      message="The requested sale item does not exist."
    />

    <div v-else class="mt-8">
      <Breadcrumb
        :breadcrumbs="[
          {
            label: 'Home',
            to: { name: 'SaleItemGallery' },
            className: 'itbms-home-button',
          },
          {
            label:
              selectedItem.brandName +
              ' ' +
              selectedItem.model +
              ' ' +
              (selectedItem.ramGb ?? '') +
              (selectedItem.ramGb && selectedItem.storageGb ? '/' : '') +
              (selectedItem.storageGb ?? '') +
              (selectedItem.ramGb || selectedItem.storageGb ? 'GB' : '') +
              ' ' +
              (selectedItem.color ?? ''),
          },
        ]"
      />

      <!-- seller's name -->
      <div class="flex items-center mt-8 space-x-2">
        <svg
          width="15"
          height="14"
          viewBox="0 0 15 14"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M1.56152 5.66602V11.6663C1.56152 12.0199 1.70201 12.3591 1.95207 12.6091C2.20212 12.8592 2.54128 12.9997 2.89492 12.9997H12.2287C12.5823 12.9997 12.9215 12.8592 13.1715 12.6091C13.4216 12.3591 13.5621 12.0199 13.5621 11.6663V5.66602"
            stroke="black"
            stroke-width="1.5"
          />
          <path
            d="M9.45037 13.0001V8.9999C9.45037 8.64626 9.30989 8.30711 9.05983 8.05705C8.80977 7.80699 8.47062 7.6665 8.11698 7.6665H6.78359C6.42995 7.6665 6.0908 7.80699 5.84074 8.05705C5.59068 8.30711 5.4502 8.64626 5.4502 8.9999V13.0001"
            stroke="black"
            stroke-width="1.5"
            stroke-miterlimit="16"
          />
          <path
            d="M14.1075 5.24286L12.9781 1.29001C12.9542 1.20644 12.9037 1.13293 12.8343 1.0806C12.7649 1.02828 12.6803 0.999981 12.5934 1H9.89526L10.2119 4.80284C10.2168 4.86396 10.2362 4.92304 10.2684 4.9752C10.3006 5.02737 10.3448 5.07112 10.3973 5.10285C10.6573 5.25819 11.1653 5.54487 11.562 5.66688C12.2394 5.87555 13.2287 5.80021 13.7928 5.73088C13.8476 5.72379 13.9002 5.70482 13.947 5.6753C13.9937 5.64577 14.0335 5.6064 14.0634 5.55994C14.0934 5.51347 14.1129 5.46102 14.1205 5.40626C14.1281 5.35149 14.1236 5.29573 14.1075 5.24286Z"
            stroke="black"
            stroke-width="1.5"
          />
          <path
            d="M8.89508 5.66688C9.27376 5.5502 9.75378 5.28419 10.0218 5.12552C10.0842 5.08821 10.1348 5.03409 10.1678 4.96936C10.2008 4.90462 10.2149 4.83189 10.2085 4.7595L9.89512 1H5.22825L4.9149 4.7595C4.90835 4.832 4.92239 4.90487 4.95542 4.96973C4.98844 5.0346 5.0391 5.08883 5.10157 5.12619C5.36959 5.28419 5.84961 5.5502 6.22829 5.66688C7.22367 5.97356 7.8997 5.97356 8.89508 5.66688Z"
            stroke="black"
            stroke-width="1.5"
          />
          <path
            d="M2.14561 1.29001L1.01623 5.24352C1.00028 5.29631 0.996023 5.35194 1.00374 5.40654C1.01145 5.46114 1.03095 5.51341 1.0609 5.55972C1.09084 5.60602 1.1305 5.64526 1.17712 5.6747C1.22375 5.70414 1.27623 5.72309 1.33091 5.73021C1.89427 5.80022 2.88431 5.87489 3.56167 5.66688C3.95836 5.54487 4.46705 5.25819 4.72639 5.10352C4.77895 5.07172 4.82317 5.02786 4.8554 4.97558C4.88763 4.92329 4.90694 4.86407 4.91173 4.80284L5.22841 1H2.53029C2.44337 0.999981 2.35881 1.02828 2.2894 1.0806C2.21999 1.13293 2.16951 1.20644 2.14561 1.29001Z"
            stroke="black"
            stroke-width="1.5"
          />
        </svg>
        <span class="font-medium">{{ selectedItem.sellerName }}</span>
      </div>

      <div class="itbms-row lg:flex">
        <ImageGallery :thumbnails="thumbnails" />

        <div class="mt-12 lg:mt-6 w-full lg:w-[40%] lg:pl-10">
          <div class="mb-4">
            <span class="lg:hidden text-base lg:text-4xl font-medium"
              >Brand:
            </span>
            <span
              class="itbms-brand lg:text-[#959496] lg:uppercase lg:tracking-widest text-base lg:font-medium"
              >{{ selectedItem.brandName }}</span
            >
          </div>
          <div class="text-base lg:text-4xl mb-4">
            <span class="lg:hidden text-base lg:text-4xl mb-4 font-medium"
              >Model:
            </span>
            <span class="itbms-model">{{ selectedItem.model }}</span>
          </div>
          <p class="text-base lg:text-lg font-medium mb-4">
            Price:
            <span class="itbms-price font-normal lg:font-medium">{{
              priceFormatted
            }}</span>
            <span class="itbms-price-unit font-normal lg:font-medium">
              Baht</span
            >
          </p>
          <p class="font-medium mb-1">Description:</p>
          <p
            class="itbms-description text-[#959496] leading-relaxed mb-4 font-mitr"
          >
            {{ selectedItem.description }}
          </p>
          <div class="space-y-2 text-base">
            <p>
              <span class="font-medium">RAM: </span>
              <span
                class="itbms-ramGb"
                :class="{ 'text-gray-400': !selectedItem.ramGb }"
              >
                {{ selectedItem.ramGb ?? '-' }}
              </span>
              <span class="itbms-ramGb-unit">{{
                selectedItem.ramGb ? 'GB' : ''
              }}</span>
            </p>
            <p>
              <span class="font-medium">Screen Size: </span>
              <span
                class="itbms-screenSizeInch"
                :class="{ 'text-gray-400': !selectedItem.screenSizeInch }"
              >
                {{ selectedItem.screenSizeInch ?? '-' }}
              </span>
              <span class="itbms-screenSizeInch-unit">
                {{ selectedItem.screenSizeInch ? ' Inches' : '' }}</span
              >
            </p>
            <p>
              <span class="font-medium">Storage: </span>
              <span
                class="itbms-storageGb"
                :class="{ 'text-gray-400': !selectedItem.storageGb }"
              >
                {{ selectedItem.storageGb ?? '-' }}
              </span>
              <span class="itbms-storageGb-unit">
                {{ selectedItem.storageGb ? 'GB' : '' }}</span
              >
            </p>
            <p>
              <span class="font-medium">Color: </span>
              <span
                class="itbms-color"
                :class="{ 'text-gray-400': !selectedItem.color }"
              >
                {{ selectedItem.color || '-' }}
              </span>
            </p>
            <p>
              <span class="font-medium">Available quantity: </span>
              <span class="itbms-quantity">
                {{ selectedItem.quantity }}
              </span>
              <span class="itbms-quantity-unit"> units</span>
            </p>
          </div>

          <div class="mt-10 space-x-2 flex">
            <div
              class="space-x-10 border border-[#0171E3] text-[#0171E3] rounded-full py-2 px-6 font-medium"
            >
              <button
                class="itbms-dec-qty-button cursor-pointer"
                @click="decreaseQuantity"
              >
                -
              </button>
              <span class="itbms-add-to-cart-quantity">{{ quantity }}</span>
              <button
                class="itbms-inc-qty-button cursor-pointer"
                @click="increaseQuantity"
              >
                +
              </button>
            </div>
            <button
              class="itbms-add-to-cart-button text-white bg-[#0171E3] hover:bg-[#157DE6] active:bg-[#3A98F6] py-1 flex-1 rounded-full cursor-pointer"
              @click="addItemToCart(selectedItem)"
            >
              Add to cart
            </button>
          </div>

          <p v-if="maxValue <= 0" class="text-red-500 text-sm mt-6">
            You have reached the maximum quantity available in stock
          </p>

          <!-- seller -->
          <!-- <div class="mt-10 space-x-4 lg:space-x-8">
            <router-link :to="{ name: 'EditSaleItem' }">
              <button
                class="itbms-edit-button cursor-pointer border text-[#0171E3] hover:bg-[#DBEDFF] py-1 px-6 lg:py-2 lg:px-9 rounded-3xl"
              >
                Edit
              </button>
            </router-link>

            <button
              @click="isPopupOpen = true"
              class="itbms-delete-button cursor-pointer border text-[#0171E3] hover:bg-[#DBEDFF] py-1 px-4 lg:py-2 lg:px-7 rounded-3xl"
            >
              Delete
            </button>
          </div> -->
        </div>
      </div>
    </div>
  </div>
</template>
