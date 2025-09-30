<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'

import Breadcrumb from '@/components/Breadcrumb.vue'
import { getItems, getItemById } from '@/libs/fetchUtils'
import NotFound from '@/components/NotFound.vue'
import SaleItemImageUploader from '@/components/SaleItemImageUploader.vue'

const emit = defineEmits(['createSaleItem', 'updateSaleItem'])

const APP_URL = import.meta.env.VITE_APP_URL
const brands = ref([])

const router = useRouter()
const imagePreviews = ref([])
const { saleItemId } = useRoute().params

const form = reactive({
  brand: '',
  model: '',
  price: null,
  description: '',
  ramGb: null,
  storageGb: null,
  screenSizeInch: null,
  color: '',
  quantity: '',
})

const touched = reactive({
  brand: false,
  model: false,
  price: false,
  description: false,
})

function onBlur(field) {
  touched[field] = true
}

const submitForm = async () => {
  const payload = {
    ...form,
    images: imagePreviews.value, // imagePreviews ต้องรวมทั้งไฟล์ใหม่และไฟล์เดิม
  }
  console.log(payload)

  if (saleItemId) {
    emit('updateSaleItem', { saleItemId, formData: payload })
  } else {
    emit('createSaleItem', payload)
  }
  router.back()
}

const originalForm = ref({})
const originalImages = ref([])
const notFound = ref(false)

onMounted(async () => {
  try {
    const brandRes = await getItems(APP_URL + '/itb-mshop/v1/brands')
    if (brandRes.code === 200) {
      brands.value = brandRes.body.sort((a, b) => {
        return a.name.localeCompare(b.name, 'en')
      })
    } else {
      console.error(brandRes.body.error)
      brands.value = []
    }

    if (saleItemId) {
      const editedItem = await getItemById(
        APP_URL + `/itb-mshop/v2/sale-items/${saleItemId}`
      )

      if (editedItem.code === 200) {
        originalForm.value = JSON.parse(JSON.stringify(editedItem.body))
        Object.assign(form, editedItem.body)
        form.brand =
          brands.value.find(
            (brand) => brand.name === editedItem.body.brandName
          ) || ''

        imagePreviews.value = editedItem.body.saleItemImages.map((img) => ({
          file: null,
          preview: `${APP_URL}/itb-mshop/v2/images/${
            img.fileName
          }?t=${Date.now()}`,
          url: `${APP_URL}/itb-mshop/v2/images/${img.fileName}?t=${Date.now()}`,
          fileName: img.fileName,
          status: 'ONLINE',
        }))
        originalImages.value = JSON.parse(JSON.stringify(imagePreviews.value))
      } else {
        notFound.value = true
      }
    }
  } catch (error) {
    console.error(error)
    brands.value = []
  }
})

// validate form
const isInputTooLong = computed(() => ({
  model: (form.model ?? '').length > 60,
  description: (form.description ?? '').length > 16384,
  color: (form.color ?? '').length > 40,
}))

function isNonNegativeInteger(value) {
  return Number.isInteger(value) && value >= 0
}

function isPositiveInteger(value) {
  return Number.isInteger(value) && value > 0
}

function isAtMostTwoDecimal(value) {
  if (typeof value !== 'number') return false
  if (value <= 0) return false
  return Number(value.toFixed(2)) === value
}

const isFormValid = computed(() => {
  if (!form.brand) return false
  if (!form.model || isInputTooLong.value.model) return false
  if (
    form.price === null ||
    form.price === undefined ||
    form.price === '' ||
    !isNonNegativeInteger(form.price)
  )
    return false
  if (!form.description || isInputTooLong.value.description) return false

  if (isInputTooLong.value.color) return false

  if (
    (form.ramGb !== null &&
      form.ramGb !== undefined &&
      form.ramGb !== '' &&
      !isPositiveInteger(form.ramGb)) ||
    (form.storageGb !== null &&
      form.storageGb !== undefined &&
      form.storageGb !== '' &&
      !isPositiveInteger(form.storageGb)) ||
    (form.screenSizeInch !== null &&
      form.screenSizeInch !== undefined &&
      form.screenSizeInch !== '' &&
      (form.screenSizeInch <= 0 || !isAtMostTwoDecimal(form.screenSizeInch))) ||
    (form.quantity !== null &&
      form.quantity !== undefined &&
      form.quantity !== '' &&
      !isNonNegativeInteger(form.quantity))
  )
    return false

  return true
})

const isImagesChanged = computed(() => {
  if (imagePreviews.value.length !== originalImages.value.length) return true

  for (let i = 0; i < imagePreviews.value.length; i++) {
    const curr = imagePreviews.value[i]
    const orig = originalImages.value[i]

    if (curr.file || curr.url !== orig.url || curr.status === 'DELETE')
      return true
  }

  return false
})

const isFormChanged = computed(() => {
  if (!saleItemId) return true
  const brandChanged = form.brand?.name !== originalForm.value.brandName
  const formChanged =
    brandChanged ||
    form.model !== originalForm.value.model ||
    form.price !== originalForm.value.price ||
    form.description !== originalForm.value.description ||
    form.ramGb !== originalForm.value.ramGb ||
    form.storageGb !== originalForm.value.storageGb ||
    form.screenSizeInch !== originalForm.value.screenSizeInch ||
    form.color !== originalForm.value.color ||
    form.quantity !== originalForm.value.quantity

  return formChanged || isImagesChanged.value
})

const focusNextField = (event) => {
  const formElements = Array.from(
    event.target.closest('form').querySelectorAll('input, textarea')
  ).filter((el) => !el.disabled && el.type !== 'hidden')

  const index = formElements.indexOf(event.target)
  if (index > -1 && index < formElements.length - 1) {
    formElements[index + 1].focus()
  }
}
</script>

<template>
  <div class="mx-auto">
    <NotFound
      v-if="notFound"
      title="Item"
      message="The requested sale item does not exist."
    />
    <div v-else class="bg-[#F6F6F6] py-6 lg:py-8">
      <div class="bg-white rounded-3xl shadow p-8 lg:p-12 w-[90%] mx-auto">
        <Breadcrumb
          v-if="!saleItemId"
          :breadcrumbs="[
            {
              label: 'Home',
              to: { name: 'SaleItemGallery' },
              className: 'itbms-home-button',
            },
            { label: 'New Sale Item' },
          ]"
        />

        <Breadcrumb
          v-else
          :breadcrumbs="[
            {
              label: 'Home',
              to: { name: 'SaleItemGallery' },
              className: 'itbms-home-button',
            },
            {
              label:
                originalForm.brandName +
                ' ' +
                originalForm.model +
                ' ' +
                (originalForm.ramGb ?? '') +
                (originalForm.ramGb && originalForm.storageGb ? '/' : '') +
                (originalForm.storageGb ?? '') +
                (originalForm.ramGb || originalForm.storageGb ? 'GB' : '') +
                ' ' +
                (originalForm.color ?? ''),
              to: { name: 'SaleItemDetail', params: { saleItemId } },
              color: 'text-[#131927]',
            },
          ]"
        />
        <form
          @submit.prevent="submitForm"
          class="flex items-start flex-col lg:flex-row"
        >
          <!-- upload image -->
          <SaleItemImageUploader
            v-if="!saleItemId"
            :images="imagePreviews"
            @update:images="(val) => (imagePreviews = val)"
          />
          <!-- edit image -->
          <SaleItemImageUploader
            v-else
            :images="imagePreviews"
            @update:images="(val) => (imagePreviews = val)"
          />

          <!-- input field -->
          <div class="mt-8 w-full lg:w-1/2 lg:pl-16 lg:pr-12 space-y-5">
            <div>
              <div class="lg:flex items-center gap-4 mb-2">
                <label class="font-medium w-24"
                  >Brand <span class="text-red-500 font-normal">*</span></label
                >
                <div class="flex-1">
                  <select
                    v-model="form.brand"
                    @blur="onBlur('brand')"
                    class="itbms-brand w-full border border-[#E4E4E4] py-1.5 pl-4 rounded"
                  >
                    <option value=""></option>
                    <option
                      v-for="brand in brands"
                      :key="brand.id"
                      :value="brand"
                    >
                      {{ brand.name }}
                    </option>
                  </select>
                </div>
              </div>
              <div class="flex justify-end">
                <p
                  v-if="touched.brand && !form.brand"
                  class="itbms-message text-red-600 font-light text-sm"
                >
                  Brand must be selected.
                </p>
              </div>
            </div>

            <div class="flex flex-col gap-1">
              <div class="lg:flex items-center gap-4">
                <label class="w-32 font-medium"
                  >Model <span class="text-red-500 font-normal">*</span></label
                >
                <input
                  v-model="form.model"
                  type="text"
                  class="itbms-model flex-1 border border-[#E4E4E4] py-1.5 pl-4 rounded w-full"
                  @blur=";(form.model = form.model.trim()), onBlur('model')"
                  @keydown.enter="focusNextField"
                  required
                />
              </div>

              <div class="flex justify-end">
                <p
                  v-if="(touched.model && !form.model) || isInputTooLong.model"
                  class="itbms-message text-red-600 font-light text-sm mt-1"
                >
                  Model must be 1-60 characters long.
                </p>
                <div v-else class="flex justify-end text-xs text-gray-500 pr-1">
                  {{ form.model.length }}/60
                </div>
              </div>
            </div>

            <div class="space-y-2">
              <div class="lg:flex items-center gap-4">
                <label class="w-32 font-medium"
                  >Price (Baht)
                  <span class="text-red-500 font-normal">*</span></label
                >
                <input
                  v-model.number="form.price"
                  type="number"
                  class="itbms-price flex-1 border border-[#E4E4E4] py-1.5 pl-4 rounded w-full"
                  @blur="onBlur('price')"
                  required
                  @keydown.enter="focusNextField"
                />
              </div>
              <div class="flex justify-end">
                <div
                  v-if="
                    (touched.price &&
                      (form.price === null ||
                        form.price === undefined ||
                        form.price === '')) ||
                    (form.price !== null &&
                      form.price !== undefined &&
                      form.price !== '' &&
                      !isNonNegativeInteger(form.price))
                  "
                  class="itbms-message text-red-600 font-light text-sm"
                >
                  Price must be non-negative integer.
                </div>
              </div>
            </div>

            <div class="flex flex-col gap-2">
              <label class="font-medium"
                >Description
                <span class="text-red-500 font-normal">*</span></label
              >
              <textarea
                v-model="form.description"
                class="itbms-description border border-[#E4E4E4] py-1.5 pl-4 rounded"
                rows="3"
                @blur="
                  ;(form.description = form.description.trim()),
                    onBlur('description')
                "
                required
                @keydown.enter="focusNextField"
              ></textarea>
              <div class="flex justify-end">
                <p
                  v-if="
                    (touched.description && !form.description) ||
                    isInputTooLong.description
                  "
                  class="itbms-message text-red-600 font-light text-sm mt-1"
                >
                  Description must be 1-16,384 characters long.
                </p>
              </div>
            </div>

            <div class="space-y-4 lg:grid lg:grid-cols-2 lg:gap-4">
              <div>
                <div>
                  <label class="font-medium">RAM (GB)</label>
                  <input
                    v-model.number="form.ramGb"
                    type="number"
                    class="itbms-ramGb w-full border border-[#E4E4E4] py-1.5 pl-4 rounded mt-1"
                    @keydown.enter="focusNextField"
                  />
                </div>
                <div class="flex justify-end">
                  <div
                    v-if="
                      form.ramGb !== null &&
                      form.ramGb !== undefined &&
                      form.ramGb !== '' &&
                      !isPositiveInteger(form.ramGb)
                    "
                    class="itbms-message text-red-600 font-light text-sm"
                  >
                    RAM size must be positive integer or not specified.
                  </div>
                </div>
              </div>
              <div>
                <div>
                  <label class="font-medium">Storage (GB)</label>
                  <input
                    v-model.number="form.storageGb"
                    type="number"
                    class="itbms-storageGb w-full border border-[#E4E4E4] py-1.5 pl-4 rounded mt-1"
                    @keydown.enter="focusNextField"
                  />
                </div>
                <div class="flex justify-end">
                  <div
                    v-if="
                      form.storageGb !== null &&
                      form.storageGb !== undefined &&
                      form.storageGb !== '' &&
                      !isPositiveInteger(form.storageGb)
                    "
                    class="itbms-message text-red-600 font-light text-sm"
                  >
                    Storage size must be positive integer or not specified.
                  </div>
                </div>
              </div>
            </div>
            <div>
              <div class="lg:flex items-center gap-4">
                <label class="w-32 font-medium">Screen Size</label>
                <input
                  v-model.number="form.screenSizeInch"
                  type="number"
                  step="0.01"
                  class="itbms-screenSizeInch flex-1 border border-[#E4E4E4] py-1.5 pl-4 rounded w-full"
                  @keydown.enter="focusNextField"
                />
              </div>
              <div class="flex justify-end">
                <div
                  v-if="
                    form.screenSizeInch !== null &&
                    form.screenSizeInch !== undefined &&
                    form.screenSizeInch !== '' &&
                    !isAtMostTwoDecimal(form.screenSizeInch)
                  "
                  class="itbms-message text-red-600 font-light text-sm"
                >
                  Screen size must be positive number with at most 2 decimal
                  points or not specified.
                </div>
              </div>
            </div>
            <div>
              <div class="lg:flex items-center gap-4">
                <label class="w-32 font-medium">Color</label>
                <input
                  v-model="form.color"
                  @blur="form.color = form.color.trim()"
                  class="itbms-color flex-1 border border-[#E4E4E4] py-1.5 pl-4 rounded w-full"
                  @keydown.enter="focusNextField"
                />
              </div>
              <div class="flex justify-end">
                <p
                  v-if="isInputTooLong.color"
                  class="itbms-message text-red-600 font-light text-sm mt-1"
                >
                  Color must be 1-40 characters long or not specified.
                </p>
                <div v-else class="text-xs text-gray-500 pr-1">
                  {{ (form.color || '').length }}/40
                </div>
              </div>
            </div>
            <div>
              <div class="lg:flex items-center gap-4">
                <label class="w-32 font-medium">Quantity</label>
                <input
                  v-model.number="form.quantity"
                  type="number"
                  class="itbms-quantity flex-1 border border-[#E4E4E4] py-1.5 pl-4 rounded w-full"
                  @keydown.enter="focusNextField"
                />
              </div>
              <div class="flex justify-end">
                <div
                  v-if="
                    form.quantity !== null &&
                    form.quantity !== undefined &&
                    form.quantity !== '' &&
                    !isNonNegativeInteger(form.quantity)
                  "
                  class="itbms-message text-red-600 font-light text-sm"
                >
                  Quantity must be non-negative integer.
                </div>
              </div>
            </div>
            <div class="flex justify-end gap-2 lg:gap-6 mt-8">
              <button
                type="button"
                @click="router.back()"
                class="itbms-cancel-button text-[#999999] border border-[#E4E4E4] text-sm px-2 py-1 lg:text-base lg:px-4 lg:py-2 rounded cursor-pointer hover:border-[#999999] hover:bg-[#F0F0F0]"
              >
                Cancel
              </button>
              <button
                type="submit"
                :disabled="!isFormValid || !isFormChanged"
                :class="[
                  'itbms-save-button bg-blue-600 text-white text-sm px-4 py-1 lg:text-base lg:px-5 lg:py-2 rounded cursor-pointer hover:border-[#0171E3] hover:bg-[#157DE6]',
                  !isFormValid || !isFormChanged
                    ? 'bg-gray-400 hover:cursor-not-allowed hover:bg-gray-400'
                    : '',
                ]"
              >
                Save
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
