package br.ufal.ic.myfood.models;

import java.util.Map;

public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String senha;
    private String endereco;
    private String cpf;
    private String veiculo;
    private String placa;

    public Usuario(String id, String nome, String email, String senha, String endereco, String cpf) {
        this.id = id; this.nome = nome; this.email = email;
        this.senha = senha; this.endereco = endereco; this.cpf = cpf;
    }

    public Usuario(String id, String nome, String email, String senha,
                   String endereco, String veiculo, String placa) {
        this.id = id; this.nome = nome; this.email = email;
        this.senha = senha; this.endereco = endereco;
        this.veiculo = veiculo; this.placa = placa;
    }

    public String getId()       { return id; }
    public String getNome()     { return nome; }
    public String getEmail()    { return email; }
    public String getSenha()    { return senha; }
    public String getEndereco() { return endereco; }
    public String getCpf()      { return cpf; }
    public String getVeiculo()  { return veiculo; }
    public String getPlaca()    { return placa; }

    public void setId(String id)           { this.id = id; }
    public void setNome(String nome)       { this.nome = nome; }
    public void setEmail(String email)     { this.email = email; }
    public void setSenha(String senha)     { this.senha = senha; }
    public void setEndereco(String e)      { this.endereco = e; }
    public void setCpf(String cpf)         { this.cpf = cpf; }

    public boolean isEntregador() {
        return veiculo != null && !veiculo.trim().isEmpty()
            && placa   != null && !placa.trim().isEmpty();
    }

    public String toJson() {
        return "{\"id\":" + JsonUtil.esc(id)
            + ", \"nome\":" + JsonUtil.esc(nome)
            + ", \"email\":" + JsonUtil.esc(email)
            + ", \"senha\":" + JsonUtil.esc(senha)
            + ", \"endereco\":" + JsonUtil.esc(endereco)
            + ", \"cpf\":" + JsonUtil.esc(cpf)
            + ", \"veiculo\":" + JsonUtil.esc(veiculo)
            + ", \"placa\":" + JsonUtil.esc(placa)
            + "}";
    }

    public static Usuario fromMap(Map<String, String> m) {
        String id       = JsonUtil.getString(m, "id");
        String nome     = JsonUtil.getString(m, "nome");
        String email    = JsonUtil.getString(m, "email");
        String senha    = JsonUtil.getString(m, "senha");
        String endereco = JsonUtil.getString(m, "endereco");
        String cpf      = JsonUtil.getString(m, "cpf");
        String veiculo  = JsonUtil.getString(m, "veiculo");
        String placa    = JsonUtil.getString(m, "placa");

        if (veiculo != null && placa != null) {
            return new Usuario(id, nome, email, senha, endereco, veiculo, placa);
        }
        return new Usuario(id, nome, email, senha, endereco, cpf);
    }
}
