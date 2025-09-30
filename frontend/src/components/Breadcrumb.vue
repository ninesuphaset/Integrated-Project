<script setup>
const props = defineProps({
  breadcrumbs: {
    type: Array,
    default: () => [],
  },
})
</script>

<template>
  <nav class="flex" aria-label="Breadcrumb">
    <ol class="inline-flex items-center space-x-1 md:space-x-2 rtl:space-x-reverse">
      <li
        v-for="(crumb, index) in breadcrumbs"
        :key="index"
        class="inline-flex items-center"
      >
        <template v-if="index !== 0">
          <svg
            class="rtl:rotate-180 w-3 h-3 text-gray-400 mx-1"
            aria-hidden="true"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 6 10"
          >
            <path
              stroke="currentColor"
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="m1 9 4-4-4-4"
            />
          </svg>
        </template>

        <router-link
          v-if="crumb.to"
          :to="crumb.to"
          :class="[
            'text-sm font-medium',
            crumb.color ?? 'text-[#959496]',
            crumb.className ?? ''
          ]"
        >
          {{ crumb.label }}
        </router-link>
        <span
          v-else
          :class="[
            'text-sm font-medium',
            crumb.color ?? 'text-[#131927]',
            crumb.className ?? ''
          ]"
        >
          {{ crumb.label }}
        </span>
      </li>
    </ol>
  </nav>
</template>
