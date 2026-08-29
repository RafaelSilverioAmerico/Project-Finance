const Auth = {
    getToken: () => localStorage.getItem('meucaixa_token'),
    salvar: (authResponser) => {
        localStorage.setItem('meucaixa_token', authResponser.token)
        localStorage.setItem('meucaixa_user', JSON.stringify({
            id: authResponser.userId,
            nome: authResponser.nome,
            email: authResponser.email
        }));
    },
    getUser: () => JSON.parse(localStorage.getItem('meucaixa_user') || 'null'),
    limpar: () => {
        localStorage.removeItem('meucaixa_token');
        localStorage.removeItem('meucaixa_user');
    },
    estaAutenticado: () => !!localStorage.getItem('meucaixa_token'),
};

const API_BASE_URL = 'http://localhost:8080';

async function apiRequest(path, {method = 'GET', body} = {}) {
    const headers = {'Content-Type': 'application/json'};
    const token = Auth.getToken();
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const response = await fetch(`${API_BASE_URL}${path}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.mensagem || 'Erro ao comunicar com o servidor');
    }
    if (response.status === 204) return null;
    return response.json();
}

const Api = {
    registrar: (dados) => apiRequest('/api/auth/registrar', {method: 'POST', body: dados}),
    login: (dados) => apiRequest('/api/auth/login', {method: 'POST', body: dados}),

    listarCategorias: () => apiRequest('/api/categorias'),
    criarCategoria: (dados) => apiRequest('/api/categorias', {method: 'POST', body: dados}),

    criarGasto: (dados) => apiRequest('/api/gastos', {method: 'POST', body: dados}),
    excluirGasto: (id) => apiRequest(`/api/gastos/${id}`, {method: 'DELETE'}),
    listarGastos: (inicio, fim) => apiRequest(`/api/gastos?inicio=${inicio}&fim=${fim}`),

    resumoDiario: () => apiRequest('/api/resumo/diario'),
    resumoSemanal: () => apiRequest('/api/resumo/semanal'),
    resumoMensal: (ano, mes) => apiRequest(`/api/resumo/mensal?ano=${ano}&mes=${mes}`),
};