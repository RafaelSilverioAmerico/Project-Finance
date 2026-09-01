if (!Auth.estaAutenticado()) {
    window.location.href = 'index.html';
}

const usuario = Auth.getUser();
document.getElementById('user-name').textContent = usuario.nome;

document.getElementById('btn-logout').addEventListener('click', () => {
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
    const ultimoDia = new Date(hoje.getFullYear(), hoje.getMonth() + 1, 0).getDate();
    const fim = `${hoje.getFullYear()}-${String(hoje.getMonth() + 1).padStart(2, '0')}-${String(ultimoDia).padStart(2, '0')}`;

    const gastos = await Api.listarGastos(inicio, fim);
    const container = document.getElementById('lista-gastos');

    container.innerHTML = gastos.map(g => `
    <div>
        <strong>${g.descricao}</strong> - R$ ${g.valor} - ${g.categoriaNome} (${g.data})
        <button class="btn-excluir" data-id="${g.id}">Excluir</button>
    </div>
    `).join('');

    document.querySelectorAll('.btn-excluir').forEach(botao => {
        botao.addEventListener('click', async () => {
            const id = botao.dataset.id;
            await Api.excluirGasto(id);
        });
    });

}

carregarGastos();

async function carregarResumo() {
    const hoje = new Date();
    const resumo = await Api.resumoMensal(hoje.getFullYear(), hoje.getMonth() + 1);
    document.getElementById('total-mes').textContent = resumo.total.toFixed(2).replace('.', ',');
}

carregarResumo();

const socket = new SockJS(`http://localhost:8080/ws`);
const client = new StompJs.Client({
    webSocketFactory: () => socket,
});

client.onConnect = () => {
    client.subscribe(`/topic/gastos/${usuario.id}`, (mensagem) => {
        const evento = JSON.parse(mensagem.body);
        console.log('Evento recebido:', evento);

        carregarGastos();
        carregarResumo();
    });
};

client.activate();

