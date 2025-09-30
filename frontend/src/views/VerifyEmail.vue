<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

const BASE_API_URL = import.meta.env.VITE_APP_URL + '/itb-mshop/v2/auth'

const status = ref("loading");
const route = useRoute();
const router = useRouter();

onMounted(async () => {
  const token = route.query.token;
  if (!token) {
    status.value = "error";
    return;
  }

  try {
    const res = await fetch(
      `${BASE_API_URL}/verify-email?token=${token}`
    );
    if (res.ok) {
      status.value = "success";
    } else {
      status.value = "error";
    }
  } catch (err) {
    status.value = "error";
  }
});

const goHome = () => {
  router.push("/");
};
</script>

<template>
  <div class="flex flex-col h-[400px] md:h-[500px] lg:h-[650px] items-center">
    <div
      v-if="status === 'loading'"
      class="flex flex-col flex-grow items-center justify-center"
    >
      <p class="text-gray-500">Verifying your account...</p>
    </div>
    <div
      v-else-if="status === 'success'"
      class="flex flex-col flex-grow my-16 items-center justify-center text-center px-4"
    >
      <svg
        width="80"
        height="80"
        viewBox="0 0 85 85"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          d="M62.6876 34.3542C63.3327 33.6862 63.6897 32.7916 63.6816 31.8629C63.6736 30.9343 63.3011 30.046 62.6445 29.3894C61.9878 28.7327 61.0995 28.3603 60.1709 28.3522C59.2423 28.3441 58.3476 28.7011 57.6797 29.3462L38.9088 48.1171L27.2213 36.4296C26.5534 35.7844 25.6587 35.4274 24.7301 35.4355C23.8015 35.4436 22.9132 35.8161 22.2566 36.4727C21.5999 37.1294 21.2274 38.0177 21.2194 38.9463C21.2113 39.8749 21.5683 40.7695 22.2134 41.4375L36.3801 55.6042C37.0443 56.2681 37.9449 56.6411 38.8841 56.6411C39.8232 56.6411 40.7238 56.2681 41.388 55.6042L62.638 34.3542H62.6876Z"
          fill="#40B07B"
        />
        <path
          fill-rule="evenodd"
          clip-rule="evenodd"
          d="M85 42.5C85 65.9458 65.9458 85 42.5 85C19.0542 85 0 65.9458 0 42.5C0 19.0542 19.0542 0 42.5 0C65.9458 0 85 19.0542 85 42.5ZM77.9167 42.5C77.9167 62.05 62.05 77.9167 42.5 77.9167C22.95 77.9167 7.08333 62.05 7.08333 42.5C7.08333 22.95 22.95 7.08333 42.5 7.08333C62.05 7.08333 77.9167 22.95 77.9167 42.5Z"
          fill="#40B07B"
        />
      </svg>
      <h2
        class="text-lg md:text-2xl lg:text-3xl font-medium text-gray-400 mb-4 mt-6"
      >
        Account verified.
      </h2>
      <div
        class="text-sm md:text-sm lg:text-base font-light text-gray-400 mb-6 space-y-2"
      >
        <p>Your account has been successfully activated.</p>
      </div>
      <button
        @click="goHome"
        class="px-6 py-2 bg-green-600 text-white rounded-lg shadow hover:bg-green-700"
      >
        Go to Home
      </button>
    </div>

    <div
      v-else
      class="flex flex-col flex-grow my-16 items-center justify-center text-center px-4"
    >
      <svg
        width="80"
        height="80"
        viewBox="0 0 85 85"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          d="M42.5 0C65.9727 0 85 19.0273 85 42.5C85 65.9727 65.9727 85 42.5 85C19.0273 85 0 65.9727 0 42.5C0 19.0273 19.0273 0 42.5 0ZM42.5 8.5C33.4826 8.5 24.8346 12.0821 18.4584 18.4584C12.0821 24.8346 8.5 33.4826 8.5 42.5C8.5 51.5174 12.0821 60.1654 18.4584 66.5416C24.8346 72.9179 33.4826 76.5 42.5 76.5C51.5174 76.5 60.1654 72.9179 66.5416 66.5416C72.9179 60.1654 76.5 51.5174 76.5 42.5C76.5 33.4826 72.9179 24.8346 66.5416 18.4584C60.1654 12.0821 51.5174 8.5 42.5 8.5ZM42.5 55.25C43.6272 55.25 44.7082 55.6978 45.5052 56.4948C46.3022 57.2918 46.75 58.3728 46.75 59.5C46.75 60.6272 46.3022 61.7082 45.5052 62.5052C44.7082 63.3022 43.6272 63.75 42.5 63.75C41.3728 63.75 40.2918 63.3022 39.4948 62.5052C38.6978 61.7082 38.25 60.6272 38.25 59.5C38.25 58.3728 38.6978 57.2918 39.4948 56.4948C40.2918 55.6978 41.3728 55.25 42.5 55.25ZM42.5 17C43.6272 17 44.7082 17.4478 45.5052 18.2448C46.3022 19.0418 46.75 20.1228 46.75 21.25V46.75C46.75 47.8772 46.3022 48.9582 45.5052 49.7552C44.7082 50.5522 43.6272 51 42.5 51C41.3728 51 40.2918 50.5522 39.4948 49.7552C38.6978 48.9582 38.25 47.8772 38.25 46.75V21.25C38.25 20.1228 38.6978 19.0418 39.4948 18.2448C40.2918 17.4478 41.3728 17 42.5 17Z"
          fill="#E84954"
        />
      </svg>
      <h2
        class="text-lg md:text-2xl lg:text-3xl font-medium text-gray-400 mb-4 mt-6"
      >
        Verification failed.
      </h2>
      <div
        class="text-sm md:text-sm lg:text-base font-light text-gray-400 mb-6 space-y-1"
      >
        <p>An error occurred, or the verification link has expired.</p>
        <p>Please request a new verification email.</p>
      </div>
      <button
        @click="goHome"
        class="px-6 py-3 bg-gray-600 text-white rounded-lg shadow hover:bg-gray-700"
      >
        Go to Home
      </button>
    </div>
  </div>
</template>
