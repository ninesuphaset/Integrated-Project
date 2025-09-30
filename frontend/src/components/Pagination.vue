<script setup>
import { ref, computed } from "vue";

const props = defineProps({
  page: { type: Number, required: true },
  pageSize: { type: Number, required: true },
  totalItems: { type: Number, required: true },
  visiblePageCount: { type: Number, default: 10 },
});

const emit = defineEmits(["update:page", "update:pageSize"]);

const pageStart = ref(0);
const totalPages = computed(() => Math.ceil(props.totalItems / props.pageSize))

const pageNumbers = computed(() => {
  const pages = [];
  const end = Math.min(
    pageStart.value + props.visiblePageCount,
    totalPages.value
  );
  for (let i = pageStart.value; i < end; i++) {
    pages.push(i);
  }
  return pages;
});

const goToPage = (targetPage) => {
  if (targetPage < 0 || targetPage >= totalPages.value) return;
  emit("update:page", targetPage);

  if (targetPage >= pageStart.value + props.visiblePageCount) {
    pageStart.value = targetPage - props.visiblePageCount + 1;
  } else if (targetPage < pageStart.value) {
    pageStart.value = targetPage;
  }
};

const goNext = () => goToPage(props.page + 1)
const goPrev = () => goToPage(props.page - 1)
const goFirst = () => goToPage(0);
const goLast = () => goToPage(totalPages.value - 1)
</script>

<template>
  <div
    v-show="props.totalItems > props.pageSize"
    class="flex flex-wrap justify-center items-center gap-2 mt-6 px-4"
  >
    <button
      @click="goFirst"
      :disabled="props.page === 0"
      class="itbms-page-first px-4 sm:px-3 py-2 rounded-md border border-gray-300 text-gray-700 text-xs sm:text-sm disabled:opacity-50 hover:bg-gray-100 min-w-0"
    >
      First
    </button>
    <button
      @click="goPrev"
      :disabled="props.page === 0"
      class="itbms-page-prev px-4 sm:px-3 py-2 rounded-md border border-gray-300 text-gray-700 text-xs sm:text-sm disabled:opacity-50 hover:bg-gray-100 min-w-0"
    >
      Prev
    </button>

    <div class="flex gap-2">
      <button
        v-for="i in pageNumbers"
        :key="i"
        :class="[
          `itbms-page-${i}`,
          'px-4 py-2 rounded-md border text-sm',
          props.page === i
            ? 'border-blue-500 bg-blue-100 text-blue-700'
            : 'border-gray-300 text-gray-700 hover:bg-gray-100',
        ]"
        @click="goToPage(i)"
      >
        {{ i + 1 }}
      </button>
    </div>

    <button
      @click="goNext"
      :disabled="props.page >= totalPages - 1"
      class="itbms-page-next px-2 sm:px-3 py-2 rounded-md border border-gray-300 text-gray-700 text-xs sm:text-sm disabled:opacity-50 hover:bg-gray-100 min-w-0"
    >
      Next
    </button>
    <button
      @click="goLast"
      :disabled="props.page >= totalPages - 1"
      class="itbms-page-last px-2 sm:px-3 py-2 rounded-md border border-gray-300 text-gray-700 text-xs sm:text-sm disabled:opacity-50 hover:bg-gray-100 min-w-0"
    >
      Last
    </button>
  </div>
</template>
