<script setup>
defineProps({
  items: {
    type: Array,
    required: true,
  }
})
</script>

<template>
  <div>
    <div :class="`grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-2 md:gap-y-3 lg:gap-4`">
      <div v-for="(item, index) in items" :key="index">
        <slot name="yourItem" :itemInList="item"></slot>
      </div>
    </div>
  </div>
</template>
