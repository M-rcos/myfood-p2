package br.ufal.ic.myfood;

import br.ufal.ic.myfood.models.*;

public class Facade {

    private UsuarioManager usuarioManager;
    private EmpresaManager empresaManager;
    private ProdutoManager produtoManager;

    public Facade() {
        this.usuarioManager = new UsuarioManager();
        this.empresaManager = new EmpresaManager(usuarioManager);
        this.produtoManager = new ProdutoManager(empresaManager);
    }

    public void zerarSistema() {
        JsonUtil.deleteFile("data/usuarios.json");
        JsonUtil.deleteFile("data/empresas.json");
        JsonUtil.deleteFile("data/produtos.json");

        this.usuarioManager = new UsuarioManager();
        this.empresaManager = new EmpresaManager(usuarioManager);
        this.produtoManager = new ProdutoManager(empresaManager);
    }

    public void encerrarSistema() {
    }

    // USUÁRIO

    public void criarUsuario(String nome, String email, String senha, String endereco) {
        usuarioManager.criarUsuario(nome, email, senha, endereco, null);
    }

    public void criarUsuario(String nome, String email, String senha, String endereco, String cpf) {
        usuarioManager.criarUsuario(nome, email, senha, endereco, cpf);
    }

    public String login(String email, String senha) {
        return usuarioManager.login(email, senha);
    }

    public String getAtributoUsuario(String id, String atributo) throws Exception {
        return usuarioManager.getAtributoUsuario(id, atributo);
    }

    // EMPRESA

    public String criarEmpresa(String tipoEmpresa, String dono, String nome, String endereco, String tipoCozinha) {
        return empresaManager.criarEmpresa(tipoEmpresa, dono, nome, endereco, tipoCozinha);
    }

    public String getEmpresasDoUsuario(String idDono) {
        return empresaManager.getEmpresasDoUsuario(idDono);
    }

    public String getIdEmpresa(String idDono, String nome, int indice) {
        return empresaManager.getIdEmpresa(idDono, nome, indice);
    }

    public String getAtributoEmpresa(String empresa, String atributo) {
        return empresaManager.getAtributoEmpresa(empresa, atributo);
    }

    // PRODUTO

    public String criarProduto(String empresa, String nome, double valor, String categoria) {
        return produtoManager.criarProduto(empresa, nome, valor, categoria);
    }

    public void editarProduto(String produto, String nome, double valor, String categoria) {
        produtoManager.editarProduto(produto, nome, valor, categoria);
    }

    public String getProduto(String nome, String empresa, String atributo) {
        return produtoManager.getProduto(nome, empresa, atributo);
    }

    public String listarProdutos(String empresa) {
        return produtoManager.listarProdutos(empresa);
    }
}
