<template>
  <AppLayout>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <div>
        <h2 class="h4 mb-0">Trainers</h2>
        <small class="text-secondary">Admin-only trainer roster management.</small>
      </div>
      <button class="btn btn-success" @click="startCreate">Add Trainer</button>
    </div>

    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div class="card border-0 shadow-sm">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0 align-middle">
            <thead class="table-light"><tr><th>ID</th><th>Name</th><th>Specialization</th><th>Phone</th><th>Email</th><th>Hired</th><th class="text-end">Actions</th></tr></thead>
            <tbody>
              <tr v-for="t in trainers" :key="t.trainerId">
                <td>{{ t.trainerId }}</td>
                <td>{{ t.firstName }} {{ t.lastName }}</td>
                <td>{{ t.specialization }}</td>
                <td>{{ t.phone }}</td>
                <td>{{ t.email }}</td>
                <td>{{ t.hireDate }}</td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-primary me-2" @click="startEdit(t)">Edit</button>
                  <button class="btn btn-sm btn-outline-danger" @click="remove(t.trainerId)">Delete</button>
                </td>
              </tr>
              <tr v-if="trainers.length === 0"><td colspan="7" class="text-center py-4 text-secondary">No trainers yet.</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm mt-4">
      <div class="card-body">
        <h3 class="h6 mb-3">{{ editingId ? 'Update Trainer' : 'Create Trainer' }}</h3>
        <form class="row g-3" @submit.prevent="save">
          <div class="col-md-6"><label class="form-label">First Name</label><input v-model.trim="form.firstName" class="form-control" required /></div>
          <div class="col-md-6"><label class="form-label">Last Name</label><input v-model.trim="form.lastName" class="form-control" required /></div>
          <div class="col-md-3"><label class="form-label">Gender</label><select v-model="form.gender" class="form-select" required><option>Male</option><option>Female</option></select></div>
          <div class="col-md-3"><label class="form-label">Age</label><input v-model.number="form.age" type="number" min="1" class="form-control" required /></div>
          <div class="col-md-3"><label class="form-label">Phone</label><input v-model.trim="form.phone" class="form-control" required /></div>
          <div class="col-md-3"><label class="form-label">Hire Date</label><input v-model="form.hireDate" type="date" class="form-control" required /></div>
          <div class="col-md-6"><label class="form-label">Email</label><input v-model.trim="form.email" type="email" class="form-control" required /></div>
          <div class="col-md-6"><label class="form-label">Specialization</label><input v-model.trim="form.specialization" class="form-control" required /></div>

          <div class="col-12 d-flex gap-2 justify-content-end">
            <button v-if="editingId" type="button" class="btn btn-outline-secondary" @click="startCreate">Cancel</button>
            <button class="btn btn-success" :disabled="busy">{{ busy ? 'Saving...' : 'Save' }}</button>
          </div>
        </form>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import AppLayout from '../components/AppLayout.vue'
import { trainersApi } from '../api/trainers'

const trainers = ref([])
const error = ref('')
const busy = ref(false)
const editingId = ref(null)

const form = reactive({
  firstName: '',
  lastName: '',
  gender: 'Male',
  age: 25,
  phone: '',
  email: '',
  specialization: '',
  hireDate: new Date().toISOString().slice(0, 10)
})

function startCreate() {
  editingId.value = null
  Object.assign(form, {
    firstName: '',
    lastName: '',
    gender: 'Male',
    age: 25,
    phone: '',
    email: '',
    specialization: '',
    hireDate: new Date().toISOString().slice(0, 10)
  })
}

function startEdit(item) {
  editingId.value = item.trainerId
  Object.assign(form, {
    firstName: item.firstName,
    lastName: item.lastName,
    gender: item.gender,
    age: item.age,
    phone: item.phone,
    email: item.email,
    specialization: item.specialization,
    hireDate: item.hireDate
  })
}

async function load() {
  error.value = ''
  try {
    trainers.value = await trainersApi.list()
  } catch (err) {
    error.value = err?.response?.data?.error || 'Failed to load trainers.'
  }
}

async function save() {
  busy.value = true
  error.value = ''
  try {
    if (editingId.value) {
      await trainersApi.update(editingId.value, { ...form })
    } else {
      await trainersApi.create({ ...form })
    }
    await load()
    startCreate()
  } catch (err) {
    error.value = err?.response?.data?.error || 'Failed to save trainer.'
  } finally {
    busy.value = false
  }
}

async function remove(id) {
  if (!confirm('Delete this trainer?')) return
  try {
    await trainersApi.remove(id)
    await load()
  } catch (err) {
    error.value = err?.response?.data?.error || 'Failed to delete trainer.'
  }
}

onMounted(async () => {
  await load()
  startCreate()
})
</script>

