// Centralized function to get current user info
async function getCurrentUser() {
    try {
        const response = await fetch('/api/users/me');
        if (!response.ok) throw new Error('Not authenticated');
        return await response.json();
    } catch (error) {
        console.error("Auth Error:", error);
        return null;
    }
}

// Function to render sidebar and hide admin links if necessary
async function initSidebar() {
    // 1. Get Elements
    const sidebarContainer = document.getElementById('main-sidebar');
    if (!sidebarContainer) return;

    // 2. Inject Mobile Overlay (Click to close sidebar)
    const overlay = document.createElement('div');
    overlay.className = 'mobile-overlay';
    overlay.onclick = toggleMobileSidebar;
    document.body.appendChild(overlay);

    // 3. Inject Mobile Hamburger Button into Main Content
    // We look for the <main> tag and insert the button at the top
    const mainContent = document.querySelector('main');
    if (mainContent) {
        const mobileHeader = document.createElement('div');
        mobileHeader.className = 'mobile-header';
        mobileHeader.innerHTML = `
            <div style="display: flex; align-items: center; gap: 12px;">
                <button onclick="toggleMobileSidebar()" style="background: none; border: none; color: white; cursor: pointer;">
                    <i data-lucide="menu" size="28"></i>
                </button>
                <span style="font-weight: 700; font-size: 18px;">EduPredict.</span>
            </div>
            `;
        mainContent.insertBefore(mobileHeader, mainContent.firstChild);
    }

    // 4. Standard Sidebar Generation (Your existing code)
    const user = await getCurrentUser();
    const isAdmin = user && user.role === 'ADMIN';
    const currentPath = window.location.pathname;
    const isActive = (path) => currentPath.includes(path) ? 'active' : '';

    const logoSVG = `
    <svg width="32" height="32" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg" style="margin-right: 12px;">
        <rect x="4" y="4" width="24" height="24" rx="6" fill="rgba(255,255,255,0.05)"/>
        <rect x="8" y="16" width="4" height="8" rx="1" fill="currentColor" fill-opacity="0.6"/>
        <rect x="14" y="12" width="4" height="12" rx="1" fill="currentColor" fill-opacity="0.8"/>
        <rect x="20" y="8" width="4" height="16" rx="1" fill="var(--teal-neon)"/>
        <path d="M10 16L16 12L22 8" stroke="var(--teal-neon)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
    `;

    let sidebarHTML = `
        <div class="logo-text" style="display: flex; align-items: center; margin-bottom: 40px; font-size: 22px;">
            ${logoSVG}
            <span>EduPredict<span style="color:var(--teal-neon)">.</span></span>
        </div>
        
        <div class="nav-item ${isActive('dashboard')}" onclick="window.location.href='/dashboard.html'">
            <i data-lucide="layout-dashboard" size="20"></i> Dashboard
        </div>
        <div class="nav-item ${isActive('students')}" onclick="window.location.href='/students.html'">
            <i data-lucide="users" size="20"></i> Students
        </div>
        <div class="nav-item ${isActive('analytics')}" onclick="window.location.href='/analytics.html'">
            <i data-lucide="bar-chart-2" size="20"></i> Analytics
        </div>
        <div class="nav-item ${isActive('predictions')}" onclick="window.location.href='/predictions.html'">
            <i data-lucide="trending-up" size="20"></i> Predictions
        </div>
        <div class="nav-item" onclick="window.location.href='/api/students/report/csv'" class="sidebar-link">
    <i data-lucide="file-text" size"20"></i> Reports
</div>
    `;

    if (isAdmin) {
        sidebarHTML += `
        <div class="nav-item ${isActive('users')}" onclick="window.location.href='/users.html'">
            <i data-lucide="user-cog" size="20"></i> User Management
        </div>
        `;
    }

    sidebarHTML += `
        <div class="nav-item" style="margin-top: auto; color: var(--risk-high); padding-bottom: 20px;   " onclick="window.location.href='/logout'">
             <i data-lucide="log-out" size="20"></i> Logout
        </div>
    `;

    sidebarContainer.innerHTML = sidebarHTML;
    lucide.createIcons();
}

// --- NEW FUNCTION: Toggle Mobile Sidebar ---
function toggleMobileSidebar() {
    const sidebar = document.getElementById('main-sidebar');
    const overlay = document.querySelector('.mobile-overlay');

    sidebar.classList.toggle('mobile-open');
    overlay.classList.toggle('active');
}