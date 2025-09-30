<script setup>
defineProps({
  backgroundImage: {
    type: String,
    required: true,
  },
})
</script>

<template>
  <div
    class="h-[180px] md:h-[280px] lg:h-[330px] rounded-xl w-full p-4 md:p-6 lg:p-10 bg-cover bg-center flex flex-col"
    :style="`background-image: url(${backgroundImage})`"
  >
    <div class="flex flex-col h-full">
      <div class="space-y-3 md:space-y-6 lg:space-y-10 flex-grow">
        <div>
          <p
            class="bg-[#DAECFB] w-fit px-3 md:px-3 lg:px-4 py-1 rounded-2xl hidden md:block md:text-sm lg:text-base text-[#0171E3]"
          >
            Special Price
          </p>
        </div>

        <div
          class="pt-2 text-lg md:text-2xl lg:text-3xl font-semibold text-white lg:pr-48 lg:leading-10"
        >
          <slot name="heading"></slot>
        </div>
      </div>

      <router-link :to="{ name: 'SaleItemGallery' }">
        <button
          class="cursor-pointer flex items-center gap-2 bg-white border border-gray-300 text-gray-700 text-[13px] md:text-base lg:text-base font-medium px-2 md:px-3 lg:px-4 py-1 md:py-1.5 lg:py-2 rounded-full hover:bg-gray-100 transition"
        >
          Buy Now
          <div>
            <svg
              width="20"
              height="20"
              viewBox="0 0 15 15"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <g clip-path="url(#clip0_102_2561)">
                <path
                  d="M12.854 7.56802C12.854 7.54002 12.8423 7.51435 12.819 7.49102C12.7957 7.46768 12.77 7.45602 12.742 7.45602L10.404 5.11802C10.3107 5.03402 10.2033 4.99202 10.082 4.99202C9.96066 4.99202 9.85333 5.03402 9.76 5.11802C9.66666 5.20202 9.62233 5.30935 9.627 5.44002C9.63166 5.57068 9.676 5.67802 9.76 5.76202L11.342 7.34402H0.771998C0.650664 7.34402 0.543331 7.38602 0.449998 7.47002C0.356664 7.55402 0.309998 7.66835 0.309998 7.81302C0.309998 7.95768 0.351998 8.06968 0.435998 8.14902C0.519998 8.22835 0.631998 8.26802 0.771998 8.26802H11.398L9.816 9.85002C9.732 9.93402 9.69 10.039 9.69 10.165C9.69 10.291 9.732 10.4007 9.816 10.494C9.844 10.522 9.886 10.5477 9.942 10.571C9.998 10.5944 10.054 10.606 10.11 10.606C10.166 10.606 10.222 10.5944 10.278 10.571C10.334 10.5477 10.376 10.522 10.404 10.494L12.742 8.15602C12.7607 8.13735 12.7793 8.11402 12.798 8.08602C12.8167 8.05802 12.8353 8.04402 12.854 8.04402C12.882 7.92268 12.896 7.82935 12.896 7.76402C12.896 7.69868 12.882 7.63335 12.854 7.56802Z"
                  fill="#212529"
                />
              </g>
            </svg>
          </div>
        </button>
      </router-link>
    </div>
  </div>
</template>
