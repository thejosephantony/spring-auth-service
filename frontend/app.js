const API_URL = 'http://localhost:8080';
let tokenJWT = localStorage.getItem('token') || '';

window.onload = async () => {
    if (tokenJWT) {
        await verificarEExibirPainel();
    }
};

async function fazerLogin() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('senha').value;

    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) throw new Error('E-mail ou senha inválidos!');

        const data = await response.json();
        tokenJWT = data.token;
        localStorage.setItem('token', tokenJWT);

        await verificarEExibirPainel();
    } catch (error) {
        alert(error.message);
    }
}

async function verificarEExibirPainel() {
    try {
        const response = await fetch(`${API_URL}/users/me`, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${tokenJWT}` }
        });

        if (!response.ok) {
            fazerLogout();
            throw new Error('Sessão expirada ou token inválido.');
        }

        const user = await response.json();
        
        // Exibe a navbar e oculta o login
        document.getElementById('login-section').classList.add('hidden');
        document.getElementById('main-navbar').classList.remove('hidden');

        // CORREÇÃO AQUI: Atualiza o e-mail na navbar para TODOS os usuários (ADMIN ou USER)
        document.getElementById('admin-email-display').innerText = `${user.email} (${user.role})`;

        // Direciona para o painel correto dependendo do perfil
        if (user.role === 'ADMIN') {
            document.getElementById('admin-dashboard').classList.remove('hidden');
            carregarUsuarios();
        } else {
            document.getElementById('user-dashboard').classList.remove('hidden');
            document.getElementById('me-id').innerText = user.id;
            document.getElementById('me-nome').innerText = user.name;
            document.getElementById('me-email').innerText = user.email;
            
            const roleBadge = document.getElementById('me-role');
            roleBadge.innerText = user.role;
            roleBadge.className = 'badge badge-user';
        }
    } catch (error) {
        console.error(error);
    }
}

function fazerLogout() {
    tokenJWT = '';
    localStorage.removeItem('token');
    document.getElementById('login-section').classList.remove('hidden');
    document.getElementById('main-navbar').classList.add('hidden');
    document.getElementById('admin-dashboard').classList.add('hidden');
    document.getElementById('user-dashboard').classList.add('hidden');
}

async function carregarUsuarios() {
    try {
        const response = await fetch(`${API_URL}/users`, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${tokenJWT}` }
        });

        if (!response.ok) throw new Error('Erro ao buscar usuários.');

        const usuarios = await response.json();
        const tbody = document.getElementById('tabela-usuarios');
        tbody.innerHTML = '';

        usuarios.forEach(user => {
            const tr = document.createElement('tr');
            const badgeClass = user.role === 'ADMIN' ? 'badge badge-admin' : 'badge badge-user';
            
            tr.innerHTML = `
                <td><strong>#${user.id}</strong></td>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td><span class="${badgeClass}">${user.role}</span></td>
                <td class="text-right">
                    <button class="btn-action-edit" onclick="abrirModalEdicao(${user.id}, '${user.name}', '${user.email}')">Editar</button>
                    <button class="btn-action-delete" onclick="deletarUsuario(${user.id})">Excluir</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        alert(error.message);
    }
}

async function criarUsuario() {
    const name = document.getElementById('novoNome').value;
    const email = document.getElementById('novoEmail').value;
    const password = document.getElementById('novaSenha').value;
    const role = document.getElementById('novaRole').value;

    if (!name || !email || !password) {
        alert('Preencha todos os campos!');
        return;
    }

    try {
        const response = await fetch(`${API_URL}/users`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${tokenJWT}`
            },
            body: JSON.stringify({ name, email, password, role })
        });

        if (!response.ok) throw new Error('Erro ao criar usuário (verifique se o e-mail já existe).');

        document.getElementById('novoNome').value = '';
        document.getElementById('novoEmail').value = '';
        document.getElementById('novaSenha').value = '';

        carregarUsuarios();
        alert('Colaborador cadastrado com sucesso!');
    } catch (error) {
        alert(error.message);
    }
}

async function deletarUsuario(id) {
    if (!confirm('Deseja realmente excluir este colaborador?')) return;

    try {
        const response = await fetch(`${API_URL}/users/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${tokenJWT}` }
        });

        if (!response.ok) throw new Error('Erro ao deletar usuário.');

        carregarUsuarios();
    } catch (error) {
        alert(error.message);
    }
}

// ==========================================
// LÓGICA DO MODAL DE EDIÇÃO
// ==========================================
let usuarioEmEdicaoId = null;

function abrirModalEdicao(id, nomeAtual, emailAtual, isMe = false) {
    usuarioEmEdicaoId = id; 
    
    document.getElementById('edit-nome').value = nomeAtual;
    document.getElementById('edit-email').value = emailAtual;
    document.getElementById('edit-senha').value = '';
    
    document.getElementById('modal-title').innerText = isMe ? 'Editar Meu Perfil' : 'Editar Colaborador';
    
    document.getElementById('edit-modal').classList.remove('hidden');
}

function fecharModal() {
    document.getElementById('edit-modal').classList.add('hidden');
    usuarioEmEdicaoId = null;
}

async function salvarEdicao() {
    const novoNome = document.getElementById('edit-nome').value;
    const novoEmail = document.getElementById('edit-email').value;
    const novaSenha = document.getElementById('edit-senha').value;

    if (!novoNome || !novoEmail) {
        alert("O nome e o e-mail não podem ficar em branco!");
        return;
    }

    const dadosAtualizados = { name: novoNome, email: novoEmail };
    
    if (novaSenha && novaSenha.trim() !== "") {
        dadosAtualizados.password = novaSenha;
    }

    const endpoint = usuarioEmEdicaoId === 'me' 
        ? `${API_URL}/users/me` 
        : `${API_URL}/users/${usuarioEmEdicaoId}`;

    try {
        const response = await fetch(endpoint, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${tokenJWT}`
            },
            body: JSON.stringify(dadosAtualizados)
        });

        if (!response.ok) throw new Error('Erro ao atualizar. Verifique se o e-mail já está em uso.');

        fecharModal();
        
        if (usuarioEmEdicaoId === 'me') {
            alert('Seu perfil foi atualizado com sucesso! Por favor, faça login novamente.');
            fazerLogout();
        } else {
            carregarUsuarios();
            alert('Dados do colaborador atualizados com sucesso!');
        }
    } catch (error) {
        alert(error.message);
    }
}