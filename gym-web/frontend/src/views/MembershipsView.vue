<template>
  <AppLayout>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <div>
        <h2 class="h4 mb-0">Memberships</h2>
        <small class="text-secondary">Admin-only membership plans.</small>
      </div>
      <button class="btn btn-success" @click="startCreate">Add Plan</button>
    </div>

    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div class="card border-0 shadow-sm">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0 align-middle">
            <thead class="table-light"><tr><th>ID</th><th>Name</th><th>Duration</th><th>Fee</th><th class="text-end">Actions</th></tr></thead>
            <tbody>
              <tr v-for="m in memberships" :key="m.membershipId">
                <td>{{ m.membershipId }}</td>
                <td>{{ m.name }}</td>
                <td>{{ m.durationMonths }} month(s)</td>
                <td>PKR {{ Number(m.fee).toLocaleString() }}</td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-primary me-2" @click="startEdit(m)">Edit</button>
                  <button class="btn btn-sm btn-outline-danger" @click="remove(m.membershipId)">Delete</button>
                </td>
              </tr>
              <tr v-if="memberships.length === 0"><td colspan="5" class="text-center py-4 text-secondary">No plans yet.</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm mt-4">
      <div class="card-body">
        <h3 class="h6 mb-3">{{ editingId ? 'Update Plan' : 'Create Plan' }}</h3>
        <form class="row g-3" @submit.prevent="save">
          <div class="col-md-6"><label class="form-label">Name</label><input v-model.trim="form.name" class="form-control" required /></div>
          <div class="col-md-3"><label class="form-label">Duration (months)</label><input v-model.number="form.durationMonths" min="1" type="number" class="form-control" required /></div>
          <div class="col-md-3"><label class="form-label">Fee</label><input v-model.number="form.fee" min="0" type="number" class="form-control" required /></div>
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
import { membershipsApi } from '../api/memberships'

const memberships = ref([])
const error = ref('')
const busy = ref(false)
const editingId = ref(null)

const form = reactive({ name: '', durationMonths: 1, fee: 0 })

function startCreate() {
  editingId.value = null
  Object.assign(form, { name: '', durationMonths: 1, fee: 0 })
}

function startEdit(item) {
  editingId.value = item.membershipId
  Object.assign(form, {
    name: item.name,
    durationMonths: item.durationMonths,
    fee: item.fee
  })
}

async function load() {
  error.value = ''
  try {
    memberships.value = await membershipsApi.list()
  } catch (err) {
    error.value = err?.response?.data?.error || 'Failed to load memberships.'
  }
}

async function save() {
  busy.value = true
  error.value = ''
  try {
    if (editingId.value) {
      await membershipsApi.update(editingId.value, { ...form })
    } else {
      await membershipsApi.create({ ...form })
    }
    await load()
    startCreate()
  } catch (err) {
    error.value = err?.response?.data?.error || 'Failed to save membership.'
  } finally {
    busy.value = false
  }
}

async function remove(id) {
  if (!confirm('Delete this membership plan?')) return
  try {
    await membershipsApi.remove(id)
    await load()
  } catch (err) {
    error.value = err?.response?.data?.error || 'Failed to delete membership.'
  }
}

onMounted(async () => {
  await load()
  startCreate()
})
</script>

