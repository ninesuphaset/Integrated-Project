<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  thumbnails: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['update:thumbnails'])

const currentImage = ref(props.thumbnails[0] || '')

watch(
  () => props.thumbnails,
  (newVal) => {
    if (!newVal.includes(currentImage.value)) {
      currentImage.value = newVal[0] || ''
    }
  }
)

const selectImage = (image) => {
  currentImage.value = image
  emit('update:thumbnails', props.thumbnails) // ถ้ามีการแก้ไข thumbnails อื่นๆ อนาคตจะส่ง event ได้
}

const fixedThumbnails = computed(() => {
  const tempThumbnails = [...props.thumbnails]
  while (tempThumbnails.length < 4) {
    tempThumbnails.push(null)
  }
  return tempThumbnails
})
</script>

<template>
  <div
    class="flex flex-col-reverse md:flex-row lg:flex-row gap-4 w-full lg:w-1/2 mt-6 md:mt-8 lg:mt-6"
  >
    <div
      class="grid grid-cols-4 md:grid-cols-1 lg:grid-cols-1 w-full md:w-1/6 lg:w-1/5 h-[80px] md:h-[500px] lg:h-[600px] gap-1 md:gap-2 lg:gap-4"
    >
      <div
        v-for="(thumb, index) in fixedThumbnails"
        :key="index"
        class="flex items-center justify-center border border-gray-200 shadow-sm cursor-pointer bg-white"
        @click="thumb && selectImage(thumb)"
      >
        <div v-if="thumb" class="h-full flex items-center justify-center">
          <img :src="thumb" alt="Thumbnail" class="w-4/6 object-contain" />
        </div>

        <div v-else>
          <div class="text-gray-400">
            <svg
              width="40"
              height="40"
              viewBox="0 0 89 89"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M89 4.4945L84.5055 0L0 84.5055L4.4945 89L10.8516 82.6429H76.2857C77.971 82.6403 79.5865 81.9698 80.7781 80.7781C81.9698 79.5865 82.6403 77.971 82.6429 76.2857V10.8516L89 4.4945ZM76.2857 76.2857H17.2088L41.9794 51.5151L49.5412 59.0769C50.7334 60.2687 52.35 60.9382 54.0357 60.9382C55.7214 60.9382 57.3381 60.2687 58.5302 59.0769L63.5714 54.0357L76.2857 66.7405V76.2857ZM76.2857 57.7483L68.0659 49.5285C66.8738 48.3367 65.2571 47.6672 63.5714 47.6672C61.8857 47.6672 60.2691 48.3367 59.0769 49.5285L54.0357 54.5697L46.4802 47.0143L76.2857 17.2088V57.7483ZM12.7143 63.5714V54.0357L28.6071 38.1524L32.9713 42.5197L37.4722 38.0189L33.1016 33.6484C31.9095 32.4566 30.2928 31.7871 28.6071 31.7871C26.9215 31.7871 25.3048 32.4566 24.1126 33.6484L12.7143 45.0467V12.7143H63.5714V6.35714H12.7143C11.0283 6.35714 9.4113 7.02691 8.21911 8.21911C7.02691 9.4113 6.35714 11.0283 6.35714 12.7143V63.5714H12.7143Z"
                fill="#B3B3B3"
              />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <div
      class="flex-1 flex items-center justify-center border border-gray-200 shadow-sm h-[300px] md:h-[500px] lg:h-[600px] bg-white"
    >
      <div
        v-if="currentImage"
        class="h-[300px] lg:h-full flex items-center justify-center"
      >
        <img
          :src="currentImage"
          alt="Main item"
          class="w-3/5 lg:w-3/4 max-w-md object-contain"
        />
      </div>

      <div v-else>
        <div
          class="text-gray-400 h-[300px] lg:h-full flex items-center justify-center"
        >
          <svg
            class="w-[66px] h-[56px] lg:w-[106px] lg:h-[96px]"
            viewBox="0 0 106 96"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M9.58333 95.8333C6.94792 95.8333 4.69264 94.8958 2.8175 93.0206C0.942361 91.1455 0.00319444 88.8886 0 86.25V28.75C0 26.1146 0.939167 23.8593 2.8175 21.9842C4.69583 20.109 6.95111 19.1699 9.58333 19.1667H24.6771L33.5417 9.58333H62.2917V19.1667H37.7344L28.9896 28.75H9.58333V86.25H86.25V43.125H95.8333V86.25C95.8333 88.8854 94.8958 91.1423 93.0206 93.0206C91.1455 94.899 88.8886 95.8365 86.25 95.8333H9.58333ZM86.25 28.75V19.1667H76.6667V9.58333H86.25V0H95.8333V9.58333H105.417V19.1667H95.8333V28.75H86.25ZM47.9167 79.0625C53.9062 79.0625 58.9982 76.9669 63.1925 72.7758C67.3868 68.5847 69.4824 63.4928 69.4792 57.5C69.476 51.5072 67.3804 46.4169 63.1925 42.229C59.0046 38.041 53.9126 35.9439 47.9167 35.9375C41.9207 35.9311 36.8303 38.0283 32.6456 42.229C28.4609 46.4296 26.3637 51.52 26.3542 57.5C26.3446 63.48 28.4417 68.5719 32.6456 72.7758C36.8495 76.9797 41.9399 79.0753 47.9167 79.0625ZM47.9167 69.4792C44.5625 69.4792 41.7274 68.3212 39.4115 66.0052C37.0955 63.6892 35.9375 60.8542 35.9375 57.5C35.9375 54.1458 37.0955 51.3108 39.4115 48.9948C41.7274 46.6788 44.5625 45.5208 47.9167 45.5208C51.2708 45.5208 54.1059 46.6788 56.4219 48.9948C58.7378 51.3108 59.8958 54.1458 59.8958 57.5C59.8958 60.8542 58.7378 63.6892 56.4219 66.0052C54.1059 68.3212 51.2708 69.4792 47.9167 69.4792Z"
              fill="#B3B3B3"
            />
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>
