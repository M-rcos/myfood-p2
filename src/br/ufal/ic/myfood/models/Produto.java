package br.ufal.ic.myfood.models;

import java.util.Map;

public class Produto {
    private String id;
    private String idEmpresa;
    private String nome;
    private double valor;
    private String categoria;

    public Produto(String id, String idEmpresa, String nome, double valor, String categoria) {
        this.id = id; this.idEmpresa = idEmpresa; this.nome = nome;
        this.valor = valor; this.categoria = categoria;
    }

    public String getId()         { return id; }
    public String getIdEmpresa()  { return idEmpresa; }
    public String getNome()       { return nome; }
    public double getValor()      { return valor; }
    public String getCategoria()  { return categoria; }

    public void setNome(String nome)         { this.nome = nome; }
    public void setValor(double valor)       { this.valor = valor; }
    public void setCategoria(String cat)     { this.categoria = cat; }

    public String toJson() {
        return "{\"id\":" + JsonUtil.esc(id)
            + ", \"idEmpresa\":" + JsonUtil.esc(idEmpresa)
            + ", \"nome\":" + JsonUtil.esc(nome)
            + ", \"valor\":" + valor
            + ", \"categoria\":" + JsonUtil.esc(categoria)
            + "}";
    }

    public static Produto fromMap(Map<String, String> m) {
        return new Produto(
            JsonUtil.getString(m, "id"),
            JsonUtil.getString(m, "idEmpresa"),
            JsonUtil.getString(m, "nome"),
            JsonUtil.getDouble(m, "valor"),
            JsonUtil.getString(m, "categoria")
        );
    }
}
