<script setup>
import RectangleBanner from '@/components/RectangleBanner.vue'
</script>

<template>
  <div class="flex flex-col space-y-6 md:space-y-10 lg:space-y-14">
    <div
      class="h-[220px] md:h-[300px] lg:h-[500px] bg-[url('/banner/herobanner.png')] bg-[#F5F5F7] bg-contain bg-center bg-no-repeat flex justify-center text-center py-8 md:py-10 lg:py-16"
    >
      <div>
        <h1 class="text-[26px] md:text-3xl lg:text-5xl font-semibold text-gray-900 mb-3 md:mb-2 lg:mb-4">ITB MShop</h1>
        <p class="hidden md:block px-12 text-sm md:text-base lg:text-xl text-gray-700 md:mb-4 lg:mb-6">
          Get the best quality products at the lowest prices
        </p>

        <div class="space-x-8">
          <router-link :to="{ name: 'SaleItemGallery' }">
            <button
              class="itbms-shopnow bg-[#0171E3] hover:bg-[#157DE6] cursor-pointer text-white text-[12px] md:text-sm lg:text-base font-medium py-1.5 md:py-2 lg:py-3 px-3 md:px-5 lg:px-7 rounded-full transition duration-300"
            >
              SHOP NOW
            </button>
          </router-link>
          <router-link :to="{ name: 'SaleItemList' }">
            <button
              class="itbms-seller bg-[#0171E3] hover:bg-[#157DE6] cursor-pointer text-white text-[12px] md:text-sm lg:text-base font-medium py-1.5 md:py-2 lg:py-3 px-3 md:px-5 lg:px-11 rounded-full transition duration-300"
            >
              SELLER
            </button>
          </router-link>
        </div>
      </div>
    </div>
    <div
      class="h-[60px] md:h-[80px] lg:h-[100px] mx-6 md:mx-8 lg:mx-12 rounded-md flex flex-col pl-4 md:pl-8 lg:pl-10 justify-center bg-[#F5F5F7]"
    >
      <p class="text-base md:text-xl lg:text-2xl font-semibold">Up to 30% Off – Limited Time Offer!</p>
      <p class="text-[12px] md:text-sm lg:text-base text-[#6B7280]">
        Great Deals on Top Brands, Only at ITB MShop
      </p>
    </div>
    <div>
      <div class="mx-6 md:mx-8 lg:mx-12 md:flex lg:flex md:space-x-2 lg:space-x-3 space-y-10">
        <RectangleBanner backgroundImage="banner/dealbanner1.png">
          <template #heading>Best-Selling Phones from Trusted Brands</template>
        </RectangleBanner>
        <RectangleBanner backgroundImage="banner/dealbanner2.png">
          <template #heading
            >Compare & Save – All Your Favorite Devices in One Place</template
          >
        </RectangleBanner>
      </div>
    </div>
  </div>
</template>
