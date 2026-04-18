package br.ufal.ic.myfood.models;

public class Empresa {

    private String id;
    private String nome;
    private String endereco;
    private String tipoCozinha;
    private String idDono;

    public Empresa(String id, String nome, String endereco, String tipoCozinha, String idDono) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.tipoCozinha = tipoCozinha;
        this.idDono = idDono;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public String getTipoCozinha() { return tipoCozinha; }
    public String getIdDono() { return idDono; }
}
