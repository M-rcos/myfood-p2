package br.ufal.ic.myfood.models;

import java.util.Map;

public class Empresa {
    private String id;
    private String nome;
    private String endereco;
    private String idDono;
    private String tipoEmpresa;
    private String tipoCozinha;
    private String abre;
    private String fecha;
    private String tipoMercado;
    private boolean aberto24Horas;
    private int numeroFuncionarios;

    public Empresa(String id, String nome, String endereco, String tipoCozinha, String idDono) {
        this.id = id; this.nome = nome; this.endereco = endereco;
        this.tipoCozinha = tipoCozinha; this.idDono = idDono;
        this.tipoEmpresa = "restaurante";
    }

    public Empresa(String id, String nome, String endereco, String idDono,
                   String tipoEmpresa, String abre, String fecha, String tipoMercado) {
        this.id = id; this.nome = nome; this.endereco = endereco; this.idDono = idDono;
        this.tipoEmpresa = tipoEmpresa; this.abre = abre; this.fecha = fecha;
        this.tipoMercado = tipoMercado;
    }

    public Empresa(String id, String nome, String endereco, String idDono,
                   String tipoEmpresa, boolean aberto24Horas, int numeroFuncionarios) {
        this.id = id; this.nome = nome; this.endereco = endereco; this.idDono = idDono;
        this.tipoEmpresa = tipoEmpresa; this.aberto24Horas = aberto24Horas;
        this.numeroFuncionarios = numeroFuncionarios;
    }

    public String getId()               { return id; }
    public String getNome()             { return nome; }
    public String getEndereco()         { return endereco; }
    public String getIdDono()           { return idDono; }
    public String getTipoEmpresa()      { return tipoEmpresa; }
    public String getTipoCozinha()      { return tipoCozinha; }
    public String getAbre()             { return abre; }
    public String getFecha()            { return fecha; }
    public String getTipoMercado()      { return tipoMercado; }
    public boolean isAberto24Horas()    { return aberto24Horas; }
    public int getNumeroFuncionarios()  { return numeroFuncionarios; }

    public void setAbre(String abre)    { this.abre = abre; }
    public void setFecha(String fecha)  { this.fecha = fecha; }

    public String toJson() {
        return "{\"id\":" + JsonUtil.esc(id)
            + ", \"nome\":" + JsonUtil.esc(nome)
            + ", \"endereco\":" + JsonUtil.esc(endereco)
            + ", \"idDono\":" + JsonUtil.esc(idDono)
            + ", \"tipoEmpresa\":" + JsonUtil.esc(tipoEmpresa)
            + ", \"tipoCozinha\":" + JsonUtil.esc(tipoCozinha)
            + ", \"abre\":" + JsonUtil.esc(abre)
            + ", \"fecha\":" + JsonUtil.esc(fecha)
            + ", \"tipoMercado\":" + JsonUtil.esc(tipoMercado)
            + ", \"aberto24Horas\":" + aberto24Horas
            + ", \"numeroFuncionarios\":" + numeroFuncionarios
            + "}";
    }

    public static Empresa fromMap(Map<String, String> m) {
        String id                = JsonUtil.getString(m, "id");
        String nome              = JsonUtil.getString(m, "nome");
        String endereco          = JsonUtil.getString(m, "endereco");
        String idDono            = JsonUtil.getString(m, "idDono");
        String tipoEmpresa       = JsonUtil.getString(m, "tipoEmpresa");
        String tipoCozinha       = JsonUtil.getString(m, "tipoCozinha");
        String abre              = JsonUtil.getString(m, "abre");
        String fecha             = JsonUtil.getString(m, "fecha");
        String tipoMercado       = JsonUtil.getString(m, "tipoMercado");
        boolean aberto24Horas    = JsonUtil.getBoolean(m, "aberto24Horas");
        int numeroFuncionarios   = JsonUtil.getInt(m, "numeroFuncionarios");

        if ("mercado".equals(tipoEmpresa)) {
            return new Empresa(id, nome, endereco, idDono, tipoEmpresa, abre, fecha, tipoMercado);
        } else if ("farmacia".equals(tipoEmpresa)) {
            return new Empresa(id, nome, endereco, idDono, tipoEmpresa, aberto24Horas, numeroFuncionarios);
        } else {
            return new Empresa(id, nome, endereco, tipoCozinha, idDono);
        }
    }
}
