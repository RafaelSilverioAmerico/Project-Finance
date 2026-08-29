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

async function carregarGastos() {
    const hoje = new Date();
    const inicio = `${hoje.getFullYear()}-${String(hoje.getMonth() + 1).padStart(2, '0')}-01`;
    const fim  = `${hoje.getFullYear()}-${String(hoje.getMonth() + 1).padStart(2, '0')}-31`;

    const gastos = await Api.listarGastos(inicio, fim);
    const container = document.getElementById('lista-gastos');

    container.innerHTML = gastos.map(g =>`
    <div>
        <strong>${g.descricao}</strong> - R$ ${g.valor} - ${g.categoriaNome} (${g.data})
    </div>
    `).join('');
}
carregarGastos();