package br.ufal.ic.myfood.models;

public class Produto {

    private String id;
    private String idEmpresa;
    private String nome;
    private double valor;
    private String categoria;

    public Produto(String id, String idEmpresa, String nome, double valor, String categoria) {
        this.id = id;
        this.idEmpresa = idEmpresa;
        this.nome = nome;
        this.valor = valor;
        this.categoria = categoria;
    }

    public String getId() { return id; }
    public String getIdEmpresa() { return idEmpresa; }
    public String getNome() { return nome; }
    public double getValor() { return valor; }
    public String getCategoria() { return categoria; }

    public void setNome(String nome) { this.nome = nome; }
    public void setValor(double valor) { this.valor = valor; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
