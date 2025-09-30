<script setup>
import { ref } from 'vue'
import {
  addUser,
  userLogin,
  userLogout,
  updateUser,
  getUserProfile,
} from '@/libs/fetchUtils'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import { useAuth } from '@/stores/auth'
import { useEvent } from '@/stores/event'
import router from '@/router'

const BASE_API_URL_auth = import.meta.env.VITE_APP_URL + '/itb-mshop/v2/auth'
const BASE_API_URL_user = import.meta.env.VITE_APP_URL + '/itb-mshop/v2/users'

const auth = useAuth()
const event = useEvent()
const isLoading = ref(false)

const createNewUser = async (formData) => {
  try {
    const res = await addUser(`${BASE_API_URL_auth}/register`, formData)
    if (res.code === 201) {
      event.showNotification(
        'The user account has been successfully registered.',
        'success'
      )
    } else {
      console.error(res.body.error)
      event.showNotification('Failed. Please try again.', 'error')
    }
  } catch (error) {
    console.error(error)
  }
}

const editUser = async ({ id, payload }) => {
  const data = localStorage.getItem('auth')
  const token = JSON.parse(data)
  try {
    const res = await updateUser(
      `${BASE_API_URL_user}/${id}`,
      payload,
      token.access_token
    )
    if (res.code === 200) {
      event.showNotification('Profile data is updated successfully.')
      auth.setUserProfile(res.body)
      router.push('/profile')
    } else {
      console.error(res.body.error)
      event.showNotification('Failed. Please try again.', 'error')
    }
  } catch (error) {
    console.error(error)
    event.showNotification('Something went wrong.', 'error')
  }
}

const login = async (form) => {
  try {
    const res = await userLogin(`${BASE_API_URL_auth}/login`, form)
    if (res.code === 200) {
      auth.setTokens(res.body)

      try {
        const profile = await getUserProfile(
          `${BASE_API_URL_user}/${auth.user.id}`
        )
        // auth.userProfile จะถูกอัปเดตอัตโนมัติจาก getUserProfile
        auth.setUserProfile(profile.body)
      } catch (error) {
        console.error('Failed to fetch user profile:', error)
      }

      event.showNotification(
        `Hi ${auth.user?.nickname}. You are now logged in.`,
        'success'
      )

      if (auth.user.role === 'seller') {
        router.push({ name: 'SaleItemList' })
      } else {
        router.push({ name: 'SaleItemGallery' })
      }
    } else if (res.code === 401 || res.code === 400) {
      console.log(res.body.error)
      event.showNotification('Email or Password is incorrect.', 'error')
    } else if (res.code === 403) {
      console.log(res.body.error)
      event.showNotification(
        'You need to activate your account before signing in.',
        'error'
      )
    } else {
      console.log(res.body.error)
      event.showNotification(
        'There is a problem. Please try again later.',
        'error'
      )
    }
  } catch (error) {
    console.error(error)
  }
}
</script>

<template>
  <LoadingSpinner v-if="isLoading" />
  <router-view
    v-else
    @createNewUser="createNewUser"
    @login="login"
    @editUser="editUser"
  />
</template>

<style scoped></style>
