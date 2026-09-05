package br.com.duxusdesafio.controller;

import br.com.duxusdesafio.dto.IntegranteDto;
import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/integrante")
public class IntegranteController {
    @Autowired
    private ApiService apiService;

    @PostMapping("/cadastro")
    public ResponseEntity<Integrante> cadastrar(@RequestBody IntegranteDto dto){

        //Verificação de nome e função null ou vazio

        if(dto.possuiNomeOuFuncaoNulo() || dto.possuiNomeOuFuncaoVazio()){
            return ResponseEntity.badRequest().build();
        }

        Integrante integrante = apiService.cadastrarIntegrante(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(integrante);

    }
}
