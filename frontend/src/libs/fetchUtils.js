import { useAuth } from '@/stores/auth'

function getAuthHeader() {
  const auth = useAuth()
  // ถ้า login มี token → return Authorization header, ถ้าไม่ → return object ว่าง
  return auth.isLoggedIn ? { Authorization: `Bearer ${auth.accessToken}` } : {}
}

async function getItems(url) {
  try {
    let response = await fetch(url, { mode: 'cors' })
    let responseBody = await response.json()
    if (response.ok) {
      return { body: responseBody, code: response.status }
    } else {
      return {
        body: {
          error:
            response.status +
            ' ' +
            (responseBody?.error ?? '') +
            ' ' +
            (responseBody?.message ?? ''),
        },
        code: response.status,
      }
    }
  } catch (error) {
    return { body: { error: 'Can not connect to ' + url }, code: 500 }
  }
}

async function getItemById(url) {
  try {
    let response = await fetch(url, { mode: 'cors' })
    let responseBody = await response.json()
    if (response.ok) {
      return { body: responseBody, code: response.status }
    } else {
      return {
        body: {
          error:
            response.status +
            ' ' +
            (responseBody?.error ?? '') +
            ' ' +
            (responseBody?.message ?? ''),
        },
        code: response.status,
      }
    }
  } catch (error) {
    return { body: { error: 'Can not connect to ' + url }, code: 500 }
  }
}

async function addItem(url, data) {
  try {
    let response = await fetch(url, {
      method: 'POST',
      mode: 'cors',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    })

    let responseBody = {}
    try {
      responseBody = await response.json()
    } catch (e) {
      console.warn('Response is not JSON:', e)
    }

    if (response.ok) {
      return { body: responseBody, code: response.status }
    } else {
      return {
        body: {
          error:
            response.status +
            ' ' +
            (responseBody?.error ?? '') +
            ' ' +
            (responseBody?.message ?? ''),
        },
        code: response.status,
      }
    }
  } catch (error) {
    return { body: { error: 'Can not connect to ' + url }, code: 500 }
  }
}

async function editItem(url, data) {
  try {
    let response = await fetch(url, {
      method: 'PUT',
      mode: 'cors',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    })
    let responseBody = await response.json()
    if (response.ok) {
      return { body: responseBody, code: response.status }
    } else {
      return {
        body: {
          error:
            response.status +
            ' ' +
            (responseBody?.error ?? '') +
            ' ' +
            (responseBody?.message ?? ''),
        },
        code: response.status,
      }
    }
  } catch (error) {
    return { body: { error: 'Can not connect to ' + url }, code: 500 }
  }
}

async function addItemWithImage(url, data) {
  try {
    const formData = new FormData()

    // fields ปกติ
    formData.append('brandId', data.brand.id)
    formData.append('model', data.model)
    formData.append('price', data.price)
    formData.append('description', data.description)
    if (data.ramGb != null) formData.append('ramGb', data.ramGb.toString())
    if (data.storageGb != null)
      formData.append('storageGb', data.storageGb.toString())
    if (data.screenSizeInch != null)
      formData.append('screenSizeInch', data.screenSizeInch.toString())
    formData.append('color', data.color)
    const quantityValue =
      data.quantity != null && data.quantity !== '' ? data.quantity : 1
    formData.append('quantity', quantityValue.toString())

    // images (เฉพาะไฟล์ใหม่ สำหรับ add)
    const images = data.images || []
    images.forEach((img, idx) => {
      if (img.file) {
        formData.append(`imageInfos[${idx}].imageFile`, img.file)
        formData.append(`imageInfos[${idx}].status`, 'NEW')
        formData.append(`imageInfos[${idx}].order`, (idx + 1).toString())
      }
    })

    const response = await fetch(url, {
      method: 'POST',
      headers: {
        ...getAuthHeader(),
      },
      body: formData,
    })

    let responseBody = {}
    try {
      responseBody = await response.json()
    } catch (e) {
      console.warn('Response is not JSON:', e)
    }

    if (response.ok) return { body: responseBody, code: response.status }
    else
      return {
        body: {
          error:
            response.status +
            ' ' +
            (responseBody?.error ?? '') +
            ' ' +
            (responseBody?.message ?? ''),
        },
        code: response.status,
      }
  } catch (error) {
    return { body: { error: 'Can not connect to ' + url }, code: 500 }
  }
}

async function editItemWithImage(url, data) {
  try {
    const formData = new FormData()

    // append fields ปกติ (ไม่รวม images)
    formData.append('brandId', data.brand.id)
    formData.append('model', data.model)
    formData.append('price', data.price)
    formData.append('description', data.description)
    if (data.ramGb != null) formData.append('ramGb', data.ramGb.toString())
    if (data.storageGb != null)
      formData.append('storageGb', data.storageGb.toString())
    if (data.screenSizeInch != null)
      formData.append('screenSizeInch', data.screenSizeInch.toString())
    formData.append('color', data.color ?? '')
    formData.append('quantity', (data.quantity ?? 1).toString())

    // images: รวมไฟล์ใหม่ + รูปเดิม
    const images = data.images || []
    images.forEach((img, idx) => {
      if (img.status === 'NEW' && img.file) {
        // ไฟล์ใหม่
        formData.append(`imageInfos[${idx}].imageFile`, img.file)
        formData.append(`imageInfos[${idx}].status`, 'NEW')
        formData.append(`imageInfos[${idx}].order`, (idx + 1).toString())
      } else if (img.status === 'ONLINE') {
        // รูปเดิมยังอยู่
        formData.append(`imageInfos[${idx}].fileName`, img.fileName)
        formData.append(`imageInfos[${idx}].status`, 'ONLINE')
        formData.append(`imageInfos[${idx}].order`, (idx + 1).toString())
      } else if (img.status === 'DELETE') {
        // รูปเดิมถูกลบ
        formData.append(`imageInfos[${idx}].fileName`, img.fileName)
        formData.append(`imageInfos[${idx}].status`, 'DELETE')
      } else if (img.status === 'MOVE') {
        // รูปเดิมถูกย้าย
        formData.append(`imageInfos[${idx}].fileName`, img.fileName)
        formData.append(`imageInfos[${idx}].status`, 'MOVE')
        formData.append(`imageInfos[${idx}].order`, (idx + 1).toString())
      }
    })

    const response = await fetch(url, {
      method: 'PUT',
      body: formData,
    })

    let responseBody = {}
    try {
      responseBody = await response.json()
    } catch (e) {
      console.warn('Response is not JSON:', e)
    }

    if (response.ok) return { body: responseBody, code: response.status }
    else
      return {
        body: {
          error:
            response.status +
            ' ' +
            (responseBody?.error ?? '') +
            ' ' +
            (responseBody?.message ?? ''),
        },
        code: response.status,
      }
  } catch (error) {
    return { body: { error: 'Can not connect to ' + url }, code: 500 }
  }
}

async function deleteItemById(url) {
  try {
    let response = await fetch(url, {
      method: 'DELETE',
      mode: 'cors',
    })
    if (response.ok) {
      return { body: {}, code: response.status }
    } else {
      let responseBody = await response.json()
      return {
        body: {
          error:
            response.status +
            ' ' +
            (responseBody?.error ?? '') +
            ' ' +
            (responseBody?.message ?? ''),
        },
        code: response.status,
      }
    }
  } catch (error) {
    return { body: { error: 'Can not connect to ' + url }, code: 500 }
  }
}

async function addUser(url, data) {
  try {
    const formData = new FormData()

    formData.append('userType', data.userType)
    formData.append('email', data.email)
    formData.append('password', data.password)
    formData.append('nickName', data.nickName)
    formData.append('fullName', data.fullName)
    formData.append('idCardNumber', data.idCardNumber ?? '')
    formData.append('phoneNumber', data.phoneNumber ?? '')
    formData.append('bankName', data.bankName ?? '')
    formData.append('bankAccount', data.bankAccount ?? '')

    if (data.idCardImageFront) {
      formData.append('idCardImageFront', data.idCardImageFront)
    }

    if (data.idCardImageBack) {
      formData.append('idCardImageBack', data.idCardImageBack)
    }

    let response = await fetch(url, {
      method: 'POST',
      mode: 'cors',
      body: formData,
    })

    let responseBody = {}
    try {
      responseBody = await response.json()
    } catch (e) {
      console.warn('Response is not JSON:', e)
    }

    if (response.ok) {
      return { body: responseBody, code: response.status }
    } else {
      return {
        body: {
          error:
            response.status +
            ' ' +
            (responseBody?.error ?? '') +
            ' ' +
            (responseBody?.message ?? ''),
        },
        code: response.status,
      }
    }
  } catch (error) {
    return { body: { error: 'Can not connect to ' + url }, code: 500 }
  }
}

async function updateUser(url, data) {
  try {
    const response = await fetch(url, {
      method: 'PUT',
      mode: 'cors',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeader(),
      },
      body: JSON.stringify(data),
    })

    let responseBody = {}
    try {
      responseBody = await response.json()
    } catch (e) {
      console.warn('Response is not JSON:', e)
    }

    if (response.ok) {
      return { body: responseBody, code: response.status }
    } else {
      return {
        body: {
          error:
            response.status +
            ' ' +
            (responseBody?.error ?? '') +
            ' ' +
            (responseBody?.message ?? ''),
        },
        code: response.status,
      }
    }
  } catch (error) {
    return { body: { error: 'Can not connect to ' + url }, code: 500 }
  }
}

async function userLogin(url, data) {
  try {
    let response = await fetch(url, {
      method: 'POST',
      mode: 'cors',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    })

    let responseBody = {}
    try {
      responseBody = await response.json()
    } catch (e) {
      console.warn('Response is not JSON:', e)
    }

    if (response.ok) {
      return { body: responseBody, code: response.status }
    } else {
      return {
        body: {
          error:
            response.status +
            ' ' +
            (responseBody?.error ?? '') +
            ' ' +
            (responseBody?.message ?? ''),
        },
        code: response.status,
      }
    }
  } catch (error) {
    return { body: { error: 'Can not connect to ' + url }, code: 500 }
  }
}

async function userLogout(url) {
  try {
    let response = await fetch(url, {
      method: 'POST',
      mode: 'cors',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeader(),
      },
    })

    let responseBody = null
    if (response.status !== 204) {
      responseBody = await response.json().catch(() => null)
    }

    if (response.ok) {
      return { body: responseBody, code: response.status }
    } else {
      return {
        body: {
          error:
            response.status +
            ' ' +
            (responseBody?.error ?? '') +
            ' ' +
            (responseBody?.message ?? ''),
        },
        code: response.status,
      }
    }
  } catch (error) {
    return { body: { error: 'Can not connect to ' + url }, code: 500 }
  }
}

async function getUserProfile(url) {
  let response = await fetch(url, {
    mode: 'cors',
    headers: {
      'Content-Type': 'application/json',
      ...getAuthHeader(),
    },
  })

  let responseBody = await response.json()
  if (response.ok) {
    return { body: responseBody, code: response.status }
  } else {
    return {
      body: {
        error:
          response.status +
          ' ' +
          (responseBody?.error ?? '') +
          ' ' +
          (responseBody?.message ?? ''),
      },
      code: response.status,
    }
  }
}

export {
  getItems,
  getItemById,
  addItem,
  editItem,
  addItemWithImage,
  editItemWithImage,
  deleteItemById,
  addUser,
  userLogin,
  userLogout,
  getUserProfile,
  updateUser,
}
