package br.ufal.ic.myfood;

import br.ufal.ic.myfood.models.*;

public class Facade {

    private UsuarioManager    usuarioManager;
    private EmpresaManager    empresaManager;
    private ProdutoManager    produtoManager;
    private PedidoManager     pedidoManager;
    private EntregadorManager entregadorManager;
    private EntregaManager    entregaManager;

    public Facade() { init(); }

    private void init() {
        usuarioManager    = new UsuarioManager();
        empresaManager    = new EmpresaManager(usuarioManager);
        produtoManager    = new ProdutoManager(empresaManager);
        pedidoManager     = new PedidoManager(usuarioManager, empresaManager, produtoManager);
        entregadorManager = new EntregadorManager(usuarioManager, empresaManager);
        entregaManager    = new EntregaManager(usuarioManager, empresaManager, produtoManager,
                                               pedidoManager, entregadorManager);
    }

    public void zerarSistema() {
        JsonUtil.deleteFile("data/usuarios.json");
        JsonUtil.deleteFile("data/empresas.json");
        JsonUtil.deleteFile("data/produtos.json");
        JsonUtil.deleteFile("data/pedidos.json");
        JsonUtil.deleteFile("data/entregadores.json");
        JsonUtil.deleteFile("data/entregas.json");
        init();
    }

    public void encerrarSistema() {}

    // USUÁRIO
    public void criarUsuario(String nome, String email, String senha, String endereco) {
        usuarioManager.criarUsuario(nome, email, senha, endereco);
    }
    public void criarUsuario(String nome, String email, String senha, String endereco, String cpf) {
        usuarioManager.criarUsuario(nome, email, senha, endereco, cpf);
    }
    public void criarUsuario(String nome, String email, String senha,
                             String endereco, String veiculo, String placa) {
        usuarioManager.criarUsuario(nome, email, senha, endereco, veiculo, placa);
    }
    public String login(String email, String senha) { return usuarioManager.login(email, senha); }
    public String getAtributoUsuario(String id, String atributo) throws Exception {
        return usuarioManager.getAtributoUsuario(id, atributo);
    }

    // EMPRESA
    public String criarEmpresa(String tipoEmpresa, String dono, String nome,
                               String endereco, String tipoCozinha) {
        return empresaManager.criarEmpresa(tipoEmpresa, dono, nome, endereco, tipoCozinha);
    }
    public String criarEmpresa(String tipoEmpresa, String dono, String nome,
                               String endereco, String abre, String fecha, String tipoMercado) {
        return empresaManager.criarEmpresa(tipoEmpresa, dono, nome, endereco, abre, fecha, tipoMercado);
    }
    public String criarEmpresa(String tipoEmpresa, String dono, String nome,
                               String endereco, boolean aberto24Horas, int numeroFuncionarios) {
        return empresaManager.criarEmpresa(tipoEmpresa, dono, nome, endereco, aberto24Horas, numeroFuncionarios);
    }
    public String getEmpresasDoUsuario(String idDono) { return empresaManager.getEmpresasDoUsuario(idDono); }
    public String getIdEmpresa(String idDono, String nome, int indice) {
        return empresaManager.getIdEmpresa(idDono, nome, indice);
    }
    public String getAtributoEmpresa(String empresa, String atributo) {
        return empresaManager.getAtributoEmpresa(empresa, atributo);
    }
    public void alterarFuncionamento(String mercado, String abre, String fecha) {
        empresaManager.alterarFuncionamento(mercado, abre, fecha);
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
    public String listarProdutos(String empresa) { return produtoManager.listarProdutos(empresa); }

    // PEDIDO
    public String criarPedido(String cliente, String empresa) {
        return pedidoManager.criarPedido(cliente, empresa);
    }
    public void adicionarProduto(String numero, String produto) {
        pedidoManager.adicionarProduto(numero, produto);
    }
    public String getPedidos(String pedido, String atributo) {
        return pedidoManager.getPedidos(pedido, atributo);
    }
    public void fecharPedido(String numero) { pedidoManager.fecharPedido(numero); }
    public void liberarPedido(String numero) { pedidoManager.liberarPedido(numero); }
    public String getNumeroPedido(String cliente, String empresa, int indice) {
        return pedidoManager.getNumeroPedido(cliente, empresa, indice);
    }
    public void removerProduto(String pedido, String produto) {
        pedidoManager.removerProduto(pedido, produto);
    }

    // ENTREGADOR
    public void cadastrarEntregador(String empresa, String entregador) {
        entregadorManager.cadastrarEntregador(empresa, entregador);
    }
    public String getEntregadores(String empresa) { return entregadorManager.getEntregadores(empresa); }
    public String getEmpresas(String entregador)  { return entregadorManager.getEmpresas(entregador); }

    // ENTREGA
    public String obterPedido(String entregador)  { return entregaManager.obterPedido(entregador); }
    public String criarEntrega(String pedido, String entregador, String destino) {
        return entregaManager.criarEntrega(pedido, entregador, destino);
    }
    public String getEntrega(String id, String atributo) { return entregaManager.getEntrega(id, atributo); }
    public String getIdEntrega(String pedido)  { return entregaManager.getIdEntrega(pedido); }
    public void entregar(String entrega)       { entregaManager.entregar(entrega); }
}
