<template>
  <AppLayout>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <div>
        <h2 class="h4 mb-0">Payments</h2>
        <small class="text-secondary">Track paid and pending transactions.</small>
      </div>
      <button class="btn btn-success" @click="startCreate">Record Payment</button>
    </div>

    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div class="card border-0 shadow-sm">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0 align-middle">
            <thead class="table-light"><tr><th>ID</th><th>Member</th><th>Amount</th><th>Date</th><th>Method</th><th>Status</th><th class="text-end">Actions</th></tr></thead>
            <tbody>
              <tr v-for="p in payments" :key="p.paymentId">
                <td>{{ p.paymentId }}</td>
                <td>{{ p.memberName }}</td>
                <td>PKR {{ Number(p.amount).toLocaleString() }}</td>
                <td>{{ p.paymentDate }}</td>
                <td>{{ p.paymentMethod }}</td>
                <td><span class="badge" :class="p.status === 'Paid' ? 'text-bg-success' : 'text-bg-warning'">{{ p.status }}</span></td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-primary me-2" @click="startEdit(p)">Edit</button>
                  <button class="btn btn-sm btn-outline-danger" @click="remove(p.paymentId)">Delete</button>
                </td>
              </tr>
              <tr v-if="payments.length === 0"><td colspan="7" class="text-center py-4 text-secondary">No payments yet.</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm mt-4">
      <div class="card-body">
        <h3 class="h6 mb-3">{{ editingId ? 'Update Payment' : 'Create Payment' }}</h3>
        <form class="row g-3" @submit.prevent="save">
          <div class="col-md-4"><label class="form-label">Member</label>
            <select v-model.number="form.memberId" class="form-select" required>
              <option v-for="m in members" :key="m.memberId" :value="m.memberId">{{ m.firstName }} {{ m.lastName }}</option>
            </select>
          </div>
          <div class="col-md-4"><label class="form-label">Amount</label><input v-model.number="form.amount" type="number" min="0" class="form-control" required /></div>
          <div class="col-md-4"><label class="form-label">Date</label><input v-model="form.paymentDate" type="date" class="form-control" required /></div>
          <div class="col-md-6"><label class="form-label">Method</label><select v-model="form.paymentMethod" class="form-select" required><option>Cash</option><option>Card</option><option>Bank</option></select></div>
          <div class="col-md-6"><label class="form-label">Status</label><select v-model="form.status" class="form-select" required><option>Paid</option><option>Pending</option></select></div>

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
import { paymentsApi } from '../api/payments'
import { membersApi } from '../api/members'

const payments = ref([])
const members = ref([])
const error = ref('')
const busy = ref(false)
const editingId = ref(null)

const form = reactive({
  memberId: null,
  amount: 0,
  paymentDate: new Date().toISOString().slice(0, 10),
  paymentMethod: 'Cash',
  status: 'Paid'
})

function startCreate() {
  editingId.value = null
  Object.assign(form, {
    memberId: members.value[0]?.memberId ?? null,
    amount: 0,
    paymentDate: new Date().toISOString().slice(0, 10),
    paymentMethod: 'Cash',
    status: 'Paid'
  })
}

function startEdit(item) {
  editingId.value = item.paymentId
  Object.assign(form, {
    memberId: item.memberId,
    amount: item.amount,
    paymentDate: item.paymentDate,
    paymentMethod: item.paymentMethod,
    status: item.status
  })
}

async function load() {
  error.value = ''
  try {
    const [paymentsData, membersData] = await Promise.all([
      paymentsApi.list(),
      membersApi.list()
    ])
    payments.value = paymentsData
    members.value = membersData
    if (!editingId.value) startCreate()
  } catch (err) {
    error.value = err?.response?.data?.error || 'Failed to load payments.'
  }
}

async function save() {
  busy.value = true
  error.value = ''
  try {
    if (editingId.value) {
      await paymentsApi.update(editingId.value, { ...form })
    } else {
      await paymentsApi.create({ ...form })
    }
    await load()
    startCreate()
  } catch (err) {
    error.value = err?.response?.data?.error || 'Failed to save payment.'
  } finally {
    busy.value = false
  }
}

async function remove(id) {
  if (!confirm('Delete this payment?')) return
  try {
    await paymentsApi.remove(id)
    await load()
  } catch (err) {
    error.value = err?.response?.data?.error || 'Failed to delete payment.'
  }
}

onMounted(load)
</script>

