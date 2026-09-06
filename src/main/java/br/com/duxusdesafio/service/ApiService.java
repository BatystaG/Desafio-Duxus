package br.com.duxusdesafio.service;

import br.com.duxusdesafio.dto.IntegranteDto;
import br.com.duxusdesafio.dto.TimeDto;
import br.com.duxusdesafio.model.ComposicaoTime;
import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.model.Time;
import br.com.duxusdesafio.repository.IntegranteRepository;
import br.com.duxusdesafio.repository.TimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service que possuirá as regras de negócio para o processamento dos dados
 * solicitados no desafio!
 *
 * OBS ao candidato: PREFERENCIALMENTE, NÃO ALTERE AS ASSINATURAS DOS MÉTODOS!
 * Trabalhe com a proposta pura.
 *
 * @author carlosau
 */
@Service
public class ApiService {

    @Autowired
    private IntegranteRepository integranteRepository;
    @Autowired
    private TimeRepository timeRepository;

    public Integrante cadastrarIntegrante(IntegranteDto dto){
        Integrante integrante = new Integrante();
        integrante.setNome(dto.getNome());
        integrante.setFuncao(dto.getFuncao());
        return integranteRepository.save(integrante);
    }

    public Time cadastrarTime(TimeDto dto){
        Time time = new Time();
        time.setNomeDoClube(dto.getNomeDoClube());
        time.setData(dto.getData());

        List<Integrante> integrantes = integranteRepository.findAllById(dto.getIntegrantesIds());
        List<ComposicaoTime> composicoes = new ArrayList<>();

        for (Integrante integrante : integrantes) {

            ComposicaoTime composicao = new ComposicaoTime();

            composicao.setTime(time);
            composicao.setIntegrante(integrante);

            composicoes.add(composicao);
        }

        time.setComposicaoTime(composicoes);

        return timeRepository.save(time);

    }

    /**
     * Vai retornar um Time, com a composição do time daquela data
     */
    public Time timeDaData(LocalDate data, List<Time> todosOsTimes){
        // TODO Implementar método seguindo as instruções!
        //Caso existam mais de um time cadastrados na mesma data, retorna o ultimo cadastrado, pelo maior ID.

        if (todosOsTimes == null) {
            return null;
        }

        Time timeDaData = null;

        for (Time time : todosOsTimes) {

            if (time != null && data.equals(time.getData())) {

                if (timeDaData == null || time.getId() > timeDaData.getId()) {

                    timeDaData = time;
                }
            }
        }

        return timeDaData;

    }

    /**
     * Vai retornar o integrante que estiver presente na maior quantidade de times
     * dentro do período
     */
    public Integrante integranteMaisUsado(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes){
        // TODO Implementar método seguindo as instruções!
        // Nesta solução estamos assumindo que não é possivel cadastrar um Intregrante mais de uma vez no mesmo time,
        // Tambem não estamos considerando a possibilidade de empate, em caso de empate podemos estabelecer o críterio de desempate para implementação futura.

        if (todosOsTimes == null) {
            return null;
        }

        Map<Long, Integer> quantidadePorIntegrante = new HashMap<>();

        Integrante integranteMaisUsado = null;
        int maiorQuantidade = 0;

        for (Time time : todosOsTimes) {

            boolean respeitaDataInicial = dataInicial == null || !time.getData().isBefore(dataInicial);

            boolean respeitaDataFinal = dataFinal == null || !time.getData().isAfter(dataFinal);

            boolean dentroDoPeriodo = respeitaDataInicial && respeitaDataFinal;

            if (dentroDoPeriodo) {

                for (ComposicaoTime composicao : time.getComposicaoTime()) {

                    Integrante integrante = composicao.getIntegrante();
                    Long integranteId = integrante.getId();

                    int novaQuantidade =
                            quantidadePorIntegrante.getOrDefault(integranteId, 0) + 1;

                    quantidadePorIntegrante.put(integranteId, novaQuantidade);

                    if (novaQuantidade > maiorQuantidade) {
                        maiorQuantidade = novaQuantidade;
                        integranteMaisUsado = integrante;
                    }
                }
            }
        }

        return integranteMaisUsado;

    }

    /**
     * Vai retornar uma lista com os nomes dos integrantes do time mais recorrente dentro do período.
     * OBS: Time é o clube + composição em determinada data
     */
    public List<String> integrantesDoTimeMaisRecorrente(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes){
        // TODO Implementar método seguindo as instruções!
        // Nesta solução, consideramos que o time pode ser reutilizado em outras datas sem alteração de Integrantes, por isso,
        // O filtro é feito com o nome do Time.

        if (todosOsTimes == null) {
            return null;
        }

        Map<String, Integer> quantidadeDeAparicoesPorTime = new HashMap<>();

        Time timeMaisRecorrente = null;
        int maiorQuantidade = 0;

        for (Time time : todosOsTimes) {

            boolean respeitaDataInicial = dataInicial == null || !time.getData().isBefore(dataInicial);

            boolean respeitaDataFinal = dataFinal == null || !time.getData().isAfter(dataFinal);

            boolean dentroDoPeriodo = respeitaDataInicial && respeitaDataFinal;

            if (dentroDoPeriodo) {

                String nomeDoTime = time.getNomeDoClube();

                int novaQuantidade = quantidadeDeAparicoesPorTime.getOrDefault(nomeDoTime, 0) + 1;

                quantidadeDeAparicoesPorTime.put(nomeDoTime, novaQuantidade);

                if (novaQuantidade > maiorQuantidade) {
                    maiorQuantidade = novaQuantidade;
                    timeMaisRecorrente = time;
                }
            }
        }

        if (timeMaisRecorrente == null) {
            return null;
        }

        List<String> nomesDosIntegrantes = new ArrayList<>();

        for (ComposicaoTime composicao : timeMaisRecorrente.getComposicaoTime()) {

            nomesDosIntegrantes.add(composicao.getIntegrante().getNome());

        }

        return nomesDosIntegrantes;
    }

    /**
     * Vai retornar a função mais recorrente nos times dentro do período
     */
    public String funcaoMaisRecorrente(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes){
        // TODO Implementar método seguindo as instruções!

        if (todosOsTimes == null) {
            return null;
        }

        Map<String, Integer> quantidadePorFuncao = new HashMap<>();

        String funcaoMaisRecorrente = null;
        int maiorQuantidade = 0;

        for (Time time : todosOsTimes) {

            boolean respeitaDataInicial = dataInicial == null || !time.getData().isBefore(dataInicial);

            boolean respeitaDataFinal = dataFinal == null || !time.getData().isAfter(dataFinal);

            boolean dentroDoPeriodo = respeitaDataInicial && respeitaDataFinal;

            if (dentroDoPeriodo) {

                for (ComposicaoTime composicao : time.getComposicaoTime()) {

                    Integrante integrante = composicao.getIntegrante();
                    String funcao = integrante.getFuncao();

                    int novaQuantidade = quantidadePorFuncao.getOrDefault(funcao, 0) + 1;

                    quantidadePorFuncao.put(funcao, novaQuantidade);

                    if (novaQuantidade > maiorQuantidade) {
                        maiorQuantidade = novaQuantidade;
                        funcaoMaisRecorrente = funcao;
                    }

                }

            }
        }

        return funcaoMaisRecorrente;
    }

    /**
     * Vai retornar o nome do Clube mais comum dentro do período
     */
    public String clubeMaisRecorrente(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        // TODO Implementar método seguindo as instruções!
        return null;
    }


    /**
     * Vai retornar o número (quantidade) de aparições de cada Clube participante no período
     */
    public Map<String, Long> contagemDeClubesNoPeriodo(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes){
        // TODO Implementar método seguindo as instruções!
        return null;
    }

    /**
     * Vai retornar o número (quantidade) de Funções dentro do período.
     * Dica - pense sobre repetições!
     */
    public Map<String, Long> contagemPorFuncao(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes){
        // TODO Implementar método seguindo as instruções!
        return null;
    }

}
