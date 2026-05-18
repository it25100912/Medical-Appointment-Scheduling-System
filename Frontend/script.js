const API_BASE_URL = 'http://localhost:8080/api';

// Navbar Scroll Effect
window.addEventListener('scroll', () => {
    const navbar = document.getElementById('navbar');
    if (window.scrollY > 50) {
        navbar.classList.add('scrolled');
    } else {
        navbar.classList.remove('scrolled');
    }
});

// Smooth Scrolling
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({ behavior: 'smooth' });
        }
    });
});

// Fetch Doctors on Page Load
async function fetchDoctors() {
    try {
        const response = await fetch(`${API_BASE_URL}/doctors`);
        const doctors = await response.json();
        const doctorSelect = document.getElementById('doctorSelect');
        
        doctorSelect.innerHTML = '<option value="">Choose a Doctor</option>';
        doctors.forEach(doctor => {
            const option = document.createElement('option');
            option.value = doctor.id;
            option.textContent = `Dr. ${doctor.name} (${doctor.specialization || 'General'})`;
            doctorSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Error fetching doctors:', error);
        document.getElementById('doctorSelect').innerHTML = '<option value="">Error loading doctors</option>';
    }
}

// Handle Appointment Booking
const appointmentForm = document.getElementById('appointmentForm');
if (appointmentForm) {
    appointmentForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const submitBtn = appointmentForm.querySelector('button[type="submit"]');
        submitBtn.innerText = 'Booking...';
        submitBtn.disabled = true;

        const patientData = {
            name: document.getElementById('patientName').value,
            nic: document.getElementById('patientNic').value,
            phone: document.getElementById('patientPhone').value,
            patientType: 'OUTPATIENT'
        };

        try {
            // 1. Find or Register Patient
            let patientId;
            const patientCheck = await fetch(`${API_BASE_URL}/patients/nic/${patientData.nic}`);
            
            if (patientCheck.ok) {
                const existingPatient = await patientCheck.json();
                patientId = existingPatient.id;
            } else {
                const regResponse = await fetch(`${API_BASE_URL}/patients`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(patientData)
                });
                const newPatient = await regResponse.json();
                patientId = newPatient.id;
            }

            // 2. Book Appointment
            const appointmentData = {
                patientId: patientId,
                doctorId: document.getElementById('doctorSelect').value,
                appointmentDate: document.getElementById('appointmentDate').value,
                appointmentTime: document.getElementById('appointmentTime').value + ':00', // Ensure HH:mm:ss
                notes: 'Online Booking',
                status: 'CONFIRMED',
                type: 'GENERAL'
            };

            const appResponse = await fetch(`${API_BASE_URL}/appointments`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(appointmentData)
            });

            if (appResponse.ok) {
                alert('Success! Your appointment has been booked.');
                appointmentForm.reset();
            } else {
                const err = await appResponse.json();
                alert('Booking failed: ' + (err.message || 'Unknown error'));
            }

        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred. Please make sure the backend server is running.');
        } finally {
            submitBtn.innerText = 'Confirm Appointment';
            submitBtn.disabled = false;
        }
    });
}

// Initializations
document.addEventListener('DOMContentLoaded', () => {
    fetchDoctors();
    
    // Animation Observer
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-fade-in');
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.1 });

    document.querySelectorAll('.service-card, .about-text, .gallery-item').forEach(el => {
        el.style.opacity = '0';
        observer.observe(el);
    });
});
