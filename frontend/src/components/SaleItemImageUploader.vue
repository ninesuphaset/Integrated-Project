<script setup>
import { ref, watch, computed } from 'vue'
import { useEvent } from '@/stores/event'

const eventNoti = useEvent()
const emit = defineEmits(['update:images'])
const props = defineProps({
  images: {
    type: Array,
    default: () => [],
  },
})

const backendImages = ref([
  { file: null, fileName: '', url: '', status: '' },
  { file: null, fileName: '', url: '', status: '' },
  { file: null, fileName: '', url: '', status: '' },
  { file: null, fileName: '', url: '', status: '' },
])

watch(
  () => props.images,
  (newVal) => {
    if (!newVal || newVal.length === 0) return
    backendImages.value = backendImages.value.map((slot, index) => {
      const img = newVal[index]
      return img
        ? {
            file: img.file || null,
            fileName: img.fileName || '',
            url: img.url || '',
            status: img.status || '',
          }
        : slot
    })
  },
  { immediate: true }
)

// computed → เอาไว้ render UI (ไม่เอา DELETE)
const imagePreviews = computed(() =>
  backendImages.value.map((img) => ({
    preview: img.file ? URL.createObjectURL(img.file) : img.url,
    file: img.file,
    url: img.url,
    status: img.status,
  }))
)

// upload รูปใหม่
function handleImageUpload(event) {
  const files = Array.from(event.target.files)

  files.forEach((file) => {
    // file size
    if (file.size > 2 * 1024 * 1024) {
      eventNoti.showNotification(
        `The picture file size cannot be larger than 2MB.`,
        'warning'
      )
      return
    }

    // หาช่องว่าง
    const emptySlot = backendImages.value.find(
      (img) => !img.status || img.status === 'DELETE'
    )
    if (!emptySlot) {
      eventNoti.showNotification('Maximum 4 pictures are allowed.', 'warning')
      return
    }

    // เช็กชื่อไฟล์ซ้ำ
    const isDuplicate = backendImages.value.some(
      (img) =>
        img.fileName === file.name &&
        img.status !== '' &&
        img.status !== 'DELETE'
    )
    if (isDuplicate) {
      eventNoti.showNotification(
        `File "${file.name}" has already been uploaded.`,
        'warning'
      )
      return
    }

    // เติมข้อมูลลงช่องว่าง
    emptySlot.file = file
    emptySlot.fileName = file.name
    emptySlot.url = ''
    emptySlot.status = 'NEW'
  })

  emitFiles()
  event.target.value = ''
}

// ลบรูป
function removeImage(index) {
  const backend = backendImages.value[index]
  if (!backend) return

  if (backend.file) {
    // ถ้าเป็นไฟล์ใหม่ → clear slot
    backendImages.value[index] = {
      file: null,
      fileName: '',
      url: '',
      status: '',
    }
  } else if (backend.url) {
    // ถ้าเป็นรูปเก่า → mark DELETE
    backend.status = 'DELETE'
  }

  emitFiles()
}

// ย้ายรูป (รวม up / down)
function moveImage(index, direction) {
  const targetIndex = index + direction
  if (targetIndex < 0 || targetIndex >= backendImages.value.length) return

  // swap ระหว่างช่อง index กับ targetIndex
  const temp = backendImages.value[index]
  backendImages.value[index] = backendImages.value[targetIndex]
  backendImages.value[targetIndex] = temp

  // mark รูปที่ถูกย้าย ถ้าเป็น ONLINE → MOVE
  ;[backendImages.value[index], backendImages.value[targetIndex]].forEach(
    (img) => {
      if (img.status === 'ONLINE') {
        img.status = 'MOVE'
      }
    }
  )

  emitFiles()
}

const firstActiveImage = computed(() =>
  imagePreviews.value.find((img) => img.status && img.status !== 'DELETE')
)

// ส่งไป parent
function emitFiles() {
  emit('update:images', backendImages.value)
}
</script>

<template>
  <div class="mt-8 w-full lg:w-1/2 lg:pr-8 relative">
    <!-- กรอบ preview ใหญ่ -->
    <div
      class="h-[300px] lg:h-[500px] rounded-2xl border-2 border-gray-300 flex flex-col items-center justify-center overflow-hidden relative mb-4 cursor-pointer"
      @click="$refs.fileInput.click()"
    >
      <template v-if="firstActiveImage">
        <img
          :src="firstActiveImage.preview"
          class="max-h-full max-w-full object-contain"
        />
      </template>

      <template v-else>
        <svg
          width="105"
          height="105"
          viewBox="0 0 105 105"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          class="mb-2 text-gray-400"
        >
          <path
            d="M91.875 65.625V78.75H105V87.5H91.875V100.625H83.125V87.5H70V78.75H83.125V65.625H91.875ZM91.91 13.125C94.3075 13.125 96.25 15.0719 96.25 17.4694V56.875H87.5V21.875H17.5V83.1206L61.25 39.375L74.375 52.5V64.8769L61.25 51.7519L29.8681 83.125H61.25V91.875H13.09C11.9386 91.8738 10.8347 91.4156 10.0209 90.601C9.20711 89.7864 8.75 88.6821 8.75 87.5306V17.4694C8.75801 16.3204 9.21769 15.2207 10.0297 14.4078C10.8418 13.595 11.941 13.1342 13.09 13.125H91.91ZM35 30.625C37.3206 30.625 39.5462 31.5469 41.1872 33.1878C42.8281 34.8288 43.75 37.0544 43.75 39.375C43.75 41.6956 42.8281 43.9212 41.1872 45.5622C39.5462 47.2031 37.3206 48.125 35 48.125C32.6794 48.125 30.4538 47.2031 28.8128 45.5622C27.1719 43.9212 26.25 41.6956 26.25 39.375C26.25 37.0544 27.1719 34.8288 28.8128 33.1878C30.4538 31.5469 32.6794 30.625 35 30.625Z"
            fill="currentColor"
          />
        </svg>
        <p class="text-gray-400 text-sm select-none mb-1">
          Click to upload images
        </p>
        <p class="text-gray-400 text-xs select-none">Support JPG, PNG, JPEG</p>
      </template>
    </div>

    <!-- กรอบเล็ก 4 ช่อง -->
    <div class="grid grid-cols-4 gap-4">
      <div
        v-for="(img, index) in imagePreviews"
        :key="'small-frame-' + index"
        class="flex flex-col items-center"
      >
        <div
          class="relative rounded-2xl border-2 border-gray-300 overflow-hidden flex items-center justify-center bg-white"
          style="height: 150px; width: 100%"
        >
          <!-- ถ้ามีรูป -->
          <template v-if="img.status && img.status !== 'DELETE'">
            <img
              :src="img.preview"
              class="max-h-full max-w-full object-contain"
            />
          </template>

          <!-- ถ้าว่างหรือถูกลบ -->
          <template v-else>
            <div class="flex flex-col items-center text-gray-400 text-xs"></div>
          </template>
        </div>
      </div>
    </div>

    <!-- ปุ่ม Upload Pictures -->
    <label
      class="itbms-upload-button mt-5 inline-block bg-[#3B81C9] text-white px-4 py-2 rounded cursor-pointer select-none hover:bg-[#316dad] transition mb-5"
      
    >
      Upload Pictures
      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        multiple
        class="hidden"
        @change="handleImageUpload"
      />
    </label>

    <div
      v-for="index in 4"
      :key="'small-frame-' + index"
      class="flex items-center gap-2 mb-1"
    >
      <!-- ชื่อไฟล์ + ปุ่มลบ -->
      <div
        v-if="
          imagePreviews[index - 1] &&
          imagePreviews[index - 1].status !== 'DELETE' &&
          imagePreviews[index - 1].status !== ''
        "
        class="bg-gray-100 rounded flex justify-between items-center w-full"
      >
        <div class="text-xs p-1.5 px-3">
          {{
            imagePreviews[index - 1].file?.name ||
            (imagePreviews[index - 1].url
              ? imagePreviews[index - 1].url.split('/').pop().split('?')[0]
              : 'Unnamed')
          }}
        </div>
        <button
          @click.prevent="removeImage(index - 1)"
          class="text-sm cursor-pointer pr-2"
        >
          ×
        </button>
      </div>

      <!-- ปุ่มเลื่อนขึ้น / ลง -->
      <div
        v-if="
          imagePreviews[index - 1] &&
          imagePreviews[index - 1].status !== '' &&
          imagePreviews[index - 1].status !== 'DELETE'
        "
        class="flex gap-1 ml-auto"
      >
        <!-- ปุ่มเลื่อนขึ้น -->
        <button
          v-if="index > 1"
          @click.prevent="moveImage(index - 1, -1)"
          class="text-sm px-2 py-0.5 bg-[#C0D6EC] rounded hover:bg-gray-300 cursor-pointer"
        >
          ↑
        </button>

        <!-- ปุ่มเลื่อนลง -->
        <button
          v-if="index < imagePreviews.length"
          @click.prevent="moveImage(index - 1, 1)"
          class="text-sm px-2 py-0.5 bg-[#C0D6EC] rounded hover:bg-gray-300 cursor-pointer"
        >
          ↓
        </button>
      </div>
    </div>
  </div>
</template>
