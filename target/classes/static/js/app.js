"use strict";

const ENDPOINTS = {
    cadastrarIntegrante: "/integrante/cadastro",
    listarIntegrantes: "/integrante/consultaIntegrantes",
    cadastrarTime: "/time/cadastro"
};

const linksDoMenu = document.querySelectorAll("[data-tela]");
const secoes = document.querySelectorAll(".secao-tela");

const formIntegrante = document.getElementById("formIntegrante");
const formTime = document.getElementById("formTime");

const listaIntegrantes =
    document.getElementById("listaIntegrantes");

const mensagemIntegrante =
    document.getElementById("mensagemIntegrante");

const mensagemTime =
    document.getElementById("mensagemTime");


/*
 * Navegação entre as telas
 */
function mostrarTela(nomeDaTela) {

    secoes.forEach(secao => {
        secao.hidden =
            secao.id !== `secao-${nomeDaTela}`;
    });

    linksDoMenu.forEach(link => {
        link.classList.toggle(
            "ativo",
            link.dataset.tela === nomeDaTela
        );
    });

    history.replaceState(
        null,
        "",
        `#${nomeDaTela}`
    );

    if (nomeDaTela === "equipes") {
        carregarIntegrantes();
    }
}


linksDoMenu.forEach(link => {

    link.addEventListener("click", evento => {

        evento.preventDefault();

        mostrarTela(link.dataset.tela);
    });

});


/*
 * Exibe mensagens de sucesso ou erro
 */
function exibirMensagem(elemento, texto, tipo) {

    elemento.textContent = texto;

    elemento.classList.remove(
        "sucesso",
        "erro"
    );

    if (texto) {
        elemento.classList.add(tipo);
    }
}


/*
 * Cria visualmente um integrante com checkbox
 */
function criarOpcaoIntegrante(integrante) {

    const label = document.createElement("label");
    label.className = "opcao-integrante";

    const checkbox = document.createElement("input");

    checkbox.type = "checkbox";
    checkbox.name = "integrantesIds";
    checkbox.value = integrante.id;

    const descricao = document.createElement("span");

    descricao.textContent = integrante.funcao
        ? `${integrante.nome} — ${integrante.funcao}`
        : integrante.nome;

    label.append(
        checkbox,
        descricao
    );

    return label;
}


/*
 * Busca todos os integrantes cadastrados
 */
async function carregarIntegrantes() {

    listaIntegrantes.innerHTML =
        '<p class="estado-lista">Carregando integrantes...</p>';

    try {

        const response = await fetch(
            ENDPOINTS.listarIntegrantes
        );

        if (!response.ok) {
            throw new Error(
                "Não foi possível carregar os integrantes."
            );
        }

        const integrantes = await response.json();

        listaIntegrantes.innerHTML = "";

        if (integrantes.length === 0) {

            listaIntegrantes.innerHTML =
                '<p class="estado-lista">Nenhum integrante cadastrado.</p>';

            return;
        }

        integrantes
            .sort((primeiro, segundo) =>
                primeiro.id - segundo.id
            )
            .forEach(integrante => {

                const opcao =
                    criarOpcaoIntegrante(integrante);

                listaIntegrantes.appendChild(opcao);
            });

    } catch (erro) {

        listaIntegrantes.innerHTML = "";

        const mensagemErro =
            document.createElement("p");

        mensagemErro.className =
            "estado-lista erro-lista";

        mensagemErro.textContent =
            `${erro.message} Confirme o endpoint GET /integrante/listar.`;

        listaIntegrantes.appendChild(
            mensagemErro
        );
    }
}


/*
 * Cadastro de integrante
 */
formIntegrante.addEventListener(
    "submit",
    async evento => {

        evento.preventDefault();

        const botao =
            formIntegrante.querySelector(
                'button[type="submit"]'
            );

        const integrante = {

            nome: document
                .getElementById("nomeIntegrante")
                .value
                .trim(),

            funcao: document
                .getElementById("funcaoIntegrante")
                .value
                .trim()
        };

        exibirMensagem(
            mensagemIntegrante,
            "",
            ""
        );

        botao.disabled = true;
        botao.textContent = "Cadastrando...";

        try {

            const response = await fetch(
                ENDPOINTS.cadastrarIntegrante,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify(integrante)
                }
            );

            if (!response.ok) {
                throw new Error(
                    "Não foi possível cadastrar o integrante."
                );
            }

            const integranteCadastrado =
                await response.json();

            formIntegrante.reset();

            exibirMensagem(
                mensagemIntegrante,
                `Integrante cadastrado com sucesso.`,
                "sucesso"
            );

            /*
             * Atualiza os integrantes disponíveis
             * no cadastro de equipes.
             */
            await carregarIntegrantes();

        } catch (erro) {

            exibirMensagem(
                mensagemIntegrante,
                erro.message,
                "erro"
            );

        } finally {

            botao.disabled = false;
            botao.textContent =
                "Cadastrar integrante";
        }
    }
);


/*
 * Cadastro de equipe
 */
formTime.addEventListener(
    "submit",
    async evento => {

        evento.preventDefault();

        const botao =
            formTime.querySelector(
                'button[type="submit"]'
            );

        /*
         * Busca apenas os checkboxes selecionados.
         */
        const integrantesIds = Array.from(
            listaIntegrantes.querySelectorAll(
                'input[name="integrantesIds"]:checked'
            )
        ).map(checkbox =>
            Number(checkbox.value)
        );

        if (integrantesIds.length === 0) {

            exibirMensagem(
                mensagemTime,
                "Selecione pelo menos um integrante.",
                "erro"
            );

            return;
        }

        const time = {

            nomeDoClube: document
                .getElementById("nomeDoClube")
                .value
                .trim(),

            data: document
                .getElementById("dataTime")
                .value,

            integrantesIds: integrantesIds
        };

        exibirMensagem(
            mensagemTime,
            "",
            ""
        );

        botao.disabled = true;
        botao.textContent = "Cadastrando...";

        try {

            const response = await fetch(
                ENDPOINTS.cadastrarTime,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify(time)
                }
            );

            if (!response.ok) {
                throw new Error(
                    "Não foi possível cadastrar a equipe."
                );
            }

            const timeCadastrado =
                await response.json();

            formTime.reset();

            exibirMensagem(
                mensagemTime,
                `Equipe cadastrada com sucesso. ID: ${timeCadastrado.id}`,
                "sucesso"
            );

        } catch (erro) {

            exibirMensagem(
                mensagemTime,
                erro.message,
                "erro"
            );

        } finally {

            botao.disabled = false;
            botao.textContent =
                "Cadastrar equipe";
        }
    }
);


/*
 * Define a tela inicial pela URL.
 */
const telaInicial =
    window.location.hash.replace("#", "");

if (
    telaInicial === "equipes" ||
    telaInicial === "consultas"
) {
    mostrarTela(telaInicial);
} else {
    mostrarTela("integrantes");
}