<script setup>
import { computed } from 'vue'

const props = defineProps({
  cartItems: {
    type: Array,
    required: true,
  },
  sellerName: {
    type: String,
    required: true,
  },
})

const emit = defineEmits(['deleteFromCart'])

// checkbox seller
const isSellerSelected = computed(() => {
  return (
    props.cartItems.length > 0 && props.cartItems.every((item) => item.selected)
  )
})

// toggle checkbox seller
const toggleSeller = (checked) => {
  props.cartItems.forEach((item) => {
    item.selected = checked
  })
}
</script>

<template>
  <div class="itbms-row bg-white rounded-lg px-8 py-6">
    <!-- seller header -->
    <div class="flex items-center space-x-6">
      <input
        type="checkbox"
        class="itbms-select-nickname w-4 h-4"
        :checked="isSellerSelected"
        @change="toggleSeller($event.target.checked)"
      />
      <div class="flex items-center space-x-2">
        <!-- icon -->
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
            d="M9.45037 12.9996V8.99941C9.45037 8.64577 9.30989 8.30662 9.05983 8.05656C8.80977 7.8065 8.47062 7.66602 8.11698 7.66602H6.78359C6.42995 7.66602 6.0908 7.8065 5.84074 8.05656C5.59068 8.30662 5.4502 8.64577 5.4502 8.99941V12.9996"
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

        <span class="itbms-nickname font-medium">{{ sellerName }}</span>
      </div>
    </div>

    <hr class="border-t border-[#F0F0F0] mt-3 mb-4" />

    <!-- products ของ seller -->
    <div v-for="(item, index) in cartItems" :key="index">
      <slot name="yourItem" :itemInList="item"></slot>
    </div>
  </div>
</template>
