document.getElementById('form-login').addEventListener('submit', async (e) => {
    e.preventDefault();

    const dados = Object.fromEntries(new FormData(e.target));

    try {
        const resposta = await Api.login(dados);
        Auth.salvar(resposta);
        window.location.href = 'dashboard.html';
    } catch (err) {
        alert(err.message);
    }
});

document.getElementById('form-registro').addEventListener('submit', async (e) => {
    e.preventDefault();

    const dados = Object.fromEntries(new FormData(e.target));

    try {
        const resposta = await Api.registrar(dados);
        Auth.salvar(resposta);
        window.location.href = 'dashboard.html';
    } catch (err) {
        alert(err.message);
    }
});

document.getElementById('btn-alternar').addEventListener('click', () => {
    const login = document.getElementById('form-login');
    const registro = document.getElementById('form-registro');

    const mostrandoLogin = login.style.display !== 'none';

    login.style.display = mostrandoLogin ? 'none' : 'flex';
    registro.style.display = mostrandoLogin ? 'flex' : 'none';
});