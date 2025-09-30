<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuth } from '@/stores/auth'
import Breadcrumb from '@/components/Breadcrumb.vue'

const emit = defineEmits(['editUser'])
const auth = useAuth()
const route = useRoute()
const router = useRouter()

const isEditMode = computed(() => route.path === '/profile/edit')

const maskNumber = (num) => {
  if (!num) return ''
  const str = num.toString().replace(/-/g, '')
  const len = str.length
  if (len <= 4) return 'x'.repeat(len)
  const last = 'x'
  const showStart = len - 4
  const masked = 'x'.repeat(showStart) + str.slice(showStart, len - 1) + last
  return masked
}

// Edit Profile
const form = reactive({
  nickName: '',
  fullName: '',
})

watch(
  () => auth.userProfile,
  (profile) => {
    if (profile) {
      form.nickName = profile.nickName || ''
      form.fullName = profile.fullName || ''
    }
  },
  { immediate: true }
)

const formChanged = computed(() => {
  if (!auth.userProfile) return false
  return (
    form.fullName !== (auth.userProfile.fullName || '') ||
    form.nickName !== (auth.userProfile.nickName || '')
  )
})

const isFormValid = computed(() => {
  return formChanged.value && form.fullName.trim() && form.nickName.trim()
})

const handleCancel = () => {
  if (!formChanged.value) {
    router.back()
  } else {
    form.fullName = auth.userProfile.fullName || ''
    form.nickName = auth.userProfile.nickName || ''
    router.push('/profile')
  }
}

const submitForm = async () => {
  const payload = {
    fullName: form.fullName.trim(),
    nickName: form.nickName.trim(),
  }
  emit('editUser', { id: auth.user?.id, payload })
}
</script>

<template>
  <div class="min-h-screen bg-[#F6F6F6] pt-4 pb-8 px-8">
    <div class="mx-8 mt-4">
      <Breadcrumb
        v-if="auth.userProfile.userType === 'seller'"
        :breadcrumbs="[
          {
            label: 'Home',
            to: { name: 'SaleItemList' },
            className: 'itbms-home',
          },
          { label: 'Profile' },
        ]"
      />
      <Breadcrumb
        v-else
        :breadcrumbs="[
          {
            label: 'Home',
            to: { name: 'SaleItemGallery' },
            className: 'itbms-home',
          },
          { label: 'Profile' },
        ]"
      />
    </div>
    <!-- Title -->
    <div class="flex items-center mx-8 space-x-4">
      <h1 class="text-2xl font-semibold mb-6 pt-8">My Profile</h1>
      <span class="bg-[#EDEDED] w-full h-0.5 flex-1"></span>
    </div>

    <!-- Profile Card -->
    <div
      class="bg-white rounded-xl shadow-sm mx-6 px-12 py-8 mb-6 flex items-start justify-between"
    >
      <div class="flex space-x-12">
        <img
          src="https://img.freepik.com/premium-vector/cute-avatar-akita-head-simple-cartoon-vector-illustration-dog-breeds-nature-concept-icon-isolated_772770-320.jpg"
          alt="Profile"
          class="w-24 h-24 rounded-full"
        />
        <div>
          <h2 class="text-lg font-semibold">{{ auth.userProfile.nickName }}</h2>
          <p class="text-[#AAAAAA]">{{ auth.userProfile.email }}</p>
        </div>
      </div>
      <router-link v-if="!isEditMode" to="/profile/edit">
        <button
          class="itbms-profile-button bg-[#0171E3] hover:bg-blue-700 text-sm text-white px-4 py-2 rounded-lg flex items-center space-x-2 cursor-pointer"
        >
          <span>Edit Profile</span>
          <svg
            width="18"
            height="18"
            viewBox="0 0 18 18"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M11.25 4.50002L13.5 6.75002M9.75 15H15.75M3.75 12L3 15L6 14.25L14.6895 5.56052C14.9707 5.27923 15.1287 4.89776 15.1287 4.50002C15.1287 4.10227 14.9707 3.72081 14.6895 3.43952L14.5605 3.31052C14.2792 3.02931 13.8977 2.87134 13.5 2.87134C13.1023 2.87134 12.7208 3.02931 12.4395 3.31052L3.75 12Z"
              stroke="white"
              stroke-width="1.5"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </button>
      </router-link>
    </div>

    <!-- Personal Info -->
    <component
      :is="isEditMode ? 'form' : 'div'"
      class="bg-white rounded-xl shadow-sm p-12 mb-6 mx-6"
      v-on:submit.prevent="isEditMode ? submitForm : null"
    >
      <h2 class="text-xl font-medium text-[#AAAAAA]">Personal Information</h2>
      <hr class="mt-4 mb-6 border border-[#EDEDED]" />
      <div class="space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-3 text-sm">
          <div class="space-y-2">
            <p class="text-[#868686]">Nickname</p>
            <input
              v-if="isEditMode"
              v-model="form.nickName"
              type="text"
              class="itbms-nickname flex-1 border border-[#E4E4E4] py-1.5 pl-4 rounded-lg w-3/4"
            />
            <p v-else class="itbms-nickname font-medium">
              {{ auth.userProfile.nickName }}
            </p>
          </div>
          <div class="space-y-2">
            <p class="text-[#868686]">Email</p>
            <input
              v-if="isEditMode"
              type="text"
              class="itbms-email flex-1 border border-[#E4E4E4] bg-[#E4E4E4] py-1.5 pl-4 rounded-lg w-3/4"
              :value="auth.userProfile.email"
              disabled
            />
            <p v-else class="itbms-email font-medium">
              {{ auth.userProfile.email }}
            </p>
          </div>
          <div class="space-y-2">
            <p class="text-[#868686]">Fullname</p>
            <input
              v-if="isEditMode"
              v-model="form.fullName"
              type="text"
              class="itbms-fullname flex-1 border border-[#E4E4E4] py-1.5 pl-4 rounded-lg w-3/4"
            />
            <p v-else class="itbms-fullname font-medium">
              {{ auth.userProfile.fullName }}
            </p>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 text-sm">
          <div class="space-y-2">
            <p class="text-[#868686]">Type</p>
            <input
              v-if="isEditMode"
              type="text"
              class="itbms-type flex-1 border border-[#E4E4E4] bg-[#E4E4E4] py-1.5 pl-4 rounded-lg w-3/4"
              :value="auth.userProfile.userType"
              disabled
            />
            <p v-else class="itbms-type font-medium">
              {{
                auth.userProfile?.userType?.charAt(0).toUpperCase() +
                auth.userProfile?.userType?.slice(1)
              }}
            </p>
          </div>
          <div v-if="auth.userProfile.userType === 'seller'" class="space-y-2">
            <p class="text-[#868686]">Phone number</p>
            <input
              v-if="isEditMode"
              type="text"
              class="itbms-mobile flex-1 border border-[#E4E4E4] bg-[#E4E4E4] py-1.5 pl-4 rounded-lg w-3/4"
              :value="maskNumber(auth.userProfile.phoneNumber)"
              disabled
            />
            <p v-else class="itbms-mobile font-medium">
              {{ maskNumber(auth.userProfile.phoneNumber) }}
            </p>
          </div>
        </div>
      </div>

      <div v-if="auth.userProfile.userType === 'seller'" class="mt-12">
        <!-- Bank Info -->
        <h2 class="text-xl font-medium text-[#AAAAAA]">Bank Info</h2>
        <hr class="mt-4 mb-6 border border-[#EDEDED]" />
        <div class="grid grid-cols-1 md:grid-cols-3 text-sm">
          <div class="space-y-2">
            <p class="text-[#868686]">Bank name</p>
            <input
              v-if="isEditMode"
              type="text"
              class="itbms-bankName flex-1 border border-[#E4E4E4] bg-[#E4E4E4] py-1.5 pl-4 rounded-lg w-3/4"
              :value="auth.userProfile.bankName"
              disabled
            />
            <p v-else class="itbms-bankName font-medium">
              {{ auth.userProfile.bankName }}
            </p>
          </div>
          <div class="space-y-2">
            <p class="text-[#868686]">Bank account no</p>
            <input
              v-if="isEditMode"
              type="text"
              class="itbms-bankAccount flex-1 border border-[#E4E4E4] bg-[#E4E4E4] py-1.5 pl-4 rounded-lg w-3/4"
              :value="maskNumber(auth.userProfile.bankAccount)"
              disabled
            />
            <p v-else class="itbms-bankAccount font-medium">
              {{ maskNumber(auth.userProfile.bankAccount) }}
            </p>
          </div>
        </div>
      </div>
      <div v-if="isEditMode" class="flex justify-end gap-2 lg:gap-6 mt-8">
        <button
          type="button"
          @click="handleCancel"
          class="itbms-cancel-button text-[#999999] border border-[#E4E4E4] text-sm px-2 py-1 lg:text-base lg:px-4 lg:py-1 rounded cursor-pointer hover:border-[#999999] hover:bg-[#F0F0F0]"
        >
          Cancel
        </button>
        <button
          type="submit"
          :disabled="!isFormValid"
          @click="submitForm"
          :class="[
            'itbms-save-button bg-blue-600 text-white text-sm px-4 py-1 lg:text-base lg:px-5 lg:py-1 rounded cursor-pointer hover:border-[#0171E3] hover:bg-[#157DE6]',
            !isFormValid
              ? 'bg-gray-400 hover:cursor-not-allowed hover:bg-gray-400'
              : '',
          ]"
        >
          Save
        </button>
      </div>
    </component>
  </div>
</template>

<style scoped></style>
