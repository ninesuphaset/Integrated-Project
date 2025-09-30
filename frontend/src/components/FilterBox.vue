<script setup>
defineProps({
  label: String,
  placeholder: String,
  active: Boolean,
  customClass: String,
})
</script>

<template>
<div class="relative">
    <div
      class="px-6 lg:px-8 py-2 lg:py-3 cursor-pointer transition-all min-w-26 md:min-w-40 lg:min-w-55"
      :class="[customClass, active ? 'bg-white rounded-full shadow-sm' : '']"
      @click="$emit('toggle')"
    >
      <p class="text-[12px] hidden md:block lg:text-sm font-semibold text-black">{{ label }}</p>
      <p class="text-[12px] lg:text-sm text-gray-500 md:text-gray-400">{{ placeholder }}</p>
    </div>

    <div class="absolute left-0 top-full mt-2 z-10"><slot /></div>
  </div>
</template>

