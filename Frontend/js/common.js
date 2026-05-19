// Immediate Global Authentication Guard
(function() {
    const publicPages = ['/login.html', '/signup.html'];
    const path = window.location.pathname;
    const user = localStorage.getItem('user');
    
    if (!user && !publicPages.some(p => path.endsWith(p))) {
        window.location.href = 'login.html';
    } else if (user && publicPages.some(p => path.endsWith(p))) {
        try {
            const userData = JSON.parse(user);
            if (userData.role === 'PATIENT' || userData.role === 'DOCTOR') {
                window.location.href = 'appointments.html';
            } else {
                window.location.href = 'index.html';
            }
        } catch (e) {
            localStorage.clear();
            window.location.href = 'login.html';
        }
    }
})();

const API_BASE_URL = 'http://localhost:8080/api';


const apiFetch = async (endpoint, methodOrOptions = 'GET', body = null) => {
    let options = {};
    if (typeof methodOrOptions === 'object') {
        options = methodOrOptions;
    } else {
        options.method = methodOrOptions;
        if (body) {
            options.body = JSON.stringify(body);
        }
    }

    const token = localStorage.getItem('token');
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            ...options,
            headers: headers
        });

        const contentType = response.headers.get("content-type");
        const isJson = contentType && contentType.includes("application/json");

        if (!response.ok) {
            const errorData = isJson ? await response.json() : { message: await response.text() };
            throw new Error(errorData.message || errorData.error || 'Something went wrong');
        }

        if (response.status === 204) return null;
        return isJson ? await response.json() : await response.text();
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
};

// Global view switching for high-fidelity sections
function switchView(viewId, element) {
    document.querySelectorAll('.view-section').forEach(s => s.classList.remove('active'));
    if (element) {
        document.querySelectorAll('.sub-nav-item').forEach(n => n.classList.remove('active'));
        element.classList.add('active');
    }
    const target = document.getElementById(viewId);
    if (target) target.classList.add('active');
}

// Format Date
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
}

// Toast Notifications
function showToast(message, type = 'success') {
    let toast = document.querySelector('.billing-toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.className = 'billing-toast';
        document.body.appendChild(toast);
    }
    toast.innerHTML = `
        <div class="toast-content">
            <div class="icon-circle" style="background: ${type === 'danger' || type === 'error' ? '#ef4444' : '#059669'}">
                <i class="fas fa-${type === 'danger' || type === 'error' ? 'times' : 'check'}"></i>
            </div>
            <p>${message}</p>
        </div>
        <button class="btn-dismiss" onclick="this.parentElement.remove()">Dismiss</button>
    `;
    toast.style.display = 'flex';
    setTimeout(() => {
        if (toast.parentElement) toast.remove();
    }, 5000);
}

// Aliases for compatibility
const showNotification = showToast;
const apiRequest = apiFetch;
// Search Autocomplete Logic
function initGlobalSearch() {
    const searchInput = document.getElementById('system-search');
    if (!searchInput) return;

    // Inject dropdown if not exists
    let dropdown = document.getElementById('search-results-dropdown');
    if (!dropdown) {
        dropdown = document.createElement('div');
        dropdown.id = 'search-results-dropdown';
        searchInput.parentElement.appendChild(dropdown);
    }

    searchInput.addEventListener('input', (e) => {
        const term = e.target.value.toLowerCase().trim();
        if (term.length < 1) {
            dropdown.classList.remove('active');
            return;
        }

        // Detect which global list to use
        let list = [];
        let type = '';
        if (window.allPatients) { list = window.allPatients; type = 'Patient'; }
        else if (window.allDoctors) { list = window.allDoctors; type = 'Doctor'; }
        else if (window.allAppts || window.allAppointments) { 
            list = window.allAppts || window.allAppointments; 
            type = 'Appointment'; 
        }
        else if (window.allBills || window.allBilling) { 
            list = window.allBills || window.allBilling; 
            type = 'Invoice'; 
        }
        else if (window.allRecords) { 
            list = window.allRecords; 
            type = 'Record'; 
        }
        else if (window.allAdmins) { list = window.allAdmins; type = 'Admin'; }

        const matches = list.filter(item => {
            const name = (item.name || item.username || item.invoiceNumber || item.diagnosis || item.patientName || '').toLowerCase();
            return name.includes(term);
        }).slice(0, 5);

        if (matches.length > 0) {
            dropdown.innerHTML = matches.map(item => {
                const name = item.name || item.username || item.invoiceNumber || item.diagnosis || item.patientName || 'Record';
                const meta = item.id ? `#${item.id}` : type;
                return `
                    <div class="search-result-item" onclick="jumpToResult('${name}')">
                        <i class="fas fa-search"></i>
                        <div>
                            <span class="name">${name}</span>
                            <span class="meta">${type} • ${meta}</span>
                        </div>
                    </div>
                `;
            }).join('');
            dropdown.classList.add('active');
        } else {
            dropdown.classList.remove('active');
        }
    });

    // Close on click outside
    document.addEventListener('click', (e) => {
        if (!searchInput.contains(e.target) && !dropdown.contains(e.target)) {
            dropdown.classList.remove('active');
        }
    });
}

function jumpToResult(name) {
    const dropdown = document.getElementById('search-results-dropdown');
    const searchInput = document.getElementById('system-search');
    
    // Fill search input
    searchInput.value = name;
    
    // Trigger existing filter logic (which listens to 'input')
    searchInput.dispatchEvent(new Event('input'));
    
    dropdown.classList.remove('active');

    // Find row and scroll
    setTimeout(() => {
        const rows = document.querySelectorAll('tr');
        for (let row of rows) {
            if (row.innerText.includes(name)) {
                row.scrollIntoView({ behavior: 'smooth', block: 'center' });
                row.style.background = 'rgba(37, 99, 235, 0.1)';
                setTimeout(() => row.style.background = '', 2000);
                break;
            }
        }
    }, 100);
}

function checkAuth() {
    const publicPages = ['/login.html', '/signup.html'];
    const path = window.location.pathname;
    const user = localStorage.getItem('user');
    
    // If not logged in and not on a public page
    if (!user && !publicPages.some(p => path.endsWith(p))) {
        window.location.href = 'login.html';
    }

    // Update user profile in header if exists
    if (user) {
        const userData = JSON.parse(user);
        const nameEl = document.querySelector('.user-name');
        const roleEl = document.querySelector('.user-role');
        const avatarImg = document.querySelector('.user-avatar');
        
        if (nameEl) nameEl.innerText = userData.name || userData.email;
        if (roleEl) roleEl.innerText = userData.role;
        if (avatarImg) avatarImg.src = `https://ui-avatars.com/api/?name=${userData.name || 'User'}&background=4361ee&color=white`;

        // Restriction logic
        const path = window.location.pathname;
        const isAdminPage = path.endsWith('admin.html');
        const isStaffPage = ['doctors.html', 'billing.html'].some(p => path.endsWith(p));
        
        if (userData.role === 'PATIENT') {
            // Patients should only see Profile, Appointments, Invoices, Medical Records and Logout
            const restrictedPages = ['admin.html', 'patients.html', 'doctors.html', 'index.html'];
            if (restrictedPages.some(p => path.endsWith(p))) {
                window.location.href = 'appointments.html';
            }

            const sidebarLinks = document.querySelector('.nav-links');
            if (sidebarLinks) {
                sidebarLinks.innerHTML = `
                    <a href="appointments.html" class="nav-item"><i class="fas fa-calendar-alt"></i> <span>Appointments</span></a>
                    <a href="billing.html" class="nav-item"><i class="fas fa-file-invoice-dollar"></i> <span>Invoices</span></a>
                    <a href="records.html" class="nav-item"><i class="fas fa-file-medical"></i> <span>Medical Records</span></a>
                    <a href="#" class="nav-item" onclick="logout()"><i class="fas fa-sign-out-alt"></i> <span>Logout</span></a>
                `;

                // mark active link
                document.querySelectorAll('.nav-links .nav-item').forEach(a => {
                    const href = a.getAttribute('href');
                    if (href) {
                        if (path.endsWith(href) || (href === 'appointments.html' && path.endsWith('book-appointment.html'))) {
                            a.classList.add('active');
                        }
                    }
                });
            }
        
        } else if (userData.role === 'DOCTOR') {
            // Doctors should only see Profile, Appointments, Medical Records and Logout
            const restrictedPages = ['admin.html', 'patients.html', 'doctors.html', 'billing.html', 'index.html'];
            if (restrictedPages.some(p => path.endsWith(p))) {
                window.location.href = 'appointments.html';
            }

            const sidebarLinks = document.querySelector('.nav-links');
            if (sidebarLinks) {
                sidebarLinks.innerHTML = `
                    <a href="appointments.html" class="nav-item"><i class="fas fa-calendar-alt"></i> <span>Appointments</span></a>
                    <a href="records.html" class="nav-item"><i class="fas fa-file-medical"></i> <span>Medical Records</span></a>
                    <a href="#" class="nav-item" onclick="logout()"><i class="fas fa-sign-out-alt"></i> <span>Logout</span></a>
                `;

                // mark active link
                document.querySelectorAll('.nav-links .nav-item').forEach(a => {
                    const href = a.getAttribute('href');
                    if (href) {
                        if (path.endsWith(href) || (href === 'appointments.html' && path.endsWith('book-appointment.html'))) {
                            a.classList.add('active');
                        }
                    }
                });
            }
        }
    }
}

async function logout() {
    if (await showCustomConfirm('Are you sure you want to logout?')) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = 'login.html';
    }
}

function showCustomConfirm(message) {
    return new Promise((resolve) => {
        const overlay = document.createElement('div');
        overlay.className = 'custom-confirm-overlay';
        overlay.innerHTML = `
            <div class="custom-confirm-card">
                <div class="confirm-icon-circle">
                    <i class="fas fa-exclamation-triangle"></i>
                </div>
                <h3>Confirm Action</h3>
                <p>${message}</p>
                <div class="confirm-actions">
                    <button class="btn-confirm-cancel">Cancel</button>
                    <button class="btn-confirm-ok">Yes, Proceed</button>
                </div>
            </div>
        `;
        document.body.appendChild(overlay);

        setTimeout(() => overlay.classList.add('active'), 10);

        const cancelBtn = overlay.querySelector('.btn-confirm-cancel');
        const okBtn = overlay.querySelector('.btn-confirm-ok');

        const closeConfirm = (confirmed) => {
            overlay.classList.remove('active');
            setTimeout(() => {
                overlay.remove();
                resolve(confirmed);
            }, 300);
        };

        cancelBtn.addEventListener('click', () => closeConfirm(false));
        okBtn.addEventListener('click', () => closeConfirm(true));
    });
}

document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    initGlobalSearch();
    
    // Add navigation listener to user profile picture & name button (top-right header)
    const avatar = document.querySelector('.user-profile');
    if (avatar) {
        avatar.style.cursor = 'pointer';
        avatar.title = 'View Profile';
        avatar.addEventListener('click', () => {
            const user = localStorage.getItem('user');
            if (user) {
                const userData = JSON.parse(user);
                if (userData.role === 'PATIENT') {
                    window.location.href = 'patient-profile.html';
                } else if (userData.role === 'DOCTOR') {
                    window.location.href = 'doctor-profile.html';
                } else if (userData.role === 'ADMIN' || userData.role === 'STAFF') {
                    window.location.href = 'admin.html';
                }
            }
        });
    }
});
