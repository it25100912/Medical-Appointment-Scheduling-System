const modal = document.getElementById('recordModal');
const recordList = document.getElementById('recordList');
const recordForm = document.getElementById('recordForm');

function showModal() { modal.classList.add('active'); }
function hideModal() { modal.classList.remove('active'); }

async function loadRecords() {
    const records = await fetchData('medical-records');
    if (records) {
        recordList.innerHTML = records.map(r => `
            <tr>
                <td>${r.id}</td>
                <td>Patient #${r.patientId}</td>
                <td>${r.recordType || 'General'}</td>
                <td>${r.description || r.notes || ''}</td>
                <td>${r.createdAt || 'N/A'}</td>
                <td>
                    <button class="btn-icon"><i class="fas fa-eye"></i></button>
                </td>
            </tr>
        `).join('');
    }
}

recordForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const data = {
        patientId: parseInt(document.getElementById('patientId').value),
        recordType: document.getElementById('recordType').value,
        description: document.getElementById('description').value
    };

    const result = await postData('medical-records', data);
    if (result) {
        alert('Record saved!');
        hideModal();
        loadRecords();
    }
});

document.addEventListener('DOMContentLoaded', loadRecords);
