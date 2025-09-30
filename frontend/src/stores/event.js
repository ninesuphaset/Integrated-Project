import { ref } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'

export const useEvent = defineStore('event', () => {
  const message = ref('')
  const type = ref('success')
  const visible = ref(false)

  const showNotification = (msg, notifType = 'success') => {
    message.value = msg
    type.value = notifType
    visible.value = true
    setTimeout(() => {
      visible.value = false
    }, 3000)
  }

  const clearNotification = () => {
    visible.value = false
  }

  return {
    message,
    type, 
    visible,
    showNotification,
    clearNotification,
  }
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useEvent, import.meta.hot))
}
