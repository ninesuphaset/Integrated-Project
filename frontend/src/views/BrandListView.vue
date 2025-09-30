<script setup>
import { ref, computed } from 'vue'
import PopupModal from '@/components/PopupModal.vue'
import { getItemById } from '@/libs/fetchUtils'

const props = defineProps({
  brands: {
    type: Array,
    required: true,
  },
})
const count = computed(() => props.brands.length)
const emit = defineEmits(['deleteBrand', 'createBrand'])

const APP_URL = import.meta.env.VITE_APP_URL
const isPopupOpen = ref(false)
const brandId = ref(null)
const brandName = ref('')

const isDeleteAllowed = ref(true)

const openDeletePopup = async (id) => {
  brandId.value = id
  
  try {
    const response = await getItemById(APP_URL + `/itb-mshop/v1/brands/${id}`)
    if (response.code === 200) {
      const brandDetail = response.body
      brandName.value = brandDetail.name
      isDeleteAllowed.value = Number(brandDetail.noOfSaleItems) === 0
      isPopupOpen.value = true
    } else {
      isPopupOpen.value = true
      console.error()
    }
  } catch (error) {
    console.error(error)
  }
}

const confirmDelete = () => {
  emit('deleteBrand', brandId.value)
  isPopupOpen.value = false
  brandId.value = null
  brandName.value = ''
}
</script>

<template>
  <div>
    <PopupModal
      v-if="!isDeleteAllowed"
      :show="isPopupOpen"
      width="480px"
      @close="isPopupOpen = false"
    >
      <template #icon>
        <div class="flex justify-center mb-4">
          <svg
            width="59"
            height="64"
            viewBox="0 0 59 64"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M29.1667 0C45.2754 0 58.3333 13.0579 58.3333 29.1667C58.3333 45.2754 45.2754 58.3333 29.1667 58.3333C13.0579 58.3333 0 45.2754 0 29.1667C0 13.0579 13.0579 0 29.1667 0ZM29.1667 5.83333C22.9783 5.83333 17.0434 8.29166 12.6675 12.6675C8.29166 17.0434 5.83333 22.9783 5.83333 29.1667C5.83333 35.355 8.29166 41.29 12.6675 45.6658C17.0434 50.0417 22.9783 52.5 29.1667 52.5C35.355 52.5 41.29 50.0417 45.6658 45.6658C50.0417 41.29 52.5 35.355 52.5 29.1667C52.5 22.9783 50.0417 17.0434 45.6658 12.6675C41.29 8.29166 35.355 5.83333 29.1667 5.83333ZM29.1667 37.9167C29.9402 37.9167 30.6821 38.224 31.2291 38.7709C31.776 39.3179 32.0833 40.0598 32.0833 40.8333C32.0833 41.6069 31.776 42.3487 31.2291 42.8957C30.6821 43.4427 29.9402 43.75 29.1667 43.75C28.3931 43.75 27.6513 43.4427 27.1043 42.8957C26.5573 42.3487 26.25 41.6069 26.25 40.8333C26.25 40.0598 26.5573 39.3179 27.1043 38.7709C27.6513 38.224 28.3931 37.9167 29.1667 37.9167ZM29.1667 11.6667C29.9402 11.6667 30.6821 11.974 31.2291 12.5209C31.776 13.0679 32.0833 13.8098 32.0833 14.5833V32.0833C32.0833 32.8569 31.776 33.5987 31.2291 34.1457C30.6821 34.6927 29.9402 35 29.1667 35C28.3931 35 27.6513 34.6927 27.1043 34.1457C26.5573 33.5987 26.25 32.8569 26.25 32.0833V14.5833C26.25 13.8098 26.5573 13.0679 27.1043 12.5209C27.6513 11.974 28.3931 11.6667 29.1667 11.6667Z"
              fill="#E84954"
            />
          </svg>
        </div>
      </template>

      <template #title>
        <div class="itbms-message">
          <p class="text-xl font-normal text-center mb-3">Delete <span class="font-medium">{{ brandName }}</span> is not allowed.</p>
          <p class="text-center font-normal text-base text-[#636363]"> There are sale items with {{ brandName }} brand.</p>
        </div>
      </template>

      <template #button>
        <div class="flex justify-center mt-5">
          <button
            @click="isPopupOpen = false"
            class="itbms-cancel-button bg-gray-500 hover:bg-gray-400 text-white py-2 px-5 rounded-xl text-base cursor-pointer transition-all"
          >
            Cancel
          </button>
        </div>
      </template>
    </PopupModal>

    <PopupModal
      v-else
      :show="isPopupOpen"
      @close="isPopupOpen = false"
      @confirm="confirmDelete"
    >
      <template #title>
        <p class="itbms-message font-medium">Do you want to delete
          <span class="font-semibold">{{ brandName }}</span> brand?</p>
      </template>

      <template #message>
        <p>This action is permanent and cannot be undone.</p>
      </template>
    </PopupModal>

    <div class="bg-[#F6F6F6] min-h-screen py-8 px-6">
      <div class="bg-white rounded-3xl w-2/3 shadow p-12 mx-auto">
        <div class="flex justify-between mb-3">
          <div class="mb-4">
            <h2 class="text-lg font-semibold mb-3">Brand List</h2>
            <p class="text-sm text-[#131927]">{{ count }} Results</p>
          </div>
          <div class="mb-4 flex space-x-8">
            <router-link :to="{ name: 'SaleItemList' }">
              <button
                class="border text-[#0171E3] px-3 py-1 text-sm rounded hover:bg-[#DBEDFF] cursor-pointer"
              >
                Sale Item List
              </button>
            </router-link>
            <router-link :to="{ name: 'AddBrand' }">
              <button
                class="itbms-add-button border text-[#0171E3] px-3 py-1 text-sm rounded hover:bg-[#DBEDFF] cursor-pointer"
              >
                + Add Brand
              </button>
            </router-link>
          </div>
        </div>

        <div class="rounded-xl overflow-hidden border-t border-[#EBEBEB]">
          <table class="min-w-full text-sm text-center text-gray-700">
            <thead
              class="bg-[#FAFAFA] text-[#696969] border-b border-[#EBEBEB]"
            >
              <tr>
                <th class="px-4 py-4 font-medium w-1/5">Id</th>
                <th class="px-4 py-4 font-medium w-2/5">Name</th>
                <th class="px-4 py-4 font-medium w-2/5">Actions</th>
              </tr>
            </thead>
            <tbody v-if="props.brands.length > 0">
              <tr
                v-for="(brand, index) in props.brands"
                :key="index"
                class="itbms-row border-b border-[#EBEBEB]"
              >
                <td class="itbms-id px-4 py-4 w-1/5">{{ brand.id }}</td>
                <td class="itbms-name px-4 py-4 w-2/5">{{ brand.name }}</td>
                <td
                  class="px-4 py-4 flex justify-center items-center space-x-4"
                >
                  <router-link
                    :to="`/brands/${brand.id}/edit`"
                    class="itbms-edit-button cursor-pointer"
                  >
                    <svg
                      width="18"
                      height="18"
                      viewBox="0 0 18 18"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path
                        d="M12.304 2.84436L15.156 5.69636M5 5.00036H2C1.73478 5.00036 1.48043 5.10572 1.29289 5.29325C1.10536 5.48079 1 5.73514 1 6.00036V16.0004C1 16.2656 1.10536 16.5199 1.29289 16.7075C1.48043 16.895 1.73478 17.0004 2 17.0004H13C13.2652 17.0004 13.5196 16.895 13.7071 16.7075C13.8946 16.5199 14 16.2656 14 16.0004V11.5004M16.409 1.59036C16.5964 1.77767 16.745 2.00005 16.8464 2.24481C16.9478 2.48958 17 2.75192 17 3.01686C17 3.2818 16.9478 3.54414 16.8464 3.78891C16.745 4.03367 16.5964 4.25605 16.409 4.44336L9.565 11.2874L6 12.0004L6.713 8.43536L13.557 1.59136C13.7442 1.4039 13.9664 1.25517 14.2111 1.1537C14.4558 1.05223 14.7181 1 14.983 1C15.2479 1 15.5102 1.05223 15.7549 1.1537C15.9996 1.25517 16.2218 1.4039 16.409 1.59136V1.59036Z"
                        stroke="#696969"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                    </svg>
                  </router-link>
                  <button
                    @click="openDeletePopup(brand.id)"
                    class="itbms-delete-button cursor-pointer"
                  >
                    <svg
                      width="16"
                      height="16"
                      viewBox="0 0 16 16"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path
                        d="M13.25 1.875V14.125C13.25 14.5625 12.8125 15 12.375 15H8H3.625C3.1875 15 2.75 14.5625 2.75 14.125V1.875"
                        stroke="#E84954"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                      <path
                        d="M1 1.875H15"
                        stroke="#E84954"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                      <path
                        d="M6.25 1H9.75M6.25 5.375V11.5M9.75 5.375V11.5"
                        stroke="#E84954"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                    </svg>
                  </button>
                </td>
              </tr>
            </tbody>
            <tr v-else>
              <td colspan="10" class="py-16 text-center text-gray-400 text-lg">
                no brand
              </td>
            </tr>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
