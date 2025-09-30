import { ref, computed } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'

function parseJwt(token) {
  try {
    return JSON.parse(atob(token.split('.')[1]))
  } catch (e) {
    return null
  }
}

export const useAuth = defineStore('auth', () => {
  const accessToken = ref(null)
  const user = ref(null)
  const userProfile = ref(null)

  const setTokens = (tokens) => {
    accessToken.value = tokens.access_token

    const payload = parseJwt(tokens.access_token)
    if (payload) {
      user.value = {
        id: payload.id,
        email: payload.email,
        nickname: payload.nickname,
        role: payload.role,
      }
    }

    localStorage.setItem(
      'auth',
      JSON.stringify({
        access_token: accessToken.value,
      })
    )
  }

  const setUserProfile = (profile) => {
    userProfile.value = profile
    localStorage.setItem('userProfile', JSON.stringify(profile))
  }

  const loadFromStorage = () => {
    const data = localStorage.getItem('auth')
    if (data) {
      const tokens = JSON.parse(data)
      accessToken.value = tokens.access_token

      const payload = parseJwt(tokens.access_token)
      if (payload) {
        user.value = {
          id: payload.id,
          email: payload.email,
          nickname: payload.nickname,
          role: payload.role,
        }
      }
    }

    const profileData = localStorage.getItem('userProfile')
    if (profileData) {
      userProfile.value = JSON.parse(profileData)
    }
  }

  const clearAuth = () => {
    accessToken.value = null
    user.value = null
    localStorage.removeItem('auth')
    localStorage.removeItem('userProfile')
  }

  const isLoggedIn = computed(() => !!accessToken.value)

  return {
    accessToken,
    user,
    userProfile,
    isLoggedIn,
    setTokens,
    setUserProfile,
    loadFromStorage,
    clearAuth,
  }
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useAuth, import.meta.hot))
}
