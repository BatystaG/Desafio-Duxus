package br.com.duxusdesafio.controller;

import br.com.duxusdesafio.dto.IntegranteDto;
import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
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
        // TODO Checar se o nome e a função não são nulos

        Integrante integrante = apiService.cadastrarIntegrante(dto);
        return ResponseEntity.ok(integrante);

    }
}
