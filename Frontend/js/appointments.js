document.addEventListener('DOMContentLoaded', () => {
    loadAppointments();

    const bookingForm = document.querySelector('#appt-new button');
    if (bookingForm) {
        bookingForm.addEventListener('click', async () => {
            const apptData = {
                patientId: document.querySelector('input[placeholder="Search Patient ID..."]').value,
                doctorId: 1, // Default for demo
                date: new Date().toISOString().split('T')[0],
                time: "10:30:00",
                reason: "General Consultation",
                status: "CONFIRMED"
            };

            const result = await apiRequest('appointments', 'POST', apptData);
            if (result) {
                showToast('Appointment booked successfully!');
                switchView('appt-list', document.querySelector('.sub-nav-item'));
                loadAppointments();
            }
        });
    }
});

async function loadAppointments() {
    const container = document.querySelector('#appt-list tbody');
    if (!container) return;

    const appointments = await apiRequest('appointments');
    if (!appointments) return;

    container.innerHTML = '';
    appointments.forEach(app => {
        const row = `
            <tr>
                <td style="padding-left: 1.5rem;">
                    <div class="user-info">
                        <div class="avatar-sm">${app.patientName?.charAt(0) || 'P'}</div>
                        <span>${app.patientName || 'Unknown Patient'}</span>
                    </div>
                </td>
                <td>Dr. Sarah Jenkins</td>
                <td style="font-weight: 700;">${app.time}</td>
                <td><span class="table-badge status-${app.status.toLowerCase()}">${app.status}</span></td>
                <td><button class="btn-secondary" style="font-size: 0.75rem;" onclick="viewAppointment(${app.id})">Details</button></td>
            </tr>
        `;
        container.innerHTML += row;
    });
}

async function viewAppointment(id) {
    const app = await apiRequest(`appointments/${id}`);
    if (!app) return;
    
    const detailsView = document.getElementById('appt-details');
    detailsView.querySelector('p[style*="font-size: 1.25rem"]').innerText = app.patientName;
    switchView('appt-details');
}
