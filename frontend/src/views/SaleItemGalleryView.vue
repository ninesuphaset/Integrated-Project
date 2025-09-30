<script setup>
import { onMounted, ref, computed, watch } from 'vue'
import SaleItemCard from '@/components/SaleItemCard.vue'
import FilterBox from '@/components/FilterBox.vue'
import { getItems } from '@/libs/fetchUtils'
import Pagination from '@/components/Pagination.vue'

const props = defineProps({
  allItems: {
    type: Array,
    required: true,
  },
})

const items = ref([])
const brands = ref([])
const APP_URL = import.meta.env.VITE_APP_URL

const sortField = ref(sessionStorage.getItem('sortField') ?? '')
const sortDirection = ref(sessionStorage.getItem('sortDirection') ?? '')
const page = ref(Number(sessionStorage.getItem('page') ?? 0))
const pageSize = ref(Number(sessionStorage.getItem('pageSize') ?? 10))

const selectedBrands = ref(
  JSON.parse(sessionStorage.getItem('selectedBrands') ?? '[]')
)

const searchKeyword = ref(sessionStorage.getItem('keyword') ?? '')

const selectedPriceRange = ref(
  JSON.parse(sessionStorage.getItem('selectedPriceRange') ?? 'null')
)

const selectedStorageRange = ref(
  JSON.parse(sessionStorage.getItem('selectedStorageRange') ?? '[]')
)

const totalItems = ref(0)
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))

const countResult = computed(() => items.value.length)
const countAll = computed(() => props.allItems.length)

const fetchSaleItems = async () => {
  try {
    const params = new URLSearchParams({
      page: page.value,
      size: pageSize.value,
      sortField: sortField.value,
      sortDirection: sortDirection.value,
    })

    selectedBrands.value.forEach((b) => params.append('filterBrands', b.name))

    if (selectedPriceRange.value) {
      params.append('filterPriceMin', selectedPriceRange.value[0])
      params.append('filterPriceMax', selectedPriceRange.value[1])
    }

    selectedStorageRange.value.forEach((b) => {
      const StorageRange = storageOptions[b]
      params.append('filterStorage', StorageRange)
    })

    if (searchKeyword.value !== null && searchKeyword.value !== '') {
      params.append('keyword', searchKeyword.value)
    }

    const url = `${APP_URL}/itb-mshop/v2/sale-items?${params.toString()}`

    const res = await getItems(url)
    if (res.code === 200) {
      const content = res.body.content
      const total = res.body.totalElements

      if (content.length === 0 && page.value > 0) {
        page.value -= 1
        return await fetchSaleItems()
      }

      items.value = content
      totalItems.value = total

      // pageStart.value = Math.max(page.value - visiblePageCount + 1, 0)
    } else {
      console.error(res.body.error)
      items.value = []
    }
  } catch (error) {
    console.error(error)
    items.value = []
  }
}

onMounted(async () => {
  await fetchSaleItems()
  const brandRes = await getItems(APP_URL + '/itb-mshop/v1/brands')
  if (brandRes.code === 200) {
    brands.value = brandRes.body.sort((a, b) => {
      return a.name.localeCompare(b.name, 'en')
    })
  } else {
    console.error(brandRes.body.error)
    brands.value = []
  }
})

let prevBrands = JSON.stringify(selectedBrands.value)
let prevPageSize = pageSize.value

watch(
  [
    sortField,
    sortDirection,
    page,
    pageSize,
    selectedBrands,
    searchKeyword,
    selectedPriceRange,
    selectedStorageRange,
  ],
  async () => {
    const currentBrands = JSON.stringify(selectedBrands.value)

    if (currentBrands !== prevBrands) {
      page.value = 0
      prevBrands = currentBrands
    }

    if (pageSize.value !== prevPageSize) {
      page.value = 0
      prevPageSize = pageSize.value
    }

    sessionStorage.setItem('sortField', sortField.value)
    sessionStorage.setItem('sortDirection', sortDirection.value)
    sessionStorage.setItem('page', page.value.toString())
    sessionStorage.setItem('pageSize', pageSize.value.toString())
    sessionStorage.setItem(
      'selectedBrands',
      JSON.stringify(selectedBrands.value)
    )
    sessionStorage.setItem(
      'selectedPriceRange',
      JSON.stringify(selectedPriceRange.value)
    )
    sessionStorage.setItem('keyword', searchKeyword.value)
    sessionStorage.setItem('words', words.value)
    sessionStorage.setItem(
      'selectedStorageRange',
      JSON.stringify(selectedStorageRange.value)
    )

    await fetchSaleItems()
  }
)

const setSort = async (field, direction) => {
  sortDirection.value = direction
  sortField.value = field
}

// Filter
const priceOptions = {
  '0 - 5,000 Bath': [0, 5000],
  '5,001 - 10,000 Bath': [5001, 10000],
  '10,001 - 20,000 Bath': [10001, 20000],
  '20,001 - 30,000 Bath': [20001, 30000],
  '30,001 - 40,000 Bath': [30001, 40000],
  '40,001 - 50,000 Bath': [40001, 50000],
  '50,001 + Bath': [50001, 99999999],
}
const storageOptions = {
  '32Gb': 32,
  '64Gb': 64,
  '128Gb': 128,
  '256Gb': 256,
  '512Gb': 512,
  '1Tb+': 1,
  'Not specified': -1,
}

const activeFilter = ref(null)

const toggleFilter = (type) => {
  activeFilter.value = activeFilter.value === type ? null : type
}

const isAnyActive = computed(() => !!activeFilter.value)

// clear filter
const removeBrand = (brand) => {
  selectedBrands.value = selectedBrands.value.filter((b) => b.id !== brand.id)
}

const removePrice = () => {
  selectedPriceRange.value = null
}

const removeStorage = (storage) => {
  selectedStorageRange.value = selectedStorageRange.value.filter(
    (s) => s !== storage
  )
}

// clear all filter
const clearFilter = () => {
  activeFilter.value = null
  selectedBrands.value = []
  selectedPriceRange.value = null
  selectedStorageRange.value = []
}

const getPriceLabel = (range) => {
  const presetLabel = Object.entries(priceOptions).find(
    ([, value]) => value[0] === range[0] && value[1] === range[1]
  )?.[0]

  if (presetLabel) return presetLabel

  if (range[0] === range[1]) return `${range[0].toLocaleString()} Bath` // only min value

  return `${range[0]?.toLocaleString() || 0} - ${
    range[1]?.toLocaleString() || '∞'
  } Bath`
}

// custom filter price
const customMinPrice = ref(null)
const customMaxPrice = ref(null)

watch(selectedPriceRange, (range) => {
  const isPreset = Object.values(priceOptions).some(
    (preset) => preset[0] === range?.[0] && preset[1] === range?.[1]
  )

  if (isPreset) {
    customMinPrice.value = null
    customMaxPrice.value = null
  }
})

const applyCustomPrice = () => {
  const minRaw = customMinPrice.value
  const maxRaw = customMaxPrice.value

  const hasMin =
    minRaw !== null && minRaw !== undefined && String(minRaw).trim() !== ''
  const hasMax =
    maxRaw !== null && maxRaw !== undefined && String(maxRaw).trim() !== ''

  const min = hasMin ? Number(minRaw) : null
  const max = hasMax ? Number(maxRaw) : null

  if (hasMin && hasMax && min <= max) {
    // ใส่ทั้ง min และ max
    selectedPriceRange.value = [min, max]
  } else if (hasMin && !hasMax) {
    // ใส่แค่ min
    selectedPriceRange.value = [min, min]
  } else {
    // ไม่ filter
    selectedPriceRange.value = null
  }
}

// Search
const words = ref(sessionStorage.getItem('words') ?? '')

const clearKeywordFilter = () => {
  searchKeyword.value = ''
  words.value = ''
}

const onSearch = async () => {
  page.value = 0
  searchKeyword.value = words.value.trim()
  sessionStorage.setItem('keyword', searchKeyword.value)
  await fetchSaleItems()
}

const showClearButton = computed(() => {
  return words.value.trim() && words.value.trim() === searchKeyword.value.trim()
})
</script>

<template>
  <div>
    <div
      class="mx-6 md:mx-14 lg:mx-18 mt-10 md:mt-12 lg:mt-16 border-b-2 border-[#F4F3F3] mb-3 md:mb-4 lg:mb-6"
    >
      <div class="flex items-center justify-between">
        <div
          class="flex flex-col md:grid md:grid-cols-2 items-center text-xl md:text-2xl lg:text-4xl"
        >
          <h1 class="w-full">Mobile Phone</h1>
          <form
            class="flex items-center md:w-full md:ml-4 lg:ml-12 mt-4 md:mt-0"
            @submit.prevent="onSearch"
          >
            <!-- Search Box -->
            <div class="relative flex items-center w-full">
              <!-- Search Input -->
              <input
                type="text"
                v-model="words"
                class="itbms-search-text bg-white border border-[#F4F3F3] text-gray-900 text-[12px] md:text-sm rounded-md w-full py-1 lg:py-1.5 pl-4 pr-10 focus:outline-none focus:ring-2 focus:ring-blue-400"
                placeholder="Search..."
              />

              <button
                type="button"
                class="absolute right-2 text-[#9D9D9D] cursor-pointer pr-2 hover:text-[#BCBCBC] active:text-[#CDCDCD]"
                @click="showClearButton ? clearKeywordFilter() : onSearch()"
              >
                <!-- Search Icon -->
                <svg
                  v-if="!showClearButton"
                  class="w-4 h-4"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 20 20"
                >
                  <path
                    stroke="currentColor"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z"
                  />
                </svg>
                <!-- Clear Icon -->
                <svg
                  v-else
                  class="w-4 h-4 itbms-search-clear-button"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 20 20"
                >
                  <path
                    stroke="currentColor"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M6 6l8 8M6 14L14 6"
                  />
                </svg>
              </button>
            </div>
          </form>
        </div>
      </div>

      <div class="xl:flex mt-5 md:mt-10 justify-between lg:items-start">
        <div>
          <div
            class="flex items-center pr-4 rounded-full border-2 border-[#F4F3F3] transition-all relative w-[390px] md:w-[585px] lg:w-[780px]"
            :class="isAnyActive ? 'bg-[#F4F3F3]' : 'bg-white border-[#F4F3F3]'"
          >
            <FilterBox
              customClass="itbms-brand-filter"
              label="Brand"
              placeholder="Filter by brand(s)"
              :active="activeFilter === 'brand'"
              @toggle="toggleFilter('brand')"
            >
              <div v-if="activeFilter === 'brand'" class="dropdown-content">
                <label
                  v-for="option in brands"
                  :key="option.id"
                  class="flex gap-3 items-center hover:bg-[#ECECEC] py-1 px-0.5 cursor-pointer rounded-lg text-[12px] lg:text-base"
                >
                  <input
                    type="checkbox"
                    :value="option"
                    v-model="selectedBrands"
                  />
                  {{ option.name }}
                </label>
              </div>
            </FilterBox>
            <div v-if="activeFilter === 'storage' || !activeFilter">
              <svg
                width="1"
                height="46"
                viewBox="0 0 1 46"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <line x1="0.5" x2="0.5" y2="46" stroke="#D8D8D8" />
              </svg>
            </div>

            <FilterBox
              customClass="itbms-price-filter"
              label="Price"
              placeholder="Price Range"
              :active="activeFilter === 'price'"
              @toggle="toggleFilter('price')"
            >
              <div v-if="activeFilter === 'price'" class="dropdown-content">
                <label
                  v-for="(range, label) in priceOptions"
                  :key="label"
                  class="flex gap-3 items-center hover:bg-[#ECECEC] py-1 px-0.5 cursor-pointer rounded-lg text-[12px] lg:text-base"
                >
                  <input
                    type="radio"
                    :value="range"
                    v-model="selectedPriceRange"
                    name="price-filter"
                    class="accent-black"
                  />
                  {{ label }}
                </label>

                <div class="flex gap-2 items-center mt-4">
                  <input
                    type="number"
                    v-model="customMinPrice"
                    placeholder="Min Price"
                    class="itbms-price-item-min w-20 md:w-25 px-2 py-1 border border-[#F4F3F3] rounded text-[12px] lg:text-sm focus:outline-none focus:ring-1 focus:ring-blue-400"
                  />
                  <span class="text-[12px] lg:text-sm"> - </span>
                  <input
                    type="number"
                    v-model="customMaxPrice"
                    placeholder="Max Price"
                    class="itbms-price-item-max w-20 md:w-25 px-2 py-1 border border-[#F4F3F3] rounded text-[12px] lg:text-sm focus:outline-none focus:ring-1 focus:ring-blue-400"
                  />
                  <span class="text-[12px] lg:text-base hidden md:block"
                    >Baht</span
                  >
                  <button
                    class="px-3 py-1 bg-[#0171E3] hover:bg-[#157DE6] active:bg-[#3A98F6] text-[12px] lg:text-sm text-white rounded-2xl cursor-pointer"
                    @click="applyCustomPrice"
                  >
                    Apply
                  </button>
                </div>
              </div>
            </FilterBox>

            <div v-if="activeFilter === 'brand' || !activeFilter">
              <svg
                width="1"
                height="46"
                viewBox="0 0 1 46"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <line x1="0.5" x2="0.5" y2="46" stroke="#D8D8D8" />
              </svg>
            </div>
            <FilterBox
              customClass="itbms-storage-size-filter"
              label="Storage Size"
              placeholder="Storage Range"
              :active="activeFilter === 'storage'"
              @toggle="toggleFilter('storage')"
            >
              <div v-if="activeFilter === 'storage'" class="dropdown-content">
                <label
                  v-for="(value, option) in storageOptions"
                  :key="option.id"
                  class="flex gap-3 items-center hover:bg-[#ECECEC] py-1 px-0.5 cursor-pointer rounded-lg text-[12px] lg:text-base"
                >
                  <input
                    type="checkbox"
                    :value="option"
                    v-model="selectedStorageRange"
                  />
                  {{ option }}
                </label>
              </div>
            </FilterBox>
            <button
              class="bg-[#0171E3] hover:bg-[#157DE6] active:bg-[#3A98F6] ml-2 md:ml-4 text-white px-2 md:px-4 py-0.5 md:py-1.5 rounded-full cursor-pointer text-sm lg:text-base"
              @click="clearFilter"
            >
              Clear
            </button>
          </div>

          <div
            class="flex justify-between items-start w-[390px] md:w-[480px] lg:w-[700px]"
            :class="
              selectedBrands.length ||
              selectedPriceRange ||
              selectedStorageRange.length
                ? 'mt-6'
                : ''
            "
          >
            <!-- brand filter -->
            <div class="flex gap-2 flex-wrap px-3 pb-3 w-1/3">
              <template v-for="brand in selectedBrands" :key="brand.id">
                <div
                  class="itbms-brand-item bg-[#F4F3F3] px-3 py-1 rounded-full flex items-center gap-2 text-[12px] lg:text-sm max-h-fit"
                >
                  {{ brand.name }}
                  <button
                    class="itbms-brand-item-clear cursor-pointer text-[10px] lg:text-sm"
                    @click="removeBrand(brand)"
                  >
                    ✕
                  </button>
                </div>
              </template>
            </div>

            <!-- price filter tag -->
            <div
              class="flex justify-start gap-2 flex-wrap px-0 lg:px-3 pb-3 w-1/3"
            >
              <div
                v-if="selectedPriceRange"
                class="itbms-price-item bg-[#F4F3F3] px-3 py-1 rounded-full flex items-center gap-2 text-[12px] lg:text-sm max-h-fit"
              >
                {{ getPriceLabel(selectedPriceRange) }}
                <button
                  class="itbms-price-item-clear cursor-pointer text-[10px] lg:text-sm"
                  @click="removePrice"
                >
                  ✕
                </button>
              </div>
            </div>

            <!-- storage filter -->
            <div class="flex gap-2 flex-wrap px-3 pb-3 w-1/3">
              <template v-for="storage in selectedStorageRange" :key="storage">
                <div
                  class="itbms-storage-size-item bg-[#F4F3F3] px-3 py-1 rounded-full flex items-center gap-2 text-[12px] lg:text-sm max-h-fit"
                >
                  {{ storage }}
                  <button
                    class="itbms-storage-size-item-clear cursor-pointer text-[10px] lg:text-sm"
                    @click="removeStorage(storage)"
                  >
                    ✕
                  </button>
                </div>
              </template>
            </div>
          </div>
        </div>

        <!-- sort -->
        <div class="flex mt-4 flex-row lg:flex-row">
          <div
            class="flex-1 flex xl:justify-end items-center ml-2 sm:mr-6 md:mr-8"
          >
            <div class="flex items-center mb-3 sm:mb-0">
              <label
                for="pageSize"
                class="inline-block text-gray-600 text-[12px] md:text-sm"
                >Show</label
              >
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
          </div>

          <div class="flex items-center gap-1 md:gap-2 xl:gap-4">
            <div
              class="itbms-brand-none p-1 cursor-pointer"
              :class="{
                'bg-[#EBE9EA] rounded-lg':
                  sortField === '' && sortDirection === '',
              }"
              @click="setSort('', '')"
            >
              <svg
                class="w-[20px] h-[19px] md:w-[22px] md:h-[21px] lg:w-[25px] lg:h-[24px]"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M20 10.75H4C3.80109 10.75 3.61032 10.671 3.46967 10.5303C3.32902 10.3897 3.25 10.1989 3.25 10C3.25 9.80109 3.32902 9.61032 3.46967 9.46967C3.61032 9.32902 3.80109 9.25 4 9.25H20C20.1989 9.25 20.3897 9.32902 20.5303 9.46967C20.671 9.61032 20.75 9.80109 20.75 10C20.75 10.1989 20.671 10.3897 20.5303 10.5303C20.3897 10.671 20.1989 10.75 20 10.75ZM20 6.75H4C3.80109 6.75 3.61032 6.67098 3.46967 6.53033C3.32902 6.38968 3.25 6.19891 3.25 6C3.25 5.80109 3.32902 5.61032 3.46967 5.46967C3.61032 5.32902 3.80109 5.25 4 5.25H20C20.1989 5.25 20.3897 5.32902 20.5303 5.46967C20.671 5.61032 20.75 5.80109 20.75 6C20.75 6.19891 20.671 6.38968 20.5303 6.53033C20.3897 6.67098 20.1989 6.75 20 6.75ZM20 14.75H4C3.80109 14.75 3.61032 14.671 3.46967 14.5303C3.32902 14.3897 3.25 14.1989 3.25 14C3.25 13.8011 3.32902 13.6103 3.46967 13.4697C3.61032 13.329 3.80109 13.25 4 13.25H20C20.1989 13.25 20.3897 13.329 20.5303 13.4697C20.671 13.6103 20.75 13.8011 20.75 14C20.75 14.1989 20.671 14.3897 20.5303 14.5303C20.3897 14.671 20.1989 14.75 20 14.75ZM20 18.75H4C3.80109 18.75 3.61032 18.671 3.46967 18.5303C3.32902 18.3897 3.25 18.1989 3.25 18C3.25 17.8011 3.32902 17.6103 3.46967 17.4697C3.61032 17.329 3.80109 17.25 4 17.25H20C20.1989 17.25 20.3897 17.329 20.5303 17.4697C20.671 17.6103 20.75 17.8011 20.75 18C20.75 18.1989 20.671 18.3897 20.5303 18.5303C20.3897 18.671 20.1989 18.75 20 18.75Z"
                  fill="black"
                />
              </svg>
            </div>
            <div
              class="itbms-brand-asc p-1 cursor-pointer"
              :class="{
                'bg-[#EBE9EA] rounded-lg':
                  sortField === 'brand.name' && sortDirection === 'asc',
              }"
              @click="setSort('brand.name', 'asc')"
            >
              <svg
                class="w-[20px] h-[19px] md:w-[22px] md:h-[21px] lg:w-[25px] lg:h-[24px]"
                viewBox="0 0 25 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M5.85861 18.7499C5.85861 18.9488 5.7796 19.1396 5.63894 19.2803C5.49829 19.4209 5.30753 19.4999 5.10861 19.4999C4.9097 19.4999 4.71894 19.4209 4.57828 19.2803C4.43763 19.1396 4.35861 18.9488 4.35861 18.7499V5.56043L2.63961 7.28093C2.56988 7.35066 2.4871 7.40598 2.39599 7.44372C2.30488 7.48146 2.20723 7.50088 2.10861 7.50088C2.01 7.50088 1.91235 7.48146 1.82124 7.44372C1.73013 7.40598 1.64735 7.35066 1.57761 7.28093C1.50788 7.2112 1.45257 7.12842 1.41483 7.03731C1.37709 6.9462 1.35767 6.84855 1.35767 6.74993C1.35767 6.65132 1.37709 6.55366 1.41483 6.46256C1.45257 6.37145 1.50788 6.28866 1.57761 6.21893L4.57761 3.22043L4.58811 3.20993C4.72948 3.07371 4.91863 2.99834 5.11495 3.00003C5.31127 3.00171 5.4991 3.0803 5.63811 3.21893L8.63811 6.21893C8.70785 6.28856 8.76318 6.37125 8.80095 6.46227C8.83873 6.55329 8.85821 6.65086 8.85828 6.7494C8.85835 6.84795 8.83901 6.94554 8.80136 7.03661C8.76371 7.12769 8.7085 7.21045 8.63886 7.28018C8.56923 7.34991 8.48654 7.40525 8.39552 7.44302C8.30451 7.4808 8.20694 7.50028 8.10839 7.50035C8.00985 7.50042 7.91225 7.48108 7.82118 7.44343C7.73011 7.40578 7.64735 7.35056 7.57761 7.28093L5.85861 5.56043V18.7499ZM11.1086 5.24993C11.1086 5.05102 11.1876 4.86025 11.3283 4.7196C11.4689 4.57895 11.6597 4.49993 11.8586 4.49993H22.3586C22.5575 4.49993 22.7483 4.57895 22.8889 4.7196C23.0296 4.86025 23.1086 5.05102 23.1086 5.24993C23.1086 5.44884 23.0296 5.63961 22.8889 5.78026C22.7483 5.92091 22.5575 5.99993 22.3586 5.99993H11.8586C11.6597 5.99993 11.4689 5.92091 11.3283 5.78026C11.1876 5.63961 11.1086 5.44884 11.1086 5.24993ZM11.8586 8.99993C11.6597 8.99993 11.4689 9.07895 11.3283 9.2196C11.1876 9.36025 11.1086 9.55102 11.1086 9.74993C11.1086 9.94884 11.1876 10.1396 11.3283 10.2803C11.4689 10.4209 11.6597 10.4999 11.8586 10.4999H19.3586C19.5575 10.4999 19.7483 10.4209 19.8889 10.2803C20.0296 10.1396 20.1086 9.94884 20.1086 9.74993C20.1086 9.55102 20.0296 9.36025 19.8889 9.2196C19.7483 9.07895 19.5575 8.99993 19.3586 8.99993H11.8586ZM11.8586 13.4999C11.6597 13.4999 11.4689 13.5789 11.3283 13.7196C11.1876 13.8603 11.1086 14.051 11.1086 14.2499C11.1086 14.4488 11.1876 14.6396 11.3283 14.7803C11.4689 14.9209 11.6597 14.9999 11.8586 14.9999H16.3586C16.5575 14.9999 16.7483 14.9209 16.8889 14.7803C17.0296 14.6396 17.1086 14.4488 17.1086 14.2499C17.1086 14.051 17.0296 13.8603 16.8889 13.7196C16.7483 13.5789 16.5575 13.4999 16.3586 13.4999H11.8586ZM11.8586 17.9999C11.6597 17.9999 11.4689 18.0789 11.3283 18.2196C11.1876 18.3603 11.1086 18.551 11.1086 18.7499C11.1086 18.9488 11.1876 19.1396 11.3283 19.2803C11.4689 19.4209 11.6597 19.4999 11.8586 19.4999H13.3586C13.5575 19.4999 13.7483 19.4209 13.8889 19.2803C14.0296 19.1396 14.1086 18.9488 14.1086 18.7499C14.1086 18.551 14.0296 18.3603 13.8889 18.2196C13.7483 18.0789 13.5575 17.9999 13.3586 17.9999H11.8586Z"
                  fill="black"
                />
              </svg>
            </div>
            <div
              class="itbms-brand-desc p-1 cursor-pointer"
              :class="{
                'bg-[#EBE9EA] rounded-lg':
                  sortField === 'brand.name' && sortDirection === 'desc',
              }"
              @click="setSort('brand.name', 'desc')"
            >
              <svg
                class="w-[20px] h-[19px] md:w-[22px] md:h-[21px] lg:w-[25px] lg:h-[24px]"
                viewBox="0 0 25 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M5.33689 3.75C5.33689 3.55109 5.25787 3.36032 5.11722 3.21967C4.97656 3.07902 4.7858 3 4.58689 3C4.38797 3 4.19721 3.07902 4.05656 3.21967C3.9159 3.36032 3.83689 3.55109 3.83689 3.75V16.9395L2.11788 15.219C1.97706 15.0782 1.78605 14.9991 1.58688 14.9991C1.38772 14.9991 1.19671 15.0782 1.05588 15.219C0.915055 15.3598 0.835938 15.5508 0.835938 15.75C0.835937 15.9492 0.915055 16.1402 1.05588 16.281L4.05589 19.2795L4.06639 19.29C4.2072 19.4273 4.39657 19.5034 4.59324 19.5017C4.78991 19.5 4.97795 19.4207 5.11639 19.281L8.11639 16.281C8.18612 16.2114 8.24145 16.1287 8.27923 16.0377C8.317 15.9466 8.33648 15.8491 8.33655 15.7505C8.33662 15.652 8.31728 15.5544 8.27963 15.4633C8.24199 15.3722 8.18677 15.2895 8.11714 15.2197C8.0475 15.15 7.96482 15.0947 7.8738 15.0569C7.78278 15.0191 7.68521 14.9997 7.58667 14.9996C7.48812 14.9995 7.39053 15.0189 7.29945 15.0565C7.20838 15.0941 7.12562 15.1494 7.05589 15.219L5.33689 16.9395V3.75ZM10.5869 5.25C10.5869 5.05109 10.6659 4.86032 10.8066 4.71967C10.9472 4.57902 11.138 4.5 11.3369 4.5H21.8369C22.0358 4.5 22.2266 4.57902 22.3672 4.71967C22.5079 4.86032 22.5869 5.05109 22.5869 5.25C22.5869 5.44891 22.5079 5.63968 22.3672 5.78033C22.2266 5.92098 22.0358 6 21.8369 6H11.3369C11.138 6 10.9472 5.92098 10.8066 5.78033C10.6659 5.63968 10.5869 5.44891 10.5869 5.25ZM11.3369 9C11.138 9 10.9472 9.07902 10.8066 9.21967C10.6659 9.36032 10.5869 9.55109 10.5869 9.75C10.5869 9.94891 10.6659 10.1397 10.8066 10.2803C10.9472 10.421 11.138 10.5 11.3369 10.5H18.8369C19.0358 10.5 19.2266 10.421 19.3672 10.2803C19.5079 10.1397 19.5869 9.94891 19.5869 9.75C19.5869 9.55109 19.5079 9.36032 19.3672 9.21967C19.2266 9.07902 19.0358 9 18.8369 9H11.3369ZM11.3369 13.5C11.138 13.5 10.9472 13.579 10.8066 13.7197C10.6659 13.8603 10.5869 14.0511 10.5869 14.25C10.5869 14.4489 10.6659 14.6397 10.8066 14.7803C10.9472 14.921 11.138 15 11.3369 15H15.8369C16.0358 15 16.2266 14.921 16.3672 14.7803C16.5079 14.6397 16.5869 14.4489 16.5869 14.25C16.5869 14.0511 16.5079 13.8603 16.3672 13.7197C16.2266 13.579 16.0358 13.5 15.8369 13.5H11.3369ZM11.3369 18C11.138 18 10.9472 18.079 10.8066 18.2197C10.6659 18.3603 10.5869 18.5511 10.5869 18.75C10.5869 18.9489 10.6659 19.1397 10.8066 19.2803C10.9472 19.421 11.138 19.5 11.3369 19.5H12.8369C13.0358 19.5 13.2266 19.421 13.3672 19.2803C13.5079 19.1397 13.5869 18.9489 13.5869 18.75C13.5869 18.5511 13.5079 18.3603 13.3672 18.2197C13.2266 18.079 13.0358 18 12.8369 18H11.3369Z"
                  fill="black"
                />
              </svg>
            </div>
          </div>
        </div>
      </div>
      <p class="text-sm md:text-base lg:text-lg pb-4 mt-3 md:mt-6">
        {{ countResult }} items of {{ countAll }} items
      </p>
    </div>

    <SaleItemCard :items="items" />

    <!-- Pagination Tab Below -->
    <div class="mt-20">
      <Pagination
        v-model:page="page"
        v-model:pageSize="pageSize"
        :totalItems="totalItems"
      />
    </div>
  </div>
</template>
<style scoped>
.dropdown-content {
  position: absolute;
  top: 100%;
  left: 0;
  margin-top: 0.5rem;
  background: white;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1), 0 -4px 6px rgba(0, 0, 0, 0.05);
  padding: 0.75rem;
  border-radius: 0.5rem;
  z-index: 10;

  /* Responsive */
  min-width: 180px;
  max-width: min(90vw, 380px);
  max-height: 50vh;
  overflow: auto;
}
</style>
