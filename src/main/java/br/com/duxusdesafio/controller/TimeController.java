package br.com.duxusdesafio.controller;

import br.com.duxusdesafio.dto.IntegranteDto;
import br.com.duxusdesafio.dto.TimeDto;
import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.model.Time;
import br.com.duxusdesafio.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/time")
public class TimeController {
    @Autowired
    private ApiService apiService;

    @PostMapping("/cadastro")

    public ResponseEntity<Time> cadastrar(@RequestBody TimeDto dto){

        //Verificação de nome e função null ou vazio

        if(dto.possuiNomeJogadoresOuDataNulo() || dto.possuiNomeOuIntegrantesVazio()){
            return ResponseEntity.badRequest().build();
        }

        Time time = apiService.cadastrarTime(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(time);

    }
}
