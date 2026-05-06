package br.ufal.ic.myfood.models;

public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String senha;
    private String endereco;

    // Dono de empresa
    private String cpf;

    // Entregador
    private String veiculo;
    private String placa;

    public Usuario(String id, String nome, String email, String senha, String endereco, String cpf) {
        this.id       = id;
        this.nome     = nome;
        this.email    = email;
        this.senha    = senha;
        this.endereco = endereco;
        this.cpf      = cpf;
    }

    // Construtor entregador
    public Usuario(String id, String nome, String email, String senha,
                   String endereco, String veiculo, String placa) {
        this.id       = id;
        this.nome     = nome;
        this.email    = email;
        this.senha    = senha;
        this.endereco = endereco;
        this.veiculo  = veiculo;
        this.placa    = placa;
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
}
