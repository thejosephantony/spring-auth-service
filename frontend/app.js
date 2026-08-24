const API_URL = "http://localhost:8080";
let tokenJWT = localStorage.getItem("token") || "";
window.onload = () => { if (tokenJWT) { alternarSecoes(true); carregarUsuarios(); } };
async function fazerLogin() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("senha").value;
    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });
        if (!response.ok) throw new Error("Credenciais inválidas!");
        const data = await response.json();
        tokenJWT = data.token;
        localStorage.setItem("token", tokenJWT);
        alternarSecoes(true);
        carregarUsuarios();
    } catch (error) { alert(error.message); }
}
function fazerLogout() { tokenJWT = ""; localStorage.removeItem("token"); alternarSecoes(false); }
function alternarSecoes(logado) {
    if (logado) {
        document.getElementById("login-section").classList.add("hidden");
        document.getElementById("dashboard-section").classList.remove("hidden");
    } else {
        document.getElementById("login-section").classList.remove("hidden");
        document.getElementById("dashboard-section").classList.add("hidden");
    }
}
async function carregarUsuarios() {
    try {
        const response = await fetch(`${API_URL}/users`, {
            method: "GET",
            headers: { "Authorization": `Bearer ${tokenJWT}` }
        });
        if (!response.ok) throw new Error("Erro ao buscar usuários.");
        const usuarios = await response.json();
        const tbody = document.getElementById("tabela-usuarios");
        tbody.innerHTML = "";
        usuarios.forEach(user => {
            const tr = document.createElement("tr");
            tr.innerHTML = `<td>${user.id}</td><td>${user.name}</td><td>${user.email}</td><td>${user.role}</td><td><button class="btn-danger" onclick="deletarUsuario(${user.id})">Excluir</button></td>`;
            tbody.appendChild(tr);
        });
    } catch (error) { alert(error.message); }
}
async function criarUsuario() {
    const name = document.getElementById("novoNome").value;
    const email = document.getElementById("novoEmail").value;
    const password = document.getElementById("novaSenha").value;
    if (!name || !email || !password) { alert("Preencha todos os campos!"); return; }
    try {
        const response = await fetch(`${API_URL}/users`, {
            method: "POST",
            headers: { "Content-Type": "application/json", "Authorization": `Bearer ${tokenJWT}` },
            body: JSON.stringify({ name, email, password })
        });
        if (!response.ok) throw new Error("Erro ao criar usuário.");
        document.getElementById("novoNome").value = "";
        document.getElementById("novoEmail").value = "";
        document.getElementById("novaSenha").value = "";
        carregarUsuarios();
        alert("Usuário criado com sucesso!");
    } catch (error) { alert(error.message); }
}
async function deletarUsuario(id) {
    if (!confirm("Tem certeza que deseja excluir este usuário?")) return;
    try {
        const response = await fetch(`${API_URL}/users/${id}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${tokenJWT}` }
        });
        if (!response.ok) throw new Error("Erro ao deletar usuário.");
        carregarUsuarios();
    } catch (error) { alert(error.message); }
}
