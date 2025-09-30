<script setup>
import { ref, reactive, computed } from 'vue'

const emit = defineEmits(['login'])

const form = reactive({
  email: '',
  password: '',
})

const showPassword = ref(false)

// validate
const touched = reactive({
  email: false,
  password: false,
})

function onBlur(field) {
  touched[field] = true
}

const isEmailValid = computed(() => {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return regex.test(form.email)
})

const isFormValid = computed(() => {
  return isEmailValid.value && form.password !== ''
})

const submitForm = () => {
  const payload = {
    ...form,
  }
  emit('login', payload)
}
</script>

<template>
  <div class="min-h-screen bg-[#F6F6F6] py-6 lg:py-10">
    <div
      class="bg-white mt-6 mx-auto rounded-2xl shadow-lg p-10 md:p-10 lg:p-12 w-[90%] md:w-[55%] max-w-md"
    >
      <!-- Title -->
      <div class="space-y-5">
        <h1 class="text-xl md:text-2xl font-semibold text-gray-900">
          ITB MSHOP
        </h1>
        <h2 class="text-2xl md:text-3xl font-light text-gray-800">
          Welcome Back
        </h2>
        <p class="text-[#808080] font-light text-[12px] md:text-sm pr-8">
          Please Enter Your Account Details
        </p>
      </div>

      <form @submit.prevent="submitForm" class="space-y-4 mt-6">
        <!-- Email -->
        <div class="space-y-1">
          <label
            class="text-[#484848] block text-[12px] md:text-sm font-medium"
          >
            Email <span class="text-red-500">*</span>
          </label>
          <div class="relative">
            <span class="absolute inset-y-0 pt-1 left-4.5 flex items-center">
              <svg
                width="17"
                height="13"
                viewBox="0 0 17 13"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M16.4166 1.74996C16.4166 0.879126 15.7041 0.166626 14.8333 0.166626H2.16659C1.29575 0.166626 0.583252 0.879126 0.583252 1.74996V11.25C0.583252 12.1208 1.29575 12.8333 2.16659 12.8333H14.8333C15.7041 12.8333 16.4166 12.1208 16.4166 11.25V1.74996ZM14.8333 1.74996L8.49992 5.70829L2.16659 1.74996H14.8333ZM14.8333 11.25H2.16659V3.33329L8.49992 7.29163L14.8333 3.33329V11.25Z"
                  fill="#C8C8C8"
                />
              </svg>
            </span>
            <input
              v-model="form.email"
              type="email"
              required
              maxlength="50"
              @blur="onBlur('email')"
              class="itbms-email border-[#E4E4E4] mt-1 w-full border rounded-full pr-4 pl-10 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-[12px] md:text-sm placeholder-[#A2A2A2]"
            />
          </div>
        </div>

        <!-- Password -->
        <div class="space-y-1">
          <label
            class="text-[#484848] block text-[12px] md:text-sm font-medium"
          >
            Password <span class="text-red-500">*</span>
          </label>
          <div class="relative">
            <span class="absolute inset-y-0 left-4.5 flex items-center">
              <svg
                width="15"
                height="18"
                viewBox="0 0 15 18"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M3.86566 7.58209V5.00744C3.86566 3.11079 5.40186 1.57458 7.29852 1.57458C9.19517 1.57458 10.7314 3.11079 10.7314 5.00744V7.58209M7.29852 11.8732C7.52613 11.8732 7.74442 11.7827 7.90537 11.6218C8.06631 11.4608 8.15673 11.2426 8.15673 11.0149C8.15673 10.7873 8.06631 10.569 7.90537 10.4081C7.74442 10.2471 7.52613 10.1567 7.29852 10.1567C7.0709 10.1567 6.85261 10.2471 6.69167 10.4081C6.53072 10.569 6.4403 10.7873 6.4403 11.0149C6.4403 11.2426 6.53072 11.4608 6.69167 11.6218C6.85261 11.7827 7.0709 11.8732 7.29852 11.8732ZM7.29852 11.8732V14.4478M2.66416 7.58209H11.9329C12.6881 7.58209 13.306 8.2 13.306 8.95523V14.9627C13.306 16.0956 12.3791 17.0224 11.2463 17.0224H3.35073C2.21789 17.0224 1.29102 16.0956 1.29102 14.9627V8.95523C1.29102 8.2 1.90893 7.58209 2.66416 7.58209Z"
                  stroke="#C8C8C8"
                  stroke-width="1.5"
                  stroke-miterlimit="10"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
            </span>
            <input
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              required
              maxlength="14"
              @blur="onBlur('password')"
              class="itbms-password border-[#E4E4E4] mt-1 w-full border rounded-full pr-4 pl-10 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 text-[12px] md:text-sm placeholder-[#A2A2A2]"
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

          <div class="text-right mt-1">
            <a href="#" class="text-[11px] text-[#0171E3] hover:underline">
              Forgot password?
            </a>
          </div>
        </div>
        <!-- Sign In Button -->
        <button
          type="submit"
          :class="[
            'itbms-signin-button bg-[#0171E3] hover:bg-[#157DE6] active:bg-[#3A98F6] w-full py-2 md:py-3 rounded-full text-white mt-4 cursor-pointer text-[12px] md:text-sm px-4 lg:text-base lg:px-5 lg:py-2  hover:border-[#0171E3]',
            !isFormValid
              ? 'disabled bg-gray-400 hover:cursor-not-allowed hover:bg-gray-400 active:bg-gray-400'
              : '',
          ]"
        >
          Sign In
        </button>

        <p
          class="text-[#808080] font-normal text-[12px] md:text-sm mt-1.5 text-center"
        >
          Don’t have an account yet?
          <router-link
            :to="{ name: 'RegisterForm' }"
            class="text-[#0171E3] ml-2"
            >Sign Up</router-link
          >
        </p>
      </form>
    </div>
  </div>
</template>
