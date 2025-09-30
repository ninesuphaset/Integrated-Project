<script setup>
import Breadcrumb from '@/components/Breadcrumb.vue'
import NotFound from '@/components/NotFound.vue'
import { computed, reactive, watchEffect, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const props = defineProps({
  brands: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['createBrand', 'updateBrand'])
const router = useRouter()
const route = useRoute()
const brandId = computed(() => route.params.brandId)
const brandNotFound = ref(false)

const form = reactive({
  name: '',
  countryOfOrigin: '',
  websiteUrl: '',
  isActive: true,
})

const touched = reactive({
  name: false,
})

function onBlur(field) {
  touched[field] = true
}

const originalForm = ref(null)

watchEffect(() => {
  if (brandId.value && props.brands.length > 0) {
    const brand = props.brands.find((b) => b.id == brandId.value)
    if (brand) {
      form.name = brand.name
      form.countryOfOrigin = brand.countryOfOrigin
      form.websiteUrl = brand.websiteUrl
      form.isActive = brand.isActive
      originalForm.value = JSON.stringify({
        name: brand.name.trim(),
        countryOfOrigin: brand.countryOfOrigin?.trim() || '',
        websiteUrl: brand.websiteUrl?.trim() || '',
        isActive: brand.isActive,
      })

      brandNotFound.value = false
    } else {
      brandNotFound.value = true
    }
  }
})

// validate form
const isInputTooLong = computed(() => ({
  name: form.name.length > 30,
  websiteUrl: (form.websiteUrl ?? '').length > 40,
  countryOfOrigin: (form.countryOfOrigin ?? '').length > 80,
}))

const nameAlreadyExists = computed(() => {
  return props.brands.some((b) => {
    const isSameName =
      b.name.trim().toLowerCase() === form.name.trim().toLowerCase()
    const isDifferentId = !brandId.value || b.id != brandId.value
    return isSameName && isDifferentId
  })
})

function isValidUrl(url) {
  if (!url) return true
  try {
    const parsed = new URL(url)
    return (
      ['http:', 'https:'].includes(parsed.protocol) &&
      parsed.hostname.includes('.')
    )
  } catch {
    return false
  }
}

const isFormValid = computed(() => {
  const trimmedForm = {
    name: form.name ? form.name.trim() : '',
    countryOfOrigin: form.countryOfOrigin ? form.countryOfOrigin.trim() : '',
    websiteUrl: form.websiteUrl ? form.websiteUrl.trim() : '',
    isActive: form.isActive,
  }

  if (!trimmedForm.name || isInputTooLong.value.name || nameAlreadyExists.value)
    return false
  if (isInputTooLong.value.websiteUrl || !isValidUrl(trimmedForm.websiteUrl))
    return false
  if (isInputTooLong.value.countryOfOrigin) return false
  if (!brandId.value) return true
  return JSON.stringify(trimmedForm) !== originalForm.value
})

const submitForm = async () => {
  const trimmedForm = {
    ...form,
    name: form.name ? form.name.trim() : '',
    countryOfOrigin: form.countryOfOrigin ? form.countryOfOrigin.trim() : '',
    websiteUrl: form.websiteUrl ? form.websiteUrl.trim() : '',
  }

  if (brandId.value) {
    emit('updateBrand', {
      brandId: brandId.value,
      form: trimmedForm,
    })
  } else {
    emit('createBrand', trimmedForm)
  }
  router.back()
}

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
    <div class="bg-[#F6F6F6] py-5 lg:py-8 lg:px-10">
      <div class="bg-white rounded-3xl shadow p-12 mx-auto w-[90%] lg:w-[75%]">
        <NotFound
          v-if="brandNotFound"
          title="Brand"
          message="The brand does not exist."
        />

        <div v-else>
          <Breadcrumb
            v-if="!brandId"
            :breadcrumbs="[
              {
                label: 'Sale Item List',
                to: { name: 'SaleItemList' },
                className: 'itbms-item-list',
              },
              {
                label: 'Brand List',
                to: { name: 'BrandListView' },
                className: 'itbms-manage-brand',
              },
              { label: 'New Brand' },
            ]"
          />
          <Breadcrumb
            v-else
            :breadcrumbs="[
              {
                label: 'Sale Item List',
                to: { name: 'SaleItemList' },
                className: 'itbms-item-list',
              },
              {
                label: 'Brand List',
                to: { name: 'BrandListView' },
                className: 'itbms-manage-brand',
              },
              { label: 'Edit Brand' },
            ]"
          />
          <div class="mt-10 lg:mt-13 md:px-8 lg:p-6">
            <form
              @submit.prevent="submitForm"
              class="max-w-[600px] mx-auto space-y-6"
            >
              <div>
                <div class="lg:flex items-center">
                  <label class="text-sm lg:text-base font-medium mb-1 w-42"
                    >Name <span class="font-mormal text-red-500">*</span></label
                  >
                  <input
                    class="itbms-name border border-[#E4E4E4] py-2 px-3 rounded w-full lg:w-3/4 text-sm lg:text-base"
                    type="text"
                    required
                    v-model="form.name"
                    @blur="onBlur('name'), (form.name = form.name.trim())"
                    @keydown.enter="focusNextField"
                  />
                </div>
                <div class="flex justify-end mt-2">
                  <p
                    v-if="touched.name && nameAlreadyExists"
                    class="text-red-600 font-light text-sm mt-1"
                  >
                    Brand already exists.
                  </p>
                  <p
                    v-else-if="(touched.name && !form.name) || isInputTooLong.name"
                    class="itbms-message text-red-600 font-light text-sm mt-1"
                  >
                    Brand name must be 1-30 characters long.
                  </p>
                  <div
                    v-else
                    class="flex justify-end text-xs text-gray-500 pr-1"
                  >
                    {{ form.name.length }}/30
                  </div>
                </div>
              </div>
              <div>
                <div class="lg:flex items-center">
                  <label class="text-sm lg:text-base font-medium mb-1 w-42"
                    >Website Url</label
                  >
                  <input
                    class="itbms-websiteUrl border border-[#E4E4E4] py-2 px-3 rounded w-full lg:w-3/4 text-sm lg:text-base"
                    type="url"
                    placeholder="https://example.com"
                    v-model="form.websiteUrl"
                    @blur="form.websiteUrl = (form.websiteUrl || '').trim()"
                    @keydown.enter="focusNextField"
                  />
                </div>
                <div class="flex justify-end mt-2">
                  <p
                    v-if="!isValidUrl(form.websiteUrl)"
                    class="itbms-message text-red-600 font-light text-sm mt-1"
                  >
                    Brand URL must be a valid URL or not specified.
                  </p>
                  <p
                    v-else-if="isInputTooLong.websiteUrl"
                    class="itbms-message text-red-600 font-light text-sm mt-1"
                  >
                    Website url must be 1-40 characters long.
                  </p>
                  <div v-else class="text-xs text-gray-500 pr-1">
                    {{ (form.websiteUrl || '').length }}/40
                  </div>
                </div>
              </div>
              <div class="flex items-center">
                <label class="text-sm lg:text-base font-medium w-42"
                  >Active</label
                >
                <label class="relative inline-flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    v-model="form.isActive"
                    class="itbms-isActive opacity-0 w-11 h-6 absolute z-10 peer"
                  />
                  <div
                    class="w-11 h-6 bg-gray-200 rounded-full peer peer-checked:bg-blue-600 relative after:content-[''] after:absolute after:top-0.5 after:left-[2px] after:bg-white after:border after:border-gray-300 after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:after:translate-x-full peer-checked:after:border-white"
                  ></div>
                </label>
              </div>
              <div>
                <div class="lg:flex items-center">
                  <label class="text-sm lg:text-base font-medium mb-1 w-42"
                    >Country of Origin</label
                  >
                  <input
                    class="itbms-countryOfOrigin border border-[#E4E4E4] py-2 px-3 rounded w-full lg:w-3/4 text-sm lg:text-base"
                    type="text"
                    v-model="form.countryOfOrigin"
                    @blur="
                      form.countryOfOrigin = (form.countryOfOrigin || '').trim()
                    "
                    @keydown.enter="focusNextField"
                  />
                </div>
                <div class="flex justify-end mt-2">
                  <p
                    v-if="isInputTooLong.countryOfOrigin"
                    class="itbms-message text-red-600 font-light text-sm mt-1"
                  >
                    Brand country of origin must be 1-80 characters long or not
                    specified.
                  </p>
                  <div v-else class="text-xs text-gray-500 pr-1">
                    {{ (form.countryOfOrigin || '').length }}/80
                  </div>
                </div>
              </div>
              <div class="flex justify-end gap-2 lg:gap-6 mt-8">
                <button
                  type="button"
                  @click="router.push('/brands')"
                  class="itbms-cancel-button text-[#999999] border border-[#E4E4E4] text-sm px-2 py-1 lg:text-base lg:px-4 lg:py-2 rounded cursor-pointer hover:border-[#999999] hover:bg-[#F0F0F0]"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  :disabled="!isFormValid"
                  :class="[
                    'itbms-save-button bg-blue-600 text-white text-sm px-4 py-1 lg:text-base lg:px-5 lg:py-2 rounded cursor-pointer hover:border-[#0171E3] hover:bg-[#157DE6]',
                    !isFormValid
                      ? 'bg-gray-400 hover:cursor-not-allowed hover:bg-gray-400'
                      : '',
                  ]"
                >
                  Save
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
