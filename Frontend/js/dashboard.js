document.addEventListener('DOMContentLoaded', async () => {
    // Initial data load
    loadDashboardData();

    // Refresh button functionality
    const refreshBtn = document.querySelector('.btn-blue');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', () => {
            refreshBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Refreshing...';
            loadDashboardData().finally(() => {
                refreshBtn.innerHTML = '<i class="fas fa-sync-alt"></i> Refresh Data';
            });
        });
    }

    // Search box interaction (just for show)
    const searchInput = document.querySelector('.search-box input');
    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            alert('Searching for: ' + searchInput.value);
        }
    });
});

async function loadDashboardData() {
    try {
        // Fetch real data from backend
        const doctors = await fetchData('doctors');
        const appointments = await fetchData('appointments');
        const patients = await fetchData('patients');

        // Update Stats
        if (doctors) {
            document.getElementById('activeDoctors').textContent = doctors.length;
        }

        if (appointments) {
            // Filter appointments for today
            const today = new Date().toISOString().split('T')[0];
async function loadDashboardStats() {
    // In a real app, these would come from a dedicated stats endpoint
    // For now, we fetch from respective modules to count
    const doctors = await apiRequest('doctors');
    const appointments = await apiRequest('appointments');
    
    if (doctors) document.getElementById('activeDoctors').innerText = doctors.length;
    if (appointments) {
        const today = new Date().toISOString().split('T')[0];
        const todayApps = appointments.filter(a => a.date === today);
        document.getElementById('dailyApps').innerText = todayApps.length;
    }
}

async function loadActivityLogs() {
    const container = document.getElementById('activityLogs');
    if (!container) return;

    // Fetch recent appointments as activity
    const appointments = await apiRequest('appointments');
    if (!appointments) return;

    container.innerHTML = '';
    appointments.slice(0, 5).forEach(app => {
        const row = `
            <tr>
                <td>
                    <div class="user-info">
                        <div class="avatar-sm">${app.patientName.charAt(0)}</div>
                        <span style="font-weight: 700;">${app.patientName}</span>
                    </div>
                </td>
                <td>${app.reason}</td>
                <td>${app.time}</td>
                <td><span class="status-${app.status.toLowerCase()}">${app.status}</span></td>
                <td>Local Node</td>
            </tr>
        `;
        container.innerHTML += row;
    });
}
