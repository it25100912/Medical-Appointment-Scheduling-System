window.allDoctors = [];

async function loadDoctors() {
    try {
        window.allDoctors = await apiFetch('/doctors');
        renderDoctors();
    } catch (error) {
        showToast('Failed to load roster', 'error');
    }
}

function renderDoctors() {
    const tbody = document.getElementById('doctor-roster-body');
    const searchInput = document.getElementById('system-search');
    if (!tbody || !searchInput) return;
    
    const searchTerm = searchInput.value.toLowerCase().trim();
    
    const filtered = window.allDoctors.filter(d => {
        const name = (d.name || '').toLowerCase();
        const spec = (d.specialization || '').toLowerCase();
        const email = (d.email || '').toLowerCase();
        const phone = (d.phone || '').toLowerCase();
        
        return name.includes(searchTerm) || 
               spec.includes(searchTerm) || 
               email.includes(searchTerm) ||
               phone.includes(searchTerm);
    });

    if (filtered.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: var(--text-muted); padding: 3rem; font-weight: 600;">
            ${searchTerm ? 'No clinical matches found for "' + searchTerm + '"' : 'No providers onboarded yet.'}
        </td></tr>`;
        return;
    }

    tbody.innerHTML = filtered.map(d => `
        <tr>
            <td>
                <div class="patient-identity">
                    <div class="doc-avatar">${d.name.split(' ').map(n => n[0]).join('').substring(0,2)}</div>
                    <div>
                        <p class="user-name">${d.name}</p>
                        <p class="user-role">ID: #${d.id.toString().slice(-4)}</p>
                    </div>
                </div>
            </td>
            <td><span class="info-tag">${d.specialization}</span></td>
            <td><span class="status-badge PENDING">${d.experience} Years</span></td>
            <td><span class="status-badge CONFIRMED">Available</span></td>
            <td>
                <div class="action-btns">
                    <button class="btn-action-light" onclick="editDoctor(${d.id})"><i class="fas fa-edit"></i></button>
                    <button class="btn-action-light delete" onclick="deleteDoctor(${d.id})"><i class="fas fa-trash-alt"></i></button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Live Search Handler
const searchInput = document.getElementById('system-search');
if (searchInput) {
    searchInput.addEventListener('input', renderDoctors);
}

let editingId = null;

function openOnboardModal() {
    editingId = null;
    document.getElementById('doctor-form').reset();
    document.getElementById('doc-password').required = true;
    document.getElementById('doctorModal').querySelector('h2').innerText = 'Register New Doctor';
    document.getElementById('doctorModal').querySelector('button[type="submit"]').innerText = 'Register Provider';
    document.getElementById('doctorModal').classList.add('active');
}

async function editDoctor(id) {
    try {
        const d = await apiFetch(`/doctors/${id}`);
        editingId = id;
        
        document.getElementById('doc-name').value = d.name;
        document.getElementById('doc-email').value = d.email;
        document.getElementById('doc-phone').value = d.phone;
        document.getElementById('doc-spec').value = d.specialization || '';
        document.getElementById('doc-license').value = d.licenseNumber || '';
        document.getElementById('doc-exp').value = d.experience || '';
        document.getElementById('doc-fee').value = d.consultationFee || '';
        document.getElementById('doc-password').value = '';
        document.getElementById('doc-password').required = false;

        document.getElementById('doctorModal').querySelector('h2').innerText = 'Update Doctor Record';
        document.getElementById('doctorModal').querySelector('button[type="submit"]').innerText = 'Save Changes';
        document.getElementById('doctorModal').classList.add('active');
    } catch (error) {
        showToast('Failed to load provider details', 'error');
    }
}

function closeModal() {
    document.getElementById('doctorModal').classList.remove('active');
}

document.getElementById('doctor-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = e.target.querySelector('button[type="submit"]');
    const originalText = btn.innerHTML;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> processing...';
    btn.disabled = true;

    const data = {
        name: document.getElementById('doc-name').value,
        email: document.getElementById('doc-email').value,
        phone: document.getElementById('doc-phone').value,
        specialization: document.getElementById('doc-spec').value,
        licenseNumber: document.getElementById('doc-license').value,
        experience: parseInt(document.getElementById('doc-exp').value) || 0,
        consultationFee: parseFloat(document.getElementById('doc-fee').value) || 0,
        password: document.getElementById('doc-password').value,
        doctorType: 'GENERAL'
    };

    try {
        const method = editingId ? 'PUT' : 'POST';
        const url = editingId ? `/doctors/${editingId}` : '/doctors';
        await apiFetch(url, method, data);
        
        showToast(editingId ? 'Provider record updated' : 'Doctor registered successfully', 'success');
        closeModal();
        loadDoctors();
        e.target.reset();
    } catch (error) {
        showToast(error.message || 'Registration failed', 'error');
    } finally {
        btn.innerHTML = originalText;
        btn.disabled = false;
    }
});

async function deleteDoctor(id) {
    if (await showCustomConfirm('Are you sure you want to delete this provider record?')) {
        try {
            await apiFetch(`/doctors/${id}`, 'DELETE');
            showToast('Provider record deleted', 'success');
            loadDoctors();
        } catch (error) {
            showToast(error.message || 'Deletion failed', 'error');
        }
    }
}

document.addEventListener('DOMContentLoaded', loadDoctors);
