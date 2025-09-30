<script setup>
import { useRouter } from 'vue-router'
import IconWarning from './icons/IconWarning.vue'
import { onMounted } from 'vue'

const props = defineProps({
  title: {
    type: String,
    required: true,
  },
  message: {
    type: String,
    required: true,
  },
})

onMounted(() => {
  setTimeout(() => {
    router.back()
  }, 2000)
})
const router = useRouter()
</script>
<template>
  <div class="flex flex-col h-[400px] md:h-[500px] lg:h-[650px] items-center">
    <div
      class="my-16 flex-grow flex flex-col items-center justify-center text-center px-4"
    >
      <IconWarning />
      <h2
        class="text-lg md:text-2xl lg:text-3xl font-medium text-[#b3b3b3] mb-4 mt-6"
      >
        {{ props.title }} Not Found
      </h2>
      <p
        class="itbms-message text-sm md:text-base lg:text-lg text-gray-400 mb-6"
      >
        {{ props.message }}
      </p>
      <button
        @click="router.back()"
        class="itbms-button bg-[#B7B7B7] hover:bg-[#D0D0D0] cursor-pointer text-white py-1 px-6 rounded-xl"
      >
        OK
      </button>
    </div>
  </div>
</template>
