<template>
  <AppLayout>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <div>
        <h2 class="h4 mb-0">Members</h2>
        <small class="text-secondary">Create, update and manage gym members.</small>
      </div>
      <button class="btn btn-success" @click="startCreate">Add Member</button>
    </div>

    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div class="card border-0 shadow-sm">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0 align-middle">
            <thead class="table-light">
              <tr>
                <th>ID</th><th>Name</th><th>Phone</th><th>Status</th><th>Trainer</th><th>Membership</th><th class="text-end">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="m in members" :key="m.memberId">
                <td>{{ m.memberId }}</td>
                <td>{{ m.firstName }} {{ m.lastName }}</td>
                <td>{{ m.phone }}</td>
                <td><span class="badge" :class="m.status === 'Active' ? 'text-bg-success' : 'text-bg-secondary'">{{ m.status }}</span></td>
                <td>{{ m.trainerName }}</td>
                <td>{{ m.membershipName }}</td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-primary me-2" @click="startEdit(m)">Edit</button>
                  <button class="btn btn-sm btn-outline-danger" @click="remove(m.memberId)">Delete</button>
                </td>
              </tr>
              <tr v-if="members.length === 0">
                <td colspan="7" class="text-center py-4 text-secondary">No members yet.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm mt-4">
      <div class="card-body">
        <h3 class="h6 mb-3">{{ editingId ? 'Update Member' : 'Create Member' }}</h3>
        <form class="row g-3" @submit.prevent="save">
          <div class="col-md-6"><label class="form-label">First Name</label><input v-model.trim="form.firstName" class="form-control" required /></div>
          <div class="col-md-6"><label class="form-label">Last Name</label><input v-model.trim="form.lastName" class="form-control" required /></div>
          <div class="col-md-4"><label class="form-label">Gender</label><select v-model="form.gender" class="form-select" required><option>Male</option><option>Female</option></select></div>
          <div class="col-md-4"><label class="form-label">Age</label><input v-model.number="form.age" type="number" min="1" class="form-control" required /></div>
          <div class="col-md-4"><label class="form-label">Phone</label><input v-model.trim="form.phone" class="form-control" required /></div>
          <div class="col-md-6"><label class="form-label">Email</label><input v-model.trim="form.email" type="email" class="form-control" required /></div>
          <div class="col-md-6"><label class="form-label">Join Date</label><input v-model="form.joinDate" type="date" class="form-control" required /></div>
          <div class="col-md-4"><label class="form-label">Trainer</label>
            <select v-model.number="form.trainerId" class="form-select" required>
              <option v-for="t in trainers" :key="t.trainerId" :value="t.trainerId">{{ t.firstName }} {{ t.lastName }}</option>
            </select>
          </div>
          <div class="col-md-4"><label class="form-label">Membership</label>
            <select v-model.number="form.membershipId" class="form-select" required>
              <option v-for="s in memberships" :key="s.membershipId" :value="s.membershipId">{{ s.name }}</option>
            </select>
          </div>
          <div class="col-md-4"><label class="form-label">Status</label><select v-model="form.status" class="form-select" required><option>Active</option><option>Inactive</option></select></div>

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
import { membersApi } from '../api/members'
import { trainersApi } from '../api/trainers'
import { membershipsApi } from '../api/memberships'

const members = ref([])
const trainers = ref([])
const memberships = ref([])
const error = ref('')
const busy = ref(false)
const editingId = ref(null)

const form = reactive({
  firstName: '',
  lastName: '',
  gender: 'Male',
  age: 18,
  phone: '',
  email: '',
  joinDate: new Date().toISOString().slice(0, 10),
  trainerId: null,
  status: 'Active',
  membershipId: null
})

function startCreate() {
  editingId.value = null
  Object.assign(form, {
    firstName: '',
    lastName: '',
    gender: 'Male',
    age: 18,
    phone: '',
    email: '',
    joinDate: new Date().toISOString().slice(0, 10),
    trainerId: trainers.value[0]?.trainerId ?? null,
    status: 'Active',
    membershipId: memberships.value[0]?.membershipId ?? null
  })
}

function startEdit(m) {
  editingId.value = m.memberId
  Object.assign(form, {
    firstName: m.firstName,
    lastName: m.lastName,
    gender: m.gender,
    age: m.age,
    phone: m.phone,
    email: m.email,
    joinDate: m.joinDate,
    trainerId: m.trainerId,
    status: m.status,
    membershipId: m.membershipId
  })
}

async function load() {
  error.value = ''
  try {
    const [membersData, trainersData, membershipsData] = await Promise.all([
      membersApi.list(),
      trainersApi.list(),
      membershipsApi.list()
    ])
    members.value = membersData
    trainers.value = trainersData
    memberships.value = membershipsData
    if (!editingId.value) startCreate()
  } catch (err) {
    error.value = err?.response?.data?.error || 'Failed to load members.'
  }
}

async function save() {
  busy.value = true
  error.value = ''
  try {
    const payload = { ...form }
    if (editingId.value) {
      await membersApi.update(editingId.value, payload)
    } else {
      await membersApi.create(payload)
    }
    await load()
    startCreate()
  } catch (err) {
    error.value = err?.response?.data?.error || 'Failed to save member.'
  } finally {
    busy.value = false
  }
}

async function remove(id) {
  if (!confirm('Delete this member?')) return
  try {
    await membersApi.remove(id)
    await load()
  } catch (err) {
    error.value = err?.response?.data?.error || 'Failed to delete member.'
  }
}

onMounted(load)
</script>

