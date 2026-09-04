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

let chartCategorias = null;

function renderizarChartCategorias(porCategoria) {
    const ctx = document.getElementById('chart-categorias');

    if (chartCategorias) {
        chartCategorias.destroy();
    }

    chartCategorias = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: porCategoria.map(c => c.nome),
            datasets: [{
                data: porCategoria.map(c => c.total),
                backgroundColor: porCategoria.map(c => c.cor),
            }]
        }
    });
}

let chartDias = null;

function renderizarChartDias(porDia) {
    const ctx = document.getElementById('chart-dias');

    if (chartDias) {
        chartDias.destroy();
    }

    chartDias = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: porDia.map(d => d.data),
            datasets: [{
                label: 'Gasto por dia',
                data: porDia.map(d => d.total),
                backgroundColor: '#D4A73C',
            }]
        }
    });
}

async function carregarResumo(periodo) {
    let resumo;

    if (periodo === 'diario') {
        resumo = await Api.resumoDiario();
    } else if (periodo === 'semanal') {
        resumo = await Api.resumoSemanal();
    } else if (periodo === 'mensal') {
        const hoje = new Date();
        resumo = await Api.resumoMensal(hoje.getFullYear(), hoje.getMonth() + 1);
    }

    document.getElementById('total-mes').textContent = resumo.total.toFixed(2).replace('.', ',');

    renderizarChartCategorias(resumo.porCategoria);
    renderizarChartDias(resumo.porDia);

    const gastos = await Api.listarGastos(resumo.inicio, resumo.fim);
    const container = document.getElementById('lista-gastos');
    const titulos = {diario: 'Resumo do dia', semanal: 'Resumo da semana', mensal: 'Resumo do mês'};
    document.getElementById('resumo-titulo').textContent = titulos[periodo];

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

let periodoAtual = 'mensal';

document.querySelectorAll('.tab-periodo').forEach(botao => {
    botao.addEventListener('click', () => {
        document.querySelectorAll('.tab-periodo').forEach(b => b.classList.remove('ativo'));
        botao.classList.add('ativo');

        periodoAtual = botao.dataset.periodo;
        carregarResumo(periodoAtual);
    });
});

carregarResumo('mensal');

const socket = new SockJS(`http://localhost:8080/ws`);
const client = new StompJs.Client({
    webSocketFactory: () => socket,
});

client.onConnect = () => {
    client.subscribe(`/topic/gastos/${usuario.id}`, (mensagem) => {
        const evento = JSON.parse(mensagem.body);
        console.log('Evento recebido:', evento);

        carregarResumo(periodoAtual);
    });
};

client.activate();

document.getElementById('form-categoria').addEventListener('submit', async (e) => {
    e.preventDefault();

    const dados = Object.fromEntries(new FormData(e.target));

    try {
        await Api.criarCategoria(dados);
        e.target.reset();
    } catch (err) {
        alert(err.message);
    }

    carregarCategorias();
})

