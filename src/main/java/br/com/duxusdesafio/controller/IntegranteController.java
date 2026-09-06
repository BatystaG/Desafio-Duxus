package br.com.duxusdesafio.controller;

import br.com.duxusdesafio.dto.IntegranteDto;
import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.model.Time;
import br.com.duxusdesafio.repository.TimeRepository;
import br.com.duxusdesafio.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/integrante")
public class IntegranteController {
    @Autowired
    private ApiService apiService;
    @Autowired
    private TimeRepository timeRepository;

    @PostMapping("/cadastro")
    public ResponseEntity<Integrante> cadastrar(@RequestBody IntegranteDto dto){

        //Verificação de nome e função null ou vazio

        if(dto.possuiNomeOuFuncaoNulo() || dto.possuiNomeOuFuncaoVazio()){
            return ResponseEntity.badRequest().build();
        }

        Integrante integrante = apiService.cadastrarIntegrante(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(integrante);

    }

    @GetMapping("/consultaIntegranteMaisUsado")

    //Utiliza diretamente o timeRepository para buscar os times e repassa ao metodo da ApiService,
    // pois recebe do front apenas as datas

    public ResponseEntity<Integrante> consultaIntegranteMaisUsado(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicial,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFinal){

        List<Time> todosOsTimes = timeRepository.findAll();

        Integrante integranteMaisUsado = apiService.integranteMaisUsado(dataInicial, dataFinal, todosOsTimes);

        if (integranteMaisUsado == null) {
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(integranteMaisUsado);

    }

    @GetMapping("/consultaIntegrantesDoTimeMaisRecorrente")

    //Utiliza diretamente o timeRepository para buscar os times e repassa ao metodo da ApiService,
    // pois recebe do front apenas as datas

    public ResponseEntity<List<String>> consultaIntegrantesDoTimeMaisRecorrente(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicial,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFinal){

        List<Time> todosOsTimes = timeRepository.findAll();

        List<String> integrantesDoTimeMaisUsado = apiService.integrantesDoTimeMaisRecorrente(dataInicial, dataFinal, todosOsTimes);

        if (integrantesDoTimeMaisUsado == null) {
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(integrantesDoTimeMaisUsado);

    }

    @GetMapping("/consultaFuncaoMaisRecorrente")

    //Utiliza diretamente o timeRepository para buscar os times e repassa ao metodo da ApiService,
    // pois recebe do front apenas as datas

    public ResponseEntity<String> consultaFuncaoMaisRecorrente(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicial,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFinal){

        List<Time> todosOsTimes = timeRepository.findAll();

        String funcaoMaisRecorrente = apiService.funcaoMaisRecorrente(dataInicial, dataFinal, todosOsTimes);

        if (funcaoMaisRecorrente == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(funcaoMaisRecorrente);

    }

    @GetMapping("/consultaContagemFuncao")

    //Utiliza diretamente o timeRepository para buscar os times e repassa ao metodo da ApiService,
    // pois recebe do front apenas as datas

    public ResponseEntity<Map<String, Long>> contagemFuncaoNoPeriodo(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicial,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFinal){

        List<Time> todosOsTimes = timeRepository.findAll();

        Map<String, Long> contagemPorFuncao = apiService.contagemPorFuncao(dataInicial, dataFinal, todosOsTimes);

        if (contagemPorFuncao == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(contagemPorFuncao);

    }
}
