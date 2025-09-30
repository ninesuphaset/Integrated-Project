<script setup>
import { computed } from 'vue'
import { useEvent } from '@/stores/event'

const event = useEvent()

const closeNotification = () => {
  event.clearNotification()
}

const colors = {
  error: {
    background: '#FFE4E4',
    border: '#F8CFCB',
    text: '#8E313A'
  },
  warning: {
    background: '#FFF7DF',
    border: '#FBEBD0',
    text: '#CDAE59'
  },
  default: {
    background: '#DCF8E5',
    border: '#C4E4D7',
    text: '#288B5C'
  }
}

const backgroundColor = computed(() => colors[event.type]?.background || colors.default.background)
const borderColor = computed(() => colors[event.type]?.border || colors.default.border)
const textColor = computed(() => colors[event.type]?.text || colors.default.text)
</script>

<template>
  <transition name="slide-fade">
    <div
      v-if="event.visible"
      class="fixed lg:bottom-4 lg:right-4 z-50 rounded p-4 flex justify-between items-center shadow-md lg:min-w-[430px] min-w-[350px] bottom-2 right-2"
      :style="{
        backgroundColor: backgroundColor,
        borderColor: borderColor,
        borderWidth: '2px',
        color: textColor,
      }"
    >
      <span class="itbms-message">{{ event.message }}</span>
      <button @click="closeNotification" class="cursor-pointer ml-4">
        <svg
          width="15"
          height="15"
          viewBox="0 0 15 15"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M10.2126 5.82625C10.2726 5.77027 10.3211 5.70302 10.3551 5.62833C10.3892 5.55364 10.4082 5.47297 10.411 5.39094C10.4139 5.3089 10.4006 5.22711 10.3719 5.15022C10.3431 5.07333 10.2995 5.00285 10.2435 4.94281C10.1876 4.88277 10.1203 4.83435 10.0456 4.8003C9.97092 4.76625 9.89026 4.74725 9.80822 4.74438C9.72619 4.74151 9.64439 4.75482 9.5675 4.78356C9.49061 4.8123 9.42014 4.8559 9.3601 4.91188L7.53135 6.61688L5.82635 4.7875C5.71226 4.67065 5.55698 4.6031 5.39372 4.59929C5.23046 4.59548 5.07219 4.65573 4.95279 4.76713C4.83338 4.87853 4.76232 5.03224 4.75481 5.19537C4.7473 5.3585 4.80393 5.5181 4.9126 5.64L6.6176 7.46875L4.78822 9.17375C4.72606 9.22916 4.6756 9.29643 4.6398 9.37161C4.60401 9.44679 4.58359 9.52837 4.57976 9.61155C4.57593 9.69474 4.58876 9.77784 4.6175 9.856C4.64624 9.93415 4.6903 10.0058 4.74711 10.0667C4.80391 10.1275 4.87231 10.1765 4.94829 10.2106C5.02426 10.2446 5.10628 10.2632 5.18953 10.2651C5.27278 10.2671 5.35557 10.2524 5.43305 10.2219C5.51054 10.1914 5.58114 10.1457 5.64072 10.0875L7.46947 8.38313L9.17447 10.2119C9.22952 10.2752 9.29676 10.3268 9.37217 10.3635C9.44758 10.4003 9.52962 10.4215 9.6134 10.4259C9.69718 10.4303 9.78099 10.4178 9.85983 10.3891C9.93866 10.3604 10.0109 10.3161 10.0723 10.2589C10.1336 10.2017 10.1829 10.1327 10.217 10.0561C10.2511 9.97944 10.2695 9.89672 10.271 9.81283C10.2725 9.72895 10.2571 9.64563 10.2257 9.56783C10.1943 9.49003 10.1475 9.41935 10.0882 9.36L8.38385 7.53125L10.2126 5.82625Z"
            fill="currentColor"
          />
          <path
            fill-rule="evenodd"
            clip-rule="evenodd"
            d="M0.625 7.5C0.625 3.70312 3.70312 0.625 7.5 0.625C11.2969 0.625 14.375 3.70312 14.375 7.5C14.375 11.2969 11.2969 14.375 7.5 14.375C3.70312 14.375 0.625 11.2969 0.625 7.5ZM7.5 13.125C6.76131 13.125 6.02986 12.9795 5.34741 12.6968C4.66495 12.4141 4.04485 11.9998 3.52252 11.4775C3.00019 10.9551 2.58586 10.335 2.30318 9.65259C2.02049 8.97014 1.875 8.23868 1.875 7.5C1.875 6.76131 2.02049 6.02986 2.30318 5.34741C2.58586 4.66495 3.00019 4.04485 3.52252 3.52252C4.04485 3.00019 4.66495 2.58586 5.34741 2.30318C6.02986 2.02049 6.76131 1.875 7.5 1.875C8.99184 1.875 10.4226 2.46763 11.4775 3.52252C12.5324 4.57742 13.125 6.00816 13.125 7.5C13.125 8.99184 12.5324 10.4226 11.4775 11.4775C10.4226 12.5324 8.99184 13.125 7.5 13.125Z"
            fill="currentColor"
          />
        </svg>
      </button>
    </div>
  </transition>
</template>

<style scoped>
.slide-fade-enter-active {
  transition: all 0.5s ease;
}
.slide-fade-leave-active {
  transition: all 0.5s ease;
}
.slide-fade-enter-from {
  transform: translateX(100%);
  opacity: 0;
}
.slide-fade-enter-to {
  transform: translateX(0);
  opacity: 1;
}
.slide-fade-leave-from {
  transform: translateX(0);
  opacity: 1;
}
.slide-fade-leave-to {
  transform: translateX(100%);
  opacity: 0;
}
</style>
