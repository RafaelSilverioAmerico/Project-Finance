if (!Auth.estaAutenticado()) {
    window.location.href ='index.html';
}

const usuario = Auth.getUser();
document.getElementById('user-name').textContent = usuario.nome;

document.getElementById('btn-logout').addEventListener('click',() => {
    Auth.limpar();
    window.location.href = 'index.html';
});

async function carregarCategorias() {
    const categorias = await Api.listarCategorias();
    const select = document.getElementById('select-categoria');
    select.innerHTML = categorias.map(c => `<option value="${c.id}">${c.nome}</option>`).join('');
}
carregarCategorias();