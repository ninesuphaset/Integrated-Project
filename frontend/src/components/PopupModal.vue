<script setup>
const props = defineProps({
  show: Boolean,
  width: {
    type: String,
    default: '560px',
  },
})

const emit = defineEmits(['close', 'confirm'])
</script>

<template>
  <div
    v-if="show"
    @click="$emit('close')"
    class="fixed inset-0 bg-black/20 flex justify-center items-center z-50"
  >
    <div
      @click.stop
      class="bg-gray-100 rounded-xl shadow-md pt-6 pb-8 pl-10 pr-6 h-auto flex flex-col"
      :style="{ width: props.width }"
    >
      <div class="self-end">
        <button @click="$emit('close')" class="cursor-pointer">
          <svg
            width="14"
            height="14"
            viewBox="0 0 14 14"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M7.00005 8.3998L2.10005 13.2998C1.91672 13.4831 1.68338 13.5748 1.40005 13.5748C1.11672 13.5748 0.883382 13.4831 0.700048 13.2998C0.516715 13.1165 0.425049 12.8831 0.425049 12.5998C0.425049 12.3165 0.516715 12.0831 0.700048 11.8998L5.60005 6.9998L0.700048 2.0998C0.516715 1.91647 0.425049 1.68314 0.425049 1.3998C0.425049 1.11647 0.516715 0.883138 0.700048 0.699804C0.883382 0.516471 1.11672 0.424805 1.40005 0.424805C1.68338 0.424805 1.91672 0.516471 2.10005 0.699804L7.00005 5.5998L11.9 0.699804C12.0834 0.516471 12.3167 0.424805 12.6 0.424805C12.8834 0.424805 13.1167 0.516471 13.3 0.699804C13.4834 0.883138 13.575 1.11647 13.575 1.3998C13.575 1.68314 13.4834 1.91647 13.3 2.0998L8.40005 6.9998L13.3 11.8998C13.4834 12.0831 13.575 12.3165 13.575 12.5998C13.575 12.8831 13.4834 13.1165 13.3 13.2998C13.1167 13.4831 12.8834 13.5748 12.6 13.5748C12.3167 13.5748 12.0834 13.4831 11.9 13.2998L7.00005 8.3998Z"
              fill="#B3B3B3"
            />
          </svg>
        </button>
      </div>

      <div>
        <slot name="icon"></slot>
      </div>

      <div class="text-xl font-semibold">
        <slot name="title"></slot>
      </div>

      <div class="mt-4">
        <slot name="message"></slot>
      </div>

      <slot name="button">
        <div class="flex justify-end space-x-3 mt-8">
          <button
            @click="$emit('close')"
            class="itbms-cancel-button bg-gray-300 text-gray-700 py-2 px-4 rounded-xl cursor-pointer"
          >
            Cancel
          </button>
          <button
            @click="$emit('confirm')"
            class="itbms-confirm-button bg-blue-600 text-white py-2 px-4 rounded-xl cursor-pointer"
          >
            Confirm
          </button>
        </div>
      </slot>
    </div>
  </div>
</template>
