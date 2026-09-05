package br.com.duxusdesafio.dto;

import java.time.LocalDate;
import java.util.List;

public class TimeDto {

    private String nomeDoClube;
    private LocalDate data;
    private List<Long> integrantesIds;

    public String getNomeDoClube() {
        return nomeDoClube;
    }

    public void setNomeDoClube(String nomeDoClube) {
        this.nomeDoClube = nomeDoClube;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<Long> getIntegrantesIds() {
        return integrantesIds;
    }

    public void setIntegrantesIds(List<Long> integrantesIds) {
        this.integrantesIds = integrantesIds;
    }

    public boolean possuiNomeJogadoresOuDataNulo() {
        return nomeDoClube == null || data == null || integrantesIds == null;
    }

    public boolean possuiNomeOuIntegrantesVazio() {
        return nomeDoClube.isBlank() || integrantesIds.isEmpty();
    }
}
