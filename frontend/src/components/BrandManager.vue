<script setup>
import { ref, onMounted } from 'vue'
import { getItems, deleteItemById, addItem, editItem } from '@/libs/fetchUtils'
import { useEvent } from '@/stores/event'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import NotFound from './NotFound.vue'

const BASE_API_URL = import.meta.env.VITE_APP_URL + '/itb-mshop/v1'
const BRANDS_ENDPOINT = BASE_API_URL + '/brands'

const isLoading = ref(false)
const event = useEvent()
const brands = ref([])
const notFound = ref(false)

const fetchBrands = async () => {
  isLoading.value = true
  try {
    const res = await getItems(BRANDS_ENDPOINT)
    if (res.code === 200) {
      brands.value = res.body
    } else {
      console.error(res.body.error)
      brands.value = []
    }
  } catch (error) {
    console.error(error)
    brands.value = []
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  await fetchBrands()
})

const addNewBrand = async (form) => {
  try {
    const res = await addItem(BRANDS_ENDPOINT, form)
    if (res.code === 201) {
      brands.value.push(res.body)
      event.showNotification('The brand has been added.', 'success')
    } else {
      console.error(res.body.error)
      event.showNotification('The brand could not be added.', 'error')
    }
  } catch (error) {
    console.error(error)
  }
}

const editBrand = async ({ brandId, form }) => {
  try {
    const res = await editItem(`${BRANDS_ENDPOINT}/${brandId}`, form)
    if (res.code === 200) {
      const index = brands.value.findIndex(b => Number(b.id) === Number(brandId))
      if (index !== -1) {
        brands.value[index] = {
          ...brands.value[index],
          ...res.body,
        }
      }
      event.showNotification('The brand has been updated.', 'success')
    } else if (res.code === 404) {
      event.showNotification('The brand does not exist.', 'error')
    } else {
      console.error(res.body.error)
      event.showNotification('Failed to update brand.', 'error')
    }
  } catch (error) {
    console.error(error)
  }
}

const deleteBrand = async (brandId) => {
  try {
    const res = await deleteItemById(`${BRANDS_ENDPOINT}/${brandId}`)
    if (res.code === 204) {
      brands.value = brands.value.filter(
        (brand) => Number(brand.id) !== Number(brandId)
      )
      event.showNotification('The brand has been deleted.', 'success')
    } else if (res.code === 404) {
      notFound.value = true
      event.showNotification(
        'An error has occurred, the brand does not exist.',
        'error'
      )
      setTimeout(async () => {
        notFound.value = false
        await fetchBrands()
      }, 2000)
    } else {
      console.error(res.body.error)
    }
  } catch (error) {
    console.error(error)
  }
}
</script>

<template>
  <div>
    <LoadingSpinner v-if="isLoading" />

    <NotFound
      v-else-if="notFound"
      title="Brand"
      message="The brand does not exist."
    />
    <router-view
      v-else
      :brands="brands"
      @createBrand="addNewBrand"
      @updateBrand="editBrand"
      @deleteBrand="deleteBrand"
    />
  </div>
</template>
