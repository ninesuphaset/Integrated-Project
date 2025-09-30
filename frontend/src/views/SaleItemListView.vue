<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useAuth } from '@/stores/auth'
import PopupModal from '@/components/PopupModal.vue'
import Pagination from '@/components/Pagination.vue'

const emit = defineEmits(['deleteSaleItem'])

const auth = useAuth()
const items = ref([])
const loading = ref(true)

const page = ref(Number(sessionStorage.getItem('page') ?? 0))
const pageSize = ref(Number(sessionStorage.getItem('pageSize') ?? 10))
const totalItems = ref(0)

const isPopupOpen = ref(false)
const saleItemId = ref(null)

const sortField = ref('')
const sortDirection = ref('')
const setSort = (field, direction) => {
  sortField.value = field
  sortDirection.value = direction
  fetchItems()
}

const APP_URL = import.meta.env.VITE_APP_URL

const formatPrice = (price) => Number(price).toLocaleString()

const openDeletePopup = (id) => {
  saleItemId.value = id
  isPopupOpen.value = true
}

const confirmDelete = () => {
  emit('deleteSaleItem', saleItemId.value)
  isPopupOpen.value = false
  saleItemId.value = null
}

const fetchItems = async () => {
  if (!auth.isLoggedIn || auth.user?.role !== 'seller') {
    console.log('You are not allowed to view this page.')
    loading.value = false
    return
  }

  loading.value = true
  try {
    let url = `${APP_URL}/itb-mshop/v2/sellers/${auth.user.id}/sale-items?page=${page.value}&size=${pageSize.value}`
    if (sortField.value && sortDirection.value) {
      url += `&sortField=${encodeURIComponent(sortField.value)}&sortDirection=${encodeURIComponent(sortDirection.value)}`
    }

    const res = await fetch(url, {
      headers: { Authorization: `Bearer ${auth.accessToken}` },
    })

    if (!res.ok) throw new Error(`Error ${res.status}`)

    const data = await res.json()
    console.log('Fetched sale items:', data)

    items.value = data.content ?? []
    totalItems.value = data.totalElements ?? data.length

    if (items.value.length === 0 && totalItems.value > 0) {
      page.value = 0
      sessionStorage.setItem('page', 0)
      fetchItems()
    }

  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(fetchItems)

watch(page, (newVal) => {
  sessionStorage.setItem('page', newVal)
  fetchItems()
})

watch(pageSize, (newVal) => {
  sessionStorage.setItem('pageSize', newVal)
  page.value = 0
  sessionStorage.setItem('page', 0)
  fetchItems()
})

const allItems = computed(() => items.value)
const count = computed(() => totalItems.value)
</script>

<template>
  <div>
    <PopupModal
      :show="isPopupOpen"
      @close="isPopupOpen = false"
      @confirm="confirmDelete"
    >
      <template #title>
        <h3 class="itbms-message">Do you want to delete this sale item?</h3>
      </template>
      <template #message>
        <p>This action is permanent and cannot be undone.</p>
      </template>
    </PopupModal>

    <div class="bg-[#F6F6F6] min-h-[80vh] py-8 px-6">
      <div class="bg-white rounded-3xl shadow p-12 mx-auto">
        <div class="flex justify-between mb-3">
          <div class="mb-4">
            <h2 class="text-lg font-semibold mb-3">Sale Item List</h2>
            <p class="text-sm text-[#131927]">{{ count }} Results</p>
          </div>
          <div class="flex items-center justify-end space-x-3 mb-4">
            <!-- Add Item Buttons -->
            <router-link to="/sale-items/add">
              <button
                class="itbms-sale-item-add border text-[#0171E3] px-3 py-1 text-sm rounded hover:bg-[#DBEDFF] cursor-pointer"
              >
                + Add Item
              </button>
            </router-link>
            <router-link :to="{ name: 'BrandListView' }">
              <button
                class="itbms-manage-brand border text-[#0171E3] px-3 py-1 text-sm rounded hover:bg-[#DBEDFF] cursor-pointer"
              >
                Manage brand
              </button>
            </router-link>
          </div>
        </div>

        <div class="flex mt-2 mb-5 justify-start items-center">
          <label for="pageSize" class="text-gray-600 text-[12px] md:text-sm">
            Show
          </label>
          <select
            id="pageSize"
            v-model.number="pageSize"
            class="itbms-page-size focus:outline-none text-[12px] md:text-sm border border-gray-300 rounded px-3 py-1 ml-2"
          >
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="20">20</option>
          </select>
        </div>

        <!-- Table -->
        <div class="rounded-xl overflow-hidden border-t border-[#EBEBEB]">
          <table class="min-w-full text-sm text-center text-gray-700">
            <thead
              class="bg-[#FAFAFA] text-[#696969] border-b border-[#EBEBEB]"
            >
              <tr>
                <th class="px-4 py-4 font-medium">Id</th>
                <th class="px-4 py-4 font-medium">Brand</th>
                <th class="px-4 py-4 font-medium">Model</th>
                <th class="px-4 py-4 font-medium">Ram</th>
                <th class="px-4 py-4 font-medium">Storage</th>
                <th class="px-4 py-4 font-medium">Color</th>
                <th class="px-4 py-4 font-medium">Price</th>
                <th class="px-4 py-4 font-medium">Actions</th>
              </tr>
            </thead>
            <tbody v-if="allItems.length > 0">
              <tr
                v-for="(item, index) in allItems"
                :key="index"
                class="itbms-row border-b border-[#EBEBEB]"
              >
                <td class="itbms-id px-4 py-4">{{ item.id }}</td>
                <td class="itbms-brand px-4 py-4">{{ item.brandName }}</td>
                <td class="itbms-model px-4 py-4">{{ item.model }}</td>
                <td class="itbms-ramGb px-4 py-4">{{ item.ramGb ?? '-' }}</td>
                <td class="itbms-storageGb px-4 py-4">
                  {{ item.storageGb ?? '-' }}
                </td>
                <td class="itbms-color px-4 py-4">{{ item.color ?? '-' }}</td>
                <td class="itbms-price px-4 py-4">
                  {{ formatPrice(item.price) }}
                </td>
                <td
                  class="px-4 py-4 flex justify-center items-center space-x-4"
                >
                  <router-link :to="`/sale-items/${item.id}/edit`">
                    <button class="itbms-edit-button cursor-pointer">
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
                    </button>
                  </router-link>
                  <button
                    class="itbms-delete-button cursor-pointer"
                    @click="openDeletePopup(item.id)"
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
                no sale item
              </td>
            </tr>
          </table>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="mt-20">
      <Pagination
        v-model:page="page"
        v-model:pageSize="pageSize"
        :totalItems="totalItems"
      />
    </div>
  </div>
</template>
