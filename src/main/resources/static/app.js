const API_BASE = "http://localhost:8080/api";
const currentUserRole = localStorage.getItem("role") || "USER";
const currentUsername = localStorage.getItem("username") || "Guest";
const currentUserId = localStorage.getItem("userId");

async function init() {
    setupUI();
    await loadStats();
    await loadBooksForBorrowing();
    await loadTransactions();
    await checkDebt(); 
    await renderCharts();

    if (currentUserRole === 'ADMIN') {
        await loadUsers(); 
        await loadAuthorsAndCategories();
        await loadUserTable(); 
    }
}

function setupUI() {
    document.getElementById("display-username").innerText = currentUsername;
    document.getElementById("display-role").innerText = currentUserRole;
    if (currentUserRole === 'ADMIN') document.querySelectorAll(".admin-only").forEach(el => el.style.display = "block");
}

async function renderCharts() {
    try {
        const books = await (await fetch(`${API_BASE}/books`)).json();
        const categories = [...new Set(books.map(b => b.category.name))];
        const counts = categories.map(cat => books.filter(b => b.category.name === cat).length);
        const ctx = document.getElementById('categoryChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: categories,
                datasets: [{ data: counts, backgroundColor: '#6366f1', borderRadius: 10 }]
            },
            options: {
                responsive: true, maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: { y: { display: false }, x: { grid: { display: false }, ticks: { color: '#71717a' } } }
            }
        });
    } catch (e) {}
}

function filterTransactions() {
    const input = document.getElementById("transactionSearch").value.toLowerCase();
    document.querySelectorAll("#transactions-table tr").forEach(row => {
        row.style.display = row.innerText.toLowerCase().includes(input) ? "" : "none";
    });
}

async function checkDebt() {
    if(!currentUserId) return;
    const debt = await (await fetch(`${API_BASE}/users/${currentUserId}/debt`)).json();
    const el = document.getElementById("debt-status");
    el.innerText = debt > 0 ? `$${debt} Overdue` : "No Fines";
    el.style.background = debt > 0 ? "rgba(239, 68, 68, 0.1)" : "rgba(16, 185, 129, 0.1)";
    el.style.color = debt > 0 ? "#ef4444" : "#10b981";
    if(debt > 0) document.getElementById("pay-btn").style.display = "block";
}

async function loadUserTable() {
    const users = await (await fetch(`${API_BASE}/users`)).json();
    const table = document.getElementById("users-table");
    table.innerHTML = "";
    for (const u of users) {
        const d = await (await fetch(`${API_BASE}/users/${u.id}/debt`)).json();
        const isBanned = !u.active;
        let ctrl = u.role === 'ADMIN' ? '🛡️' : (isBanned ? `<button onclick="unbanUser(${u.id})" class="btn" style="padding:5px 10px; font-size:0.7rem; background:#10b981">Restore</button>` : `<button onclick="banUser(${u.id})" class="btn" style="padding:5px 10px; font-size:0.7rem; background:#ef4444; color:white">Ban</button>`);
        table.innerHTML += `<tr><td>${u.id}</td><td>${u.username}</td><td>${u.email}</td><td style="color:${d>0?'#ef4444':'#10b981'}">$${d}</td><td><span class="badge ${isBanned?'b-loan':'b-ok'}">${isBanned?'BANNED':'ACTIVE'}</span></td><td>${ctrl}</td></tr>`;
    }
}

async function loadTransactions() {
    const ts = await (await fetch(`${API_BASE}/borrow`)).json();
    const table = document.getElementById("transactions-table");
    table.innerHTML = "";
    ts.forEach(t => {
        if (currentUserRole === 'ADMIN' || (t.user && t.user.username === currentUsername)) {
            const active = t.returnDate === null;
            const btn = active ? `<button onclick="returnBook(${t.id})" class="btn" style="padding:5px 10px; font-size:0.7rem; background:#6366f1; color:white">Return</button>` : '✓';
            table.innerHTML += `<tr><td>#${t.id}</td><td>${t.user.username}</td><td>${t.book.title}</td><td>${t.dueDate||'--'}</td><td><span class="badge ${active?'b-loan':'b-ok'}">${active?'Loaned':'Returned'}</span></td><td>${btn}</td></tr>`;
        }
    });
}

// Global functions (addBook, borrow, return, pay, etc.) remain as they were but call location.reload() to refresh the UI.
async function borrowBook() {
    let uId = (currentUserRole === 'ADMIN') ? document.getElementById("borrowUserSelect").value : currentUserId;
    let bId = document.getElementById("borrowBookSelect").value;
    const res = await fetch(`${API_BASE}/borrow/${uId}/${bId}`, { method: "POST" });
    if(res.ok) location.reload(); else { const e = await res.json(); alert(e.message || "Denied"); }
}
async function addBook() {
    const title = document.getElementById("bookTitle").value;
    const stock = document.getElementById("bookStock").value;
    const authorId = document.getElementById("authorSelect").value;
    const categoryId = document.getElementById("categorySelect").value;
    await fetch(`${API_BASE}/books`, { method: "POST", headers: {"Content-Type":"application/json"}, body: JSON.stringify({title, stock, author:{id:authorId}, category:{id:categoryId}}) });
    location.reload();
}
async function handlePayDebt() { await fetch(`${API_BASE}/users/${currentUserId}/pay`, { method: "POST" }); location.reload(); }
async function returnBook(id) { await fetch(`${API_BASE}/borrow/return/${id}`, { method: "POST" }); location.reload(); }
async function banUser(id) { await fetch(`${API_BASE}/users/${id}/ban`, {method:"POST"}); loadUserTable(); }
async function unbanUser(id) { await fetch(`${API_BASE}/users/${id}/unban`, {method:"POST"}); loadUserTable(); }
async function loadStats() {
    const books = await (await fetch(`${API_BASE}/books`)).json();
    const users = await (await fetch(`${API_BASE}/users`)).json();
    const ts = await (await fetch(`${API_BASE}/borrow`)).json();
    document.getElementById("stat-books").innerText = books.length;
    document.getElementById("stat-users").innerText = users.length;
    document.getElementById("stat-active").innerText = ts.filter(t => t.returnDate == null).length;
}
async function loadBooksForBorrowing() {
    const books = await (await fetch(`${API_BASE}/books`)).json();
    document.getElementById("borrowBookSelect").innerHTML = books.map(b => `<option value="${b.id}" ${b.stock < 1 ? 'disabled' : ''}>${b.title} (${b.stock} left)</option>`).join('');
}
async function loadUsers() {
    const users = await (await fetch(`${API_BASE}/users`)).json();
    document.getElementById("borrowUserSelect").innerHTML = users.map(u => `<option value="${u.id}">${u.username}</option>`).join('');
}
async function loadAuthorsAndCategories() {
    const as = await (await fetch(`${API_BASE}/authors`)).json();
    const cs = await (await fetch(`${API_BASE}/categories`)).json();
    document.getElementById("authorSelect").innerHTML = as.map(a => `<option value="${a.id}">${a.name}</option>`).join('');
    document.getElementById("categorySelect").innerHTML = cs.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
}
async function addAuthor() {
    const name = document.getElementById("authorName").value;
    await fetch(`${API_BASE}/authors`, { method: "POST", headers: {"Content-Type":"application/json"}, body: JSON.stringify({name}) });
    location.reload();
}

init();