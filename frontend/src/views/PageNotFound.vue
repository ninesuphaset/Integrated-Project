<script setup>
import IconWarning from '@/components/icons/IconWarning.vue';
</script>
<template>
  <div class="flex flex-col min-h-[600px]">
    <div
      class="my-16 flex-grow flex flex-col items-center justify-center text-center px-4"
    >
        <IconWarning/>
      <h2
        class="text-lg md:text-2xl lg:text-3xl font-semibold text-[#b3b3b3] mt-6 mb-4"
      >
        Page Not Found
      </h2>
      <p class="text-sm md:text-base lg:text-lg text-gray-400 mb-6">
        Sorry! This page doesn't exist on our system.
      </p>
      <router-link
        to="/"
        class="text-[#0171E3] hover:underline rounded-xl transition-all text-sm md:text-base"
      >
        Back to Home page
      </router-link>
    </div>
  </div>
</template>
