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

document.getElementById('form-gasto').addEventListener("submit", async (e) => {
    e.preventDefault();

    const dados = Object.fromEntries(new FormData(e.target));
    dados.categoriaId = Number(dados.categoriaId);
    dados.valor = Number(dados.valor);

    try {
        await Api.criarGasto(dados);
        e.target.reset();
    } catch (err) {
        alert(err.message);
    }
});