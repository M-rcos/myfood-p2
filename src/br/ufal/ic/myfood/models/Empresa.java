package br.ufal.ic.myfood.models;

public class Empresa {

    private String id;
    private String nome;
    private String endereco;
    private String idDono;
    private String tipoEmpresa;

    // Restaurante
    private String tipoCozinha;

    // Mercado
    private String abre;
    private String fecha;
    private String tipoMercado;

    // Farmácia
    private boolean aberto24Horas;
    private int numeroFuncionarios;

    // Construtor restaurante
    public Empresa(String id, String nome, String endereco, String tipoCozinha, String idDono) {
        this.id          = id;
        this.nome        = nome;
        this.endereco    = endereco;
        this.tipoCozinha = tipoCozinha;
        this.idDono      = idDono;
        this.tipoEmpresa = "restaurante";
    }

    // Construtor mercado
    public Empresa(String id, String nome, String endereco, String idDono,
                   String tipoEmpresa, String abre, String fecha, String tipoMercado) {
        this.id          = id;
        this.nome        = nome;
        this.endereco    = endereco;
        this.idDono      = idDono;
        this.tipoEmpresa = tipoEmpresa;
        this.abre        = abre;
        this.fecha       = fecha;
        this.tipoMercado = tipoMercado;
    }

    // Construtor farmácia
    public Empresa(String id, String nome, String endereco, String idDono,
                   String tipoEmpresa, boolean aberto24Horas, int numeroFuncionarios) {
        this.id                 = id;
        this.nome               = nome;
        this.endereco           = endereco;
        this.idDono             = idDono;
        this.tipoEmpresa        = tipoEmpresa;
        this.aberto24Horas      = aberto24Horas;
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
}
