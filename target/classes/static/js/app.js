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

const selectIntegrante =
    document.getElementById("selectIntegrante");

const listaIntegrantesSelecionados =
    document.getElementById("integrantesSelecionados");

let integrantesDisponiveis = [];

/*
 * Map com:
 * chave = ID
 * valor = objeto do integrante
 */
const integrantesEscolhidos = new Map();

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
function ordenarIntegrantes(integrantes) {

    return integrantes.sort((primeiro, segundo) =>
        primeiro.nome.localeCompare(
            segundo.nome,
            "pt-BR",
            {
                sensitivity: "base"
            }
        )
    );
}


function atualizarSelectIntegrantes() {

    selectIntegrante.innerHTML = "";

    const integrantesRestantes =
        ordenarIntegrantes(
            integrantesDisponiveis
                .filter(integrante =>
                    !integrantesEscolhidos.has(
                        Number(integrante.id)
                    )
                )
        );

    const primeiraOpcao =
        document.createElement("option");

    primeiraOpcao.value = "";

    if (integrantesRestantes.length > 0) {
        primeiraOpcao.textContent =
            "Selecione um integrante";
    } else if (integrantesDisponiveis.length > 0) {
        primeiraOpcao.textContent =
            "Todos os integrantes foram adicionados";
    } else {
        primeiraOpcao.textContent =
            "Nenhum integrante cadastrado";
    }

    selectIntegrante.appendChild(primeiraOpcao);

    integrantesRestantes.forEach(integrante => {

        const opcao =
            document.createElement("option");

        opcao.value = integrante.id;

        opcao.textContent =
            `${integrante.nome} — ${integrante.funcao}`;

        selectIntegrante.appendChild(opcao);
    });

    selectIntegrante.disabled =
        integrantesRestantes.length === 0;
}


function atualizarListaDeSelecionados() {

    listaIntegrantesSelecionados.innerHTML = "";

    if (integrantesEscolhidos.size === 0) {

        const mensagem =
            document.createElement("p");

        mensagem.className = "estado-lista";

        mensagem.textContent =
            "Nenhum integrante selecionado.";

        listaIntegrantesSelecionados.appendChild(
            mensagem
        );

        return;
    }

    const selecionadosOrdenados =
        ordenarIntegrantes(
            Array.from(integrantesEscolhidos.values())
        );

    selecionadosOrdenados.forEach(integrante => {

        const linha =
            document.createElement("div");

        linha.className =
            "integrante-selecionado";

        const nome =
            document.createElement("span");

        nome.className = "coluna-nome";
        nome.textContent = integrante.nome;

        const funcao =
            document.createElement("span");

        funcao.className = "coluna-funcao";
        funcao.textContent = integrante.funcao;

        const botaoRemover =
            document.createElement("button");

        botaoRemover.type = "button";
        botaoRemover.className =
            "botao-remover-integrante";

        botaoRemover.textContent = "Remover";

        botaoRemover.setAttribute(
            "aria-label",
            `Remover ${integrante.nome}`
        );

        botaoRemover.addEventListener(
            "click",
            () => {

                integrantesEscolhidos.delete(
                    Number(integrante.id)
                );

                atualizarListaDeSelecionados();
                atualizarSelectIntegrantes();
            }
        );

        linha.append(
            nome,
            funcao,
            botaoRemover
        );

        listaIntegrantesSelecionados.appendChild(
            linha
        );
    });
}


selectIntegrante.addEventListener(
    "change",
    () => {

        const integranteId =
            Number(selectIntegrante.value);

        if (!integranteId) {
            return;
        }

        const integrante =
            integrantesDisponiveis.find(item =>
                Number(item.id) === integranteId
            );

        if (!integrante) {
            return;
        }

        integrantesEscolhidos.set(
            integranteId,
            integrante
        );

        atualizarListaDeSelecionados();
        atualizarSelectIntegrantes();
    }
);


async function carregarIntegrantes() {

    selectIntegrante.disabled = true;

    selectIntegrante.innerHTML =
        '<option value="">Carregando integrantes...</option>';

    try {

        const response = await fetch(
            ENDPOINTS.listarIntegrantes
        );

        if (!response.ok) {
            throw new Error(
                "Não foi possível carregar os integrantes."
            );
        }

        const resultado = await response.json();

        if (!Array.isArray(resultado)) {
            throw new Error(
                "O backend não retornou uma lista de integrantes."
            );
        }

        integrantesDisponiveis = resultado;

        /*
         * Remove da seleção algum integrante que
         * eventualmente não exista mais no backend.
         */
        const idsExistentes = new Set(
            integrantesDisponiveis.map(
                integrante => Number(integrante.id)
            )
        );

        integrantesEscolhidos.forEach(
            (integrante, id) => {

                if (!idsExistentes.has(id)) {
                    integrantesEscolhidos.delete(id);
                }
            }
        );

        atualizarSelectIntegrantes();
        atualizarListaDeSelecionados();

    } catch (erro) {

        selectIntegrante.innerHTML =
            '<option value="">Erro ao carregar integrantes</option>';

        selectIntegrante.disabled = true;

        if (integrantesEscolhidos.size === 0) {

            listaIntegrantesSelecionados.innerHTML = "";

            const mensagem =
                document.createElement("p");

            mensagem.className =
                "estado-lista erro-lista";

            mensagem.textContent = erro.message;

            listaIntegrantesSelecionados.appendChild(
                mensagem
            );
        }
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
        const integrantesIds =
            Array.from(integrantesEscolhidos.keys());

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

            integrantesEscolhidos.clear();

            atualizarListaDeSelecionados();
            atualizarSelectIntegrantes();

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