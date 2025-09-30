<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

const emit = defineEmits(['createNewUser'])
const router = useRouter()

const userType = ref('BUYER')
const showPassword = ref(false)

const form = reactive({
  userType: '',
  email: '',
  password: '',
  nickName: '',
  fullName: '',
  phoneNumber: '',
  bankAccount: '',
  bankName: '',
  idCardNumber: '',
})

// validate form
const touched = reactive({
  email: false,
  password: false,
  nickName: false,
  fullName: false,
  phoneNumber: false,
  bankAccount: false,
  bankName: false,
  idCardNumber: false,
})

function onBlur(field) {
  touched[field] = true
}

const isInputLengthValid = computed(() => ({
  email: form.email.length <= 50,
  fullName: form.fullName.length >= 4 && form.fullName.length <= 40,
  password: form.password.length >= 8 && form.password.length <= 14,
}))

const isPasswordValid = computed(() => {
  const pwd = form.password
  const hasLower = /[a-z]/.test(pwd)
  const hasUpper = /[A-Z]/.test(pwd)
  const hasNumber = /[0-9]/.test(pwd)
  const hasSpecial = /[^A-Za-z0-9]/.test(pwd)
  const isLengthValid = isInputLengthValid.value.password

  return isLengthValid && hasLower && hasUpper && hasNumber && hasSpecial
})

// ตรวจสอบรูปแบบ email
const isEmailFormatValid = computed(() => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(form.email)
})

const isBuyerFormValid = computed(() => {
  return (
    isInputLengthValid.value.email &&
    isEmailFormatValid.value &&
    isInputLengthValid.value.fullName &&
    isPasswordValid.value &&
    form.nickName !== '' &&
    form.email !== ''
  )
})

const isSellerFormValid = computed(() => {
  return (
    isInputLengthValid.value.email &&
    isEmailFormatValid.value &&
    isInputLengthValid.value.fullName &&
    isPasswordValid.value &&
    form.nickName !== '' &&
    form.email !== '' &&
    form.phoneNumber !== '' &&
    form.bankAccount !== '' &&
    form.bankName !== '' &&
    form.idCardNumber !== ''
  )
})

const idCardImageFront = ref(null)
const idCardImageBack = ref(null)

const frontPreview = computed(() =>
  idCardImageFront.value ? URL.createObjectURL(idCardImageFront.value) : null
)

const backPreview = computed(() =>
  idCardImageBack.value ? URL.createObjectURL(idCardImageBack.value) : null
)

function handleFileChange(event, type) {
  const file = event.target.files[0]
  if (!file) return
  if (type === 'front') {
    idCardImageFront.value = file
  } else {
    idCardImageBack.value = file
  }
}

const submitSeller = async () => {
  const payload = {
    ...form,
    userType: 'SELLER',
    idCardImageFront: idCardImageFront.value, // เพิ่มไฟล์ด้านหน้า
    idCardImageBack: idCardImageBack.value, // เพิ่มไฟล์ด้านหลัง
  }
  emit('createNewUser', payload)
  router.back()
}

const submitBuyer = async () => {
  const payload = {
    ...form,
    userType: 'BUYER',
  }
  emit('createNewUser', payload)
  router.back()
}
</script>

<template>
  <div class="mx-auto bg-[#F6F6F6] py-6 lg:py-8">
    <div
      class="relative bg-[#FFFFFF] rounded-full text-[12px] md:text-sm w-[220px] md:w-[300px] lg:w-[420px] flex justify-between mx-auto mb-6 overflow-hidden"
    >
      <div
        class="absolute top-0 left-0 h-full w-1/2 bg-[#0171E3] rounded-full transition-transform duration-300 ease-in-out"
        :style="{
          transform:
            userType === 'BUYER' ? 'translateX(0)' : 'translateX(100%)',
        }"
      ></div>

      <button
        class="itbms-account-type relative z-10 w-1/2 py-2 rounded-full transition-colors duration-300 cursor-pointer"
        :class="userType === 'BUYER' ? 'text-white' : 'text-[#6B6B6B]'"
        @click="userType = 'BUYER'"
      >
        Buyer
      </button>
      <button
        class="itbms-account-type relative z-10 w-1/2 py-2 rounded-full transition-colors duration-300 cursor-pointer"
        :class="userType === 'SELLER' ? 'text-white' : 'text-[#6B6B6B]'"
        @click="userType = 'SELLER'"
      >
        Seller
      </button>
    </div>

    <!-- Buyer form -->
    <div v-if="userType === 'BUYER'">
      <div
        class="bg-white rounded-2xl shadow px-2 py-10 md:px-8 md:py-14 lg:px-8 lg:py-12 w-[90%] md:w-[70%] lg:w-[40%] mx-auto"
      >
        <div class="px-10 space-y-5">
          <h1 class="font-semibold text-xl md:text-2xl">ITB MSHOP</h1>
          <p class="text-2xl md:text-3xl font-light">Welcome to ITB MSHOP</p>
          <p class="text-[#808080] font-light text-[12px] md:text-sm pr-8">
            Create an account and discover top-quality products just for you!
          </p>
          <form @submit.prevent="submitBuyer" class="space-y-4">
            <!-- nickName -->
            <div class="space-y-1">
              <label
                class="text-[#484848] block text-[12px] md:text-sm font-medium"
                >Nickname <span class="text-red-500">*</span></label
              >
              <input
                v-model="form.nickName"
                type="text"
                required
                @blur="
                  onBlur('nickName'), (form.nickName = form.nickName.trim())
                "
                class="itbms-nickName border-[#E4E4E4] mt-1 w-full border rounded-full px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-[12px] md:text-sm placeholder-[#A2A2A2]"
              />
              <div class="flex justify-end">
                <p
                  v-if="touched.nickName && !form.nickName"
                  class="text-red-600 font-light text-[12px] mt-1"
                >
                  Nickname is required.
                </p>
              </div>
            </div>

            <!-- Email -->
            <div class="space-y-1">
              <label
                class="text-[#484848] block text-[12px] md:text-sm font-medium"
                >Email <span class="text-red-500">*</span></label
              >
              <input
                v-model="form.email"
                type="text"
                required
                @blur=";(form.email = form.email.trim()), onBlur('email')"
                class="itbms-email border-[#E4E4E4] mt-1 w-full border rounded-full px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-[12px] md:text-sm placeholder-[#A2A2A2]"
              />

              <div class="flex justify-end">
                <p
                  v-if="touched.email && !form.email"
                  class="text-red-600 font-light text-[12px] mt-1"
                >
                  Email is required.
                </p>
                <p
                  v-else-if="touched.email && form.email && !isEmailFormatValid"
                  class="text-red-600 font-light text-[12px] mt-1"
                >
                  Please enter a valid email address.
                </p>
                <p
                  v-if="!isInputLengthValid.email"
                  class="text-red-600 font-light text-[12px] mt-1"
                >
                  Email must not exceed 50 characters
                </p>
              </div>
            </div>

            <!-- Password -->
            <div class="space-y-1">
              <label
                class="text-[#484848] block text-[12px] md:text-sm font-medium"
                >Password <span class="text-red-500">*</span></label
              >
              <div class="relative">
                <input
                  v-model="form.password"
                  :type="showPassword ? 'text' : 'password'"
                  required
                  @blur="
                    ;(form.password = form.password.trim()), onBlur('password')
                  "
                  class="itbms-password border-[#E4E4E4] mt-1 w-full border rounded-full px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-[12px] md:text-sm placeholder-[#A2A2A2]"
                />
                <button
                  type="button"
                  class="absolute inset-y-6 right-4 flex items-center text-gray-400 hover:text-gray-600"
                  @click="showPassword = !showPassword"
                >
                  <svg
                    v-if="!showPassword"
                    width="22"
                    height="22"
                    viewBox="0 0 22 22"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      d="M13.6059 10.8847C13.6059 11.6064 13.3192 12.2985 12.8089 12.8088C12.2986 13.3192 11.6064 13.6059 10.8847 13.6059C10.163 13.6059 9.4709 13.3192 8.96059 12.8088C8.45027 12.2985 8.16357 11.6064 8.16357 10.8847C8.16357 10.163 8.45027 9.47084 8.96059 8.96053C9.4709 8.45021 10.163 8.16351 10.8847 8.16351C11.6064 8.16351 12.2986 8.45021 12.8089 8.96053C13.3192 9.47084 13.6059 10.163 13.6059 10.8847Z"
                      fill="#C8C8C8"
                    />
                    <path
                      d="M19.859 10.4792C17.9015 6.56346 14.4257 4.53528 10.8845 4.53528C7.34339 4.53528 3.86755 6.56346 1.91012 10.4792C1.8472 10.6051 1.81445 10.7439 1.81445 10.8847C1.81445 11.0254 1.8472 11.1642 1.91012 11.2901C3.86755 15.2059 7.34339 17.2341 10.8845 17.2341C14.4257 17.2341 17.9015 15.2059 19.859 11.2901C19.9219 11.1642 19.9546 11.0254 19.9546 10.8847C19.9546 10.7439 19.9219 10.6051 19.859 10.4792ZM10.8845 15.42C8.19149 15.42 5.44038 13.9505 3.746 10.8847C5.44038 7.81882 8.19058 6.34939 10.8845 6.34939C13.5785 6.34939 16.3287 7.81882 18.0231 10.8847C16.3287 13.9505 13.5776 15.42 10.8845 15.42Z"
                      fill="#C8C8C8"
                    />
                  </svg>

                  <svg
                    v-else
                    width="20"
                    height="20"
                    viewBox="0 0 20 20"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      d="M1.6493 4.34564L2.70479 3.2984L16.4921 17.0857L15.4448 18.1412L12.9051 15.6014C11.9568 15.9147 10.9508 16.0797 9.89528 16.0797C5.77229 16.0797 2.25126 13.5152 0.824707 9.89518C1.39368 8.44389 2.30074 7.16576 3.45517 6.15151L1.6493 4.34564ZM9.89528 7.42139C10.5514 7.42139 11.1806 7.68202 11.6445 8.14594C12.1084 8.60987 12.3691 9.23909 12.3691 9.89518C12.3695 10.176 12.3221 10.4549 12.2289 10.7198L9.07068 7.56157C9.3356 7.46837 9.61445 7.42097 9.89528 7.42139ZM9.89528 3.7107C14.0183 3.7107 17.5393 6.2752 18.9658 9.89518C18.2925 11.6045 17.1489 13.0883 15.6675 14.1748L14.4965 12.9957C15.6369 12.2069 16.5566 11.1396 17.1682 9.89518C16.5017 8.53448 15.4667 7.3881 14.181 6.58636C12.8953 5.78462 11.4105 5.3597 9.89528 5.35989C8.99647 5.35989 8.11415 5.50832 7.28955 5.77219L6.01967 4.51056C7.20709 3.99931 8.5182 3.7107 9.89528 3.7107ZM2.62233 9.89518C3.28889 11.2559 4.32385 12.4023 5.60954 13.204C6.89524 14.0057 8.38009 14.4307 9.89528 14.4305C10.4642 14.4305 11.025 14.3727 11.5445 14.2573L9.66439 12.369C9.0906 12.3075 8.55516 12.0514 8.14711 11.6433C7.73906 11.2353 7.48299 10.6999 7.42148 10.1261L4.61785 7.31419C3.8015 8.0151 3.11709 8.88917 2.62233 9.89518Z"
                      fill="#C8C8C8"
                    />
                  </svg>
                </button>
              </div>
              <div class="flex justify-end">
                <p
                  v-if="touched.password && !isPasswordValid"
                  class="text-red-600 font-light text-[12px] mt-1"
                >
                  Password must be 8–14 characters, including uppercase,
                  lowercase, number, and special character.
                </p>
              </div>
            </div>

            <!-- fullName -->
            <div class="space-y-1">
              <label
                class="text-[#484848] block text-[12px] md:text-sm font-medium"
                >Fullname <span class="text-red-500">*</span></label
              >
              <input
                v-model="form.fullName"
                type="text"
                required
                @blur="
                  onBlur('fullName'), (form.fullName = form.fullName.trim())
                "
                class="itbms-fullName border-[#E4E4E4] mt-1 w-full border rounded-full px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-[12px] md:text-sm placeholder-[#A2A2A2]"
              />
              <div class="flex justify-end">
                <p
                  v-if="touched.fullName && !isInputLengthValid.fullName"
                  class="text-red-600 font-light text-[12px] mt-1"
                >
                  Fullname should be 4 to 40 characters long.
                </p>
              </div>
            </div>

            <!-- Sign Up Button -->
            <button
              type="submit"
              :class="[
                'itbms-submit-button bg-[#0171E3] hover:bg-[#157DE6] active:bg-[#3A98F6] w-full py-3 rounded-full text-white mt-4 cursor-pointer text-[12px] md:text-sm px-4  lg:text-base lg:px-5 lg:py-2  hover:border-[#0171E3]',
                !isBuyerFormValid
                  ? 'bg-gray-400 hover:cursor-not-allowed hover:bg-gray-400 active:bg-gray-400'
                  : '',
              ]"
            >
              Submit
            </button>

            <!-- Cancel Button -->
            <button
              type="button"
              @click="router.back()"
              class="border w-full py-3 rounded-full mt-0.5 text-[12px] md:text-sm cursor-pointer border-[#E4E4E4] text-[#A2A2A2]"
            >
              Cancel
            </button>

            <!-- Login -->
            <p
              class="text-[#808080] font-normal text-[12px] md:text-sm mt-1.5 text-center"
            >
              Already have an account?
              <router-link class="text-[#0171E3] ml-2" :to="{ name: 'SignIn' }">
                Login
              </router-link>
            </p>
          </form>
        </div>
      </div>
    </div>

    <!-- Seller form -->
    <div
      v-if="userType === 'SELLER'"
      class="bg-white rounded-2xl shadow px-10 py-10 md:px-12 md:py-12 lg:p-12 w-[90%] md:w-[85%] lg:w-[55%] mx-auto"
    >
      <div class="space-y-5">
        <h1 class="font-semibold text-xl md:text-2xl">ITB MSHOP</h1>
        <p class="text-2xl md:text-3xl font-light">Seller Registration</p>
        <p class="text-[#808080] font-light text-[12px] md:text-sm mb-8">
          Join us and reach customers with your best products.
        </p>

        <form @submit.prevent="submitSeller">
          <!-- Account Info  -->
          <div>
            <h6 class="text-[#AAAAAA] text-sm font-medium mb-3">
              Account Info
            </h6>

            <div class="grid md:grid-cols-2 gap-x-8 gap-y-6">
              <div class="flex flex-col">
                <!-- nickName -->
                <label
                  class="text-[#484848] mb-1 font-medium text-[12px] md:text-sm"
                >
                  Nickname <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="form.nickName"
                  type="text"
                  required
                  @blur="
                    onBlur('nickName'), (form.nickName = form.nickName.trim())
                  "
                  class="itbms-nickName text-[12px] md:text-sm placeholder-[#A2A2A2] border px-3 py-2 border-[#E4E4E4] focus:outline-none focus:ring-2 focus:ring-blue-400 rounded-full"
                />
                <div class="flex justify-end">
                  <p
                    v-if="touched.nickName && !form.nickName"
                    class="text-red-600 font-light text-[12px] mt-1"
                  >
                    Nickname is required.
                  </p>
                </div>
              </div>

              <!-- Email -->
              <div class="flex flex-col">
                <label
                  class="text-[#484848] mb-1 font-medium text-[12px] md:text-sm"
                >
                  Email <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="form.email"
                  type="text"
                  required
                  @blur="onBlur('email'), (form.email = form.email.trim())"
                  class="itbms-email text-[12px] md:text-sm placeholder-[#A2A2A2] border px-3 py-2 border-[#E4E4E4] focus:outline-none focus:ring-2 focus:ring-blue-400 rounded-full"
                />
                <div class="flex justify-end">
                  <p
                    v-if="touched.email && !form.email"
                    class="text-red-600 font-light text-[12px] mt-1"
                  >
                    Email is required.
                  </p>
                  <p
                    v-else-if="
                      touched.email && form.email && !isEmailFormatValid
                    "
                    class="text-red-600 font-light text-[12px] mt-1"
                  >
                    Please enter a valid email address.
                  </p>
                  <p
                    v-if="!isInputLengthValid.email"
                    class="text-red-600 font-light text-[12px] mt-1"
                  >
                    Email must not exceed 50 characters
                  </p>
                </div>
              </div>

              <!-- Password -->
              <div class="flex flex-col">
                <label
                  class="text-[#484848] mb-1 font-medium text-[12px] md:text-sm"
                >
                  Password <span class="text-red-500">*</span>
                </label>
                <div class="relative">
                  <input
                    v-model="form.password"
                    required
                    @blur="
                      ;(form.password = form.password.trim()),
                        onBlur('password')
                    "
                    :type="showPassword ? 'text' : 'password'"
                    class="itbms-password w-full text-[12px] md:text-sm placeholder-[#A2A2A2] border px-3 py-2 pr-10 border-[#E4E4E4] focus:outline-none focus:ring-2 focus:ring-blue-400 rounded-full"
                  />
                  <button
                    type="button"
                    class="absolute inset-y-0 right-3 flex items-center text-gray-400 hover:text-gray-600"
                    @click="showPassword = !showPassword"
                  >
                    <svg
                      v-if="!showPassword"
                      width="22"
                      height="22"
                      viewBox="0 0 22 22"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path
                        d="M13.6059 10.8847C13.6059 11.6064 13.3192 12.2985 12.8089 12.8088C12.2986 13.3192 11.6064 13.6059 10.8847 13.6059C10.163 13.6059 9.4709 13.3192 8.96059 12.8088C8.45027 12.2985 8.16357 11.6064 8.16357 10.8847C8.16357 10.163 8.45027 9.47084 8.96059 8.96053C9.4709 8.45021 10.163 8.16351 10.8847 8.16351C11.6064 8.16351 12.2986 8.45021 12.8089 8.96053C13.3192 9.47084 13.6059 10.163 13.6059 10.8847Z"
                        fill="#C8C8C8"
                      />
                      <path
                        d="M19.859 10.4792C17.9015 6.56346 14.4257 4.53528 10.8845 4.53528C7.34339 4.53528 3.86755 6.56346 1.91012 10.4792C1.8472 10.6051 1.81445 10.7439 1.81445 10.8847C1.81445 11.0254 1.8472 11.1642 1.91012 11.2901C3.86755 15.2059 7.34339 17.2341 10.8845 17.2341C14.4257 17.2341 17.9015 15.2059 19.859 11.2901C19.9219 11.1642 19.9546 11.0254 19.9546 10.8847C19.9546 10.7439 19.9219 10.6051 19.859 10.4792ZM10.8845 15.42C8.19149 15.42 5.44038 13.9505 3.746 10.8847C5.44038 7.81882 8.19058 6.34939 10.8845 6.34939C13.5785 6.34939 16.3287 7.81882 18.0231 10.8847C16.3287 13.9505 13.5776 15.42 10.8845 15.42Z"
                        fill="#C8C8C8"
                      />
                    </svg>

                    <svg
                      v-else
                      width="20"
                      height="20"
                      viewBox="0 0 20 20"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path
                        d="M1.6493 4.34564L2.70479 3.2984L16.4921 17.0857L15.4448 18.1412L12.9051 15.6014C11.9568 15.9147 10.9508 16.0797 9.89528 16.0797C5.77229 16.0797 2.25126 13.5152 0.824707 9.89518C1.39368 8.44389 2.30074 7.16576 3.45517 6.15151L1.6493 4.34564ZM9.89528 7.42139C10.5514 7.42139 11.1806 7.68202 11.6445 8.14594C12.1084 8.60987 12.3691 9.23909 12.3691 9.89518C12.3695 10.176 12.3221 10.4549 12.2289 10.7198L9.07068 7.56157C9.3356 7.46837 9.61445 7.42097 9.89528 7.42139ZM9.89528 3.7107C14.0183 3.7107 17.5393 6.2752 18.9658 9.89518C18.2925 11.6045 17.1489 13.0883 15.6675 14.1748L14.4965 12.9957C15.6369 12.2069 16.5566 11.1396 17.1682 9.89518C16.5017 8.53448 15.4667 7.3881 14.181 6.58636C12.8953 5.78462 11.4105 5.3597 9.89528 5.35989C8.99647 5.35989 8.11415 5.50832 7.28955 5.77219L6.01967 4.51056C7.20709 3.99931 8.5182 3.7107 9.89528 3.7107ZM2.62233 9.89518C3.28889 11.2559 4.32385 12.4023 5.60954 13.204C6.89524 14.0057 8.38009 14.4307 9.89528 14.4305C10.4642 14.4305 11.025 14.3727 11.5445 14.2573L9.66439 12.369C9.0906 12.3075 8.55516 12.0514 8.14711 11.6433C7.73906 11.2353 7.48299 10.6999 7.42148 10.1261L4.61785 7.31419C3.8015 8.0151 3.11709 8.88917 2.62233 9.89518Z"
                        fill="#C8C8C8"
                      />
                    </svg>
                  </button>
                </div>
                <div class="flex justify-end">
                  <p
                    v-if="touched.password && !isPasswordValid"
                    class="text-red-600 font-light text-[12px] mt-1"
                  >
                    Password must be 8–14 characters, including uppercase,
                    lowercase, number, and special character.
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- Personal Info -->
          <div class="mt-8">
            <p class="text-[#AAAAAA] text-sm font-medium mb-3">Personal Info</p>
            <div class="grid md:grid-cols-2 gap-x-8 gap-y-6">
              <!-- fullName -->
              <div class="flex flex-col">
                <label
                  class="text-[#484848] mb-1 font-medium text-[12px] md:text-sm"
                >
                  Fullname <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="form.fullName"
                  type="text"
                  required
                  @blur="
                    onBlur('fullName'), (form.fullName = form.fullName.trim())
                  "
                  class="itbms-fullName text-[12px] md:text-sm placeholder-[#A2A2A2] border px-3 py-2 border-[#E4E4E4] focus:outline-none focus:ring-2 focus:ring-blue-400 rounded-full"
                />
                <div class="flex justify-end">
                  <p
                    v-if="touched.fullName && !isInputLengthValid.fullName"
                    class="text-red-600 font-light text-[12px] mt-1"
                  >
                    Fullname should be 4 to 40 characters long.
                  </p>
                </div>
              </div>

              <!-- Phone Number -->
              <div class="flex flex-col">
                <label
                  class="text-[#484848] mb-1 font-medium text-[12px] md:text-sm"
                >
                  Phone Number <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="form.phoneNumber"
                  type="text"
                  required
                  @blur="
                    onBlur('phoneNumber'),
                      (form.phoneNumber = form.phoneNumber.trim())
                  "
                  class="itbms-mobile text-[12px] md:text-sm placeholder-[#A2A2A2] border px-3 py-2 border-[#E4E4E4] focus:outline-none focus:ring-2 focus:ring-blue-400 rounded-full"
                />
                <div class="flex justify-end">
                  <p
                    v-if="touched.phoneNumber && !form.phoneNumber"
                    class="text-red-600 font-light text-[12px] mt-1"
                  >
                    Phone number is required.
                  </p>
                </div>
              </div>

              <!-- National Card No -->
              <div class="flex flex-col">
                <label
                  class="text-[#484848] mb-1 font-medium text-[12px] md:text-sm"
                >
                  National Card No <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="form.idCardNumber"
                  type="text"
                  required
                  @blur="
                    onBlur('idCardNumber'),
                      (form.idCardNumber = form.idCardNumber.trim())
                  "
                  class="itbms-card-no text-[12px] md:text-sm placeholder-[#A2A2A2] border px-3 py-2 border-[#E4E4E4] focus:outline-none focus:ring-2 focus:ring-blue-400 rounded-full"
                />
                <div class="flex justify-end">
                  <p
                    v-if="touched.idCardNumber && !form.idCardNumber"
                    class="text-red-600 font-light text-[12px] mt-1"
                  >
                    National Card No is required.
                  </p>
                </div>
              </div>

              <!-- ID Photo -->
              <div class="flex flex-col">
                <label
                  class="text-[#484848] mb-1 font-medium text-[12px] md:text-sm"
                >
                  National ID Photo (front and back)
                </label>
                <div class="flex gap-4 mt-1">
                  <!-- Front -->
                  <label
                    class="w-28 h-28 border-2 border-dashed border-[#E4E4E4] rounded-lg flex items-center justify-center cursor-pointer hover:bg-gray-50 overflow-hidden"
                  >
                    <input
                      type="file"
                      accept="image/*"
                      class="hidden"
                      @change="(e) => handleFileChange(e, 'front')"
                    />
                    <template v-if="frontPreview">
                      <img
                        :src="frontPreview"
                        alt="Front ID"
                        class="itbms-card-photo-front object-cover w-full h-full"
                      />
                    </template>
                    <span
                      v-else
                      class="itbms-card-photo-front text-gray-400 text-[12px] md:text-sm"
                      >Front side</span
                    >
                  </label>

                  <!-- Back -->
                  <label
                    class="w-28 h-28 border-2 border-dashed border-[#E4E4E4] rounded-lg flex items-center justify-center cursor-pointer hover:bg-gray-50 overflow-hidden"
                  >
                    <input
                      type="file"
                      accept="image/*"
                      class="hidden"
                      @change="(e) => handleFileChange(e, 'back')"
                    />
                    <template v-if="backPreview">
                      <img
                        :src="backPreview"
                        alt="Back ID"
                        class="itbms-card-photo-back object-cover w-full h-full"
                      />
                    </template>
                    <span
                      v-else
                      class="itbms-card-photo-back text-gray-400 text-[12px] md:text-sm"
                      >Back side</span
                    >
                  </label>
                </div>
              </div>
            </div>
          </div>

          <!-- Bank Info -->
          <div class="mt-8">
            <p class="text-[#AAAAAA] text-sm font-medium mb-3">Bank Info</p>

            <div class="grid md:grid-cols-2 gap-x-8 gap-y-6">
              <!-- Bank Name -->
              <div class="flex flex-col">
                <label
                  class="text-[#484848] mb-1 font-medium text-[12px] md:text-sm"
                >
                  Bank Name <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="form.bankName"
                  type="text"
                  required
                  @blur="
                    onBlur('bankName'), (form.bankName = form.bankName.trim())
                  "
                  class="itbms-bank-name text-[12px] md:text-sm placeholder-[#A2A2A2] border px-3 py-2 border-[#E4E4E4] focus:outline-none focus:ring-2 focus:ring-blue-400 rounded-full"
                />
                <div class="flex justify-end">
                  <p
                    v-if="touched.bankName && !form.bankName"
                    class="text-red-600 font-light text-[12px] mt-1"
                  >
                    Bank Name is required.
                  </p>
                </div>
              </div>

              <!-- Bank Account No -->
              <div class="flex flex-col">
                <label
                  class="text-[#484848] mb-1 font-medium text-[12px] md:text-sm"
                >
                  Bank Account No. <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="form.bankAccount"
                  type="text"
                  required
                  @blur="
                    onBlur('bankAccount'),
                      (form.bankAccount = form.bankAccount.trim())
                  "
                  class="itbms-bank-account-no text-[12px] md:text-sm placeholder-[#A2A2A2] border px-3 py-2 border-[#E4E4E4] focus:outline-none focus:ring-2 focus:ring-blue-400 rounded-full"
                />
                <div class="flex justify-end">
                  <p
                    v-if="touched.bankAccount && !form.bankAccount"
                    class="text-red-600 font-light text-[12px] mt-1"
                  >
                    Bank Account No. is required.
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div
            class="flex flex-col-reverse items-center md:flex-row md:justify-between md:items-end"
          >
            <p class="text-[12px] md:text-sm text-[#808080] mt-6">
              Already have an account?
              <router-link
                class="text-[#0171E3] font-medium ml-4"
                :to="{ name: 'SignIn' }"
                >Login</router-link
              >
            </p>
            <!-- Buttons -->
            <div class="md:col-span-2 flex justify-end space-x-3 mt-10">
              <button
                @click="router.back()"
                type="button"
                class="itbms-cancel-button border py-1.5 px-4 rounded-full border-[#E4E4E4] text-[#A2A2A2] hover:bg-gray-100 cursor-pointer text-[12px] md:text-sm"
              >
                Cancel
              </button>
              <button
                type="submit"
                :class="[
                  'itbms-submit-button bg-[#0171E3] hover:bg-[#157DE6] active:bg-[#3A98F6] text-white px-6 py-1.5 rounded-full transition cursor-pointer text-[12px] md:text-sm',
                  !isSellerFormValid
                    ? 'bg-gray-400 hover:cursor-not-allowed hover:bg-gray-400 active:bg-gray-400'
                    : '',
                ]"
              >
                Submit
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
