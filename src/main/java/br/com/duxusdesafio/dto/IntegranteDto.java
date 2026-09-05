package br.com.duxusdesafio.dto;

public class IntegranteDto {

    private String nome;
    private String funcao;

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean possuiNomeOuFuncaoNulo() {
        return nome == null || funcao == null;
    }

    public boolean possuiNomeOuFuncaoVazio() {
        return nome.isBlank() || funcao.isBlank();
    }
}
