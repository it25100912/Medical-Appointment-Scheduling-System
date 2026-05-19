const modal = document.getElementById('billingModal');
const billingList = document.getElementById('billingList');
const billingForm = document.getElementById('billingForm');

function showModal() { modal.classList.add('active'); }
function hideModal() { modal.classList.remove('active'); }

async function loadBilling() {
    const bills = await fetchData('billings');
    if (bills) {
        billingList.innerHTML = bills.map(b => `
            <tr>
                <td>${b.id}</td>
                <td>Patient #${b.patientId}</td>
                <td>LKR ${b.totalAmount}</td>
                <td><span class="badge ${b.status?.toLowerCase()}">${b.status || 'Paid'}</span></td>
                <td>${b.billingDate || 'N/A'}</td>
                <td>
                    <button class="btn-icon"><i class="fas fa-print"></i></button>
                </td>
            </tr>
        `).join('');
    }
}

billingForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const data = {
        patientId: parseInt(document.getElementById('patientId').value),
        appointmentId: parseInt(document.getElementById('appointmentId').value),
        totalAmount: parseFloat(document.getElementById('amount').value),
        paymentMethod: document.getElementById('paymentMethod').value
    };

    const result = await postData('billings', data);
    if (result) {
        alert('Invoice generated and payment processed!');
        hideModal();
        loadBilling();
    }
});

document.addEventListener('DOMContentLoaded', loadBilling);
