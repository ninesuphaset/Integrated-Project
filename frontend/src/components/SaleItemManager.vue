<script setup>
import { ref, onMounted } from 'vue'
import { getItems, deleteItemById, addItemWithImage, editItemWithImage } from '@/libs/fetchUtils'
import { useRouter } from 'vue-router'
import { useAuth } from '@/stores/auth'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import { useEvent } from '@/stores/event'
import NotFound from './NotFound.vue'

const BASE_API_URL = import.meta.env.VITE_APP_URL + '/itb-mshop/v1'
const SALE_ITEMS_ENDPOINT = BASE_API_URL + '/sale-items'
const auth = useAuth()
const event = useEvent()
const isLoading = ref(false)
const allItems = ref([])
const router = useRouter()
const notFound = ref(false)

const fetchAllSaleItems = async () => {
  isLoading.value = true
  try {
    const res = await getItems(SALE_ITEMS_ENDPOINT)
    if (res.code === 200) {
      allItems.value = res.body
    } else {
      console.error(res.body.error)
      allItems.value = []
    }
  } catch (error) {
    console.error(error)
    allItems.value = []
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  await fetchAllSaleItems()
})

const addNewItem = async (formData) => {
  try {
    const res = await addItemWithImage(
      `${import.meta.env.VITE_APP_URL}/itb-mshop/v2/sellers/${auth.user.id}/sale-items`,
      formData
    );
    if (res.code === 201 || res.code === 200) {
      allItems.value.push(res.body);
      event.showNotification(
        'The sale item has been successfully added.',
        'success'
      );
      await fetchAllSaleItems();
    } else {
      console.error(res.body.error);
      event.showNotification('Failed to add item. Please try again.', 'error');
    }
  } catch (error) {
    console.error(error);
  }
};

const editExistingItem = async ({ saleItemId, formData }) => {
  try {
    const res = await editItemWithImage(
      `${import.meta.env.VITE_APP_URL}/itb-mshop/v2/sale-items/${saleItemId}`,
      formData
    )

    if (res.code === 200) {
      const updateIndex = allItems.value.findIndex(item => Number(item.id) === Number(saleItemId))
      if (updateIndex !== -1) allItems.value.splice(updateIndex, 1, res.body)

      event.showNotification('The sale item has been updated.', 'success')
      router.back()
    } else {
      console.error(res.body.error)
      event.showNotification('Failed to update item. Please try again.', 'error')
    }
  } catch (error) {
    console.error(error)
    event.showNotification('Cannot connect to server.', 'error')
  }
}


const deleteSaleItem = async (saleItemId) => {
  try {
    const res = await deleteItemById(`${SALE_ITEMS_ENDPOINT}/${saleItemId}`)
    if (res.code === 204) {
      allItems.value = allItems.value.filter(item => Number(item.id) !== Number(saleItemId))
      event.showNotification('The sale item has been deleted.', 'success')
      await fetchAllSaleItems()
    } else if (res.code === 404) {
      notFound.value = true
      event.showNotification('Failed to delete item. Please try again.', 'error')
      setTimeout(async () => {
        notFound.value = false
        await fetchAllSaleItems()
        router.push('/sale-items')
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
  <LoadingSpinner v-if="isLoading" />

  <NotFound
    v-else-if="notFound"
    title="Item"
    message="The requested sale item does not exist."
  />

  <router-view
    v-else
    :allItems="allItems"
    @createSaleItem="addNewItem"
    @updateSaleItem="editExistingItem"
    @deleteSaleItem="deleteSaleItem"
  />
</template>
