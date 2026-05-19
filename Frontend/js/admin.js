const modal = document.getElementById('adminModal');
const adminList = document.getElementById('adminList');
const adminForm = document.getElementById('adminForm');

function showModal() { modal.classList.add('active'); }
function hideModal() { modal.classList.remove('active'); }

async function loadAdmins() {
    const admins = await fetchData('admins');
    if (admins) {
        adminList.innerHTML = admins.map(a => `
            <tr>
                <td>${a.id}</td>
                <td>${a.username}</td>
                <td>${a.email}</td>
                <td>${a.role || 'SYSTEM_ADMIN'}</td>
                <td>
                    <button class="btn-icon" onclick="deleteAdmin(${a.id})"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `).join('');
    }
}

adminForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const data = {
        username: document.getElementById('username').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        role: document.getElementById('role').value
    };

    const result = await postData('admins', data);
    if (result) {
        alert('Admin created successfully!');
        hideModal();
        loadAdmins();
    }
});

async function deleteAdmin(id) {
    if (await showCustomConfirm('Are you sure you want to delete this admin?')) {
        const response = await fetch(`${API_URL}/admins/${id}`, { method: 'DELETE' });
        if (response.ok) loadAdmins();
    }
}

document.addEventListener('DOMContentLoaded', loadAdmins);
