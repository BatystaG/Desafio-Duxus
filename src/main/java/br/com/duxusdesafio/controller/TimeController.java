package br.com.duxusdesafio.controller;

import br.com.duxusdesafio.dto.IntegranteDto;
import br.com.duxusdesafio.dto.TimeDto;
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

@RestController
@RequestMapping("/time")
public class TimeController {
    @Autowired
    private ApiService apiService;
    @Autowired
    private TimeRepository timeRepository;

    @PostMapping("/cadastro")

    public ResponseEntity<Time> cadastrar(@RequestBody TimeDto dto){

        //Verificação de nome, data e Integrantes null ou vazio

        if(dto.possuiNomeJogadoresOuDataNulo() || dto.possuiNomeOuIntegrantesVazio()){
            return ResponseEntity.badRequest().build();
        }

        Time time = apiService.cadastrarTime(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(time);

    }

    @GetMapping("/consultaTimeDaData")

    //Utiliza diretamente o timeRepository para buscar os times e repassa ao metodo da ApiService,
    // pois recebe do front apenas a data

    public ResponseEntity<Time> consultaTimeDaData(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate data){

        if(data == null ){
            return ResponseEntity.badRequest().build();
        }

        List<Time> todosOsTimes = timeRepository.findAll();

        Time timeDaData = apiService.timeDaData(data, todosOsTimes);

        if (timeDaData == null) {
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(timeDaData);

    }
}
