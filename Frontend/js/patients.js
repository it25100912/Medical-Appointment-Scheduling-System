window.allPatients = [];

async function loadPatients() {
    try {
        window.allPatients = await apiFetch('/patients');
        renderPatients();
    } catch (error) {
        console.error("Patient Load Error:", error);
        showToast("Failed to load clinical records", "error");
    }
}

function renderPatients() {
    const tbody = document.getElementById('patient-roster-body');
    const searchInput = document.getElementById('system-search');
    if (!tbody || !searchInput) return;

    const searchTerm = searchInput.value.toLowerCase().trim();

    const filtered = window.allPatients.filter(p => {
        const name = (p.name || '').toLowerCase();
        const nic = (p.nic || '').toLowerCase();
        const phone = (p.phone || '').toLowerCase();
        const email = (p.email || '').toLowerCase();

        return name.includes(searchTerm) || 
               nic.includes(searchTerm) || 
               phone.includes(searchTerm) ||
               email.includes(searchTerm);
    });

    if (filtered.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--text-muted); padding: 3rem; font-weight: 600;">
            ${searchTerm ? 'No clinical matches found for "' + searchTerm + '"' : 'No clinical records found.'}
        </td></tr>`;
        return;
    }

    tbody.innerHTML = filtered.map(p => `
        <tr>
            <td>
                <div class="patient-identity">
                    <div class="patient-avatar">${p.name.split(' ').map(n => n[0]).join('').substring(0,2)}</div>
                    <div>
                        <p class="user-name">${p.name}</p>
                        <p class="user-role">Reg ID: #${p.id.toString().slice(-4)}</p>
                    </div>
                </div>
            </td>
            <td><span class="info-tag">${p.nic}</span></td>
            <td>
                <div class="flex-col gap-1">
                    <span>${p.phone}</span>
                    <span style="color: var(--text-muted); font-size: 0.85rem;">${p.email}</span>
                </div>
            </td>
            <td><span class="status-badge PENDING">${p.bloodGroup || 'N/A'}</span></td>
            <td><span class="status-badge CONFIRMED">Active</span></td>
            <td>
                <div class="action-btns">
                    <button class="btn-action-light" onclick="editPatient(${p.id})"><i class="fas fa-edit"></i></button>
                    <button class="btn-action-light delete" onclick="deletePatient(${p.id})"><i class="fas fa-trash-alt"></i></button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Live Search Handler
const searchInput = document.getElementById('system-search');
if (searchInput) {
    searchInput.addEventListener('input', renderPatients);
}

let editingId = null;

function openOnboardModal() {
    editingId = null;
    document.getElementById('patient-form').reset();
    document.getElementById('pat-password').required = true;
    document.getElementById('patientModal').querySelector('h2').innerText = 'Register New Patient';
    document.getElementById('patientModal').querySelector('button[type="submit"]').innerText = 'Complete Registration';
    document.getElementById('patientModal').classList.add('active');
}

async function editPatient(id) {
    try {
        const p = await apiFetch(`/patients/${id}`);
        editingId = id;
        
        document.getElementById('pat-name').value = p.name;
        document.getElementById('pat-nic').value = p.nic;
        document.getElementById('pat-phone').value = p.phone;
        document.getElementById('pat-email').value = p.email;
        document.getElementById('pat-blood').value = p.bloodGroup || '';
        document.getElementById('pat-dob').value = p.dateOfBirth || '';
        document.getElementById('pat-address').value = p.address || '';
        document.getElementById('pat-type').value = p.patientType || 'OUTPATIENT';
        document.getElementById('pat-password').value = '';
        document.getElementById('pat-password').required = false;

        document.getElementById('patientModal').querySelector('h2').innerText = 'Update Patient Record';
        document.getElementById('patientModal').querySelector('button[type="submit"]').innerText = 'Save Changes';
        document.getElementById('patientModal').classList.add('active');
    } catch (error) {
        showToast('Failed to load patient details', 'error');
    }
}

function closeModal() {
    document.getElementById('patientModal').classList.remove('active');
}

document.getElementById('patient-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = e.target.querySelector('button[type="submit"]');
    const originalText = btn.innerHTML;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> processing...';
    btn.disabled = true;

    const data = {
        name: document.getElementById('pat-name').value,
        nic: document.getElementById('pat-nic').value,
        phone: document.getElementById('pat-phone').value,
        email: document.getElementById('pat-email').value,
        bloodGroup: document.getElementById('pat-blood').value,
        dateOfBirth: document.getElementById('pat-dob').value,
        address: document.getElementById('pat-address').value,
        patientType: document.getElementById('pat-type').value,
        password: document.getElementById('pat-password').value
    };

    try {
        const method = editingId ? 'PUT' : 'POST';
        const url = editingId ? `/patients/${editingId}` : '/patients';
        await apiFetch(url, method, data);
        
        showToast(editingId ? 'Patient record updated' : 'Patient registered successfully', 'success');
        closeModal();
        loadPatients();
        e.target.reset();
    } catch (error) {
        showToast(error.message || 'Registration failed', 'error');
    } finally {
        btn.innerHTML = originalText;
        btn.disabled = false;
    }
});

async function deletePatient(id) {
    if (await showCustomConfirm('Are you sure you want to delete this patient record?')) {
        try {
            await apiFetch(`/patients/${id}`, 'DELETE');
            showToast('Patient record deleted', 'success');
            loadPatients();
        } catch (error) {
            showToast(error.message || 'Deletion failed', 'error');
        }
    }
}

document.addEventListener('DOMContentLoaded', loadPatients);
