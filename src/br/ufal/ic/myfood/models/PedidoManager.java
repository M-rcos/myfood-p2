package br.ufal.ic.myfood.models;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PedidoManager {

    private List<Pedido> pedidos;
    private int proximoId;

    private UsuarioManager usuarioManager;
    private EmpresaManager empresaManager;
    private ProdutoManager produtoManager;

    private static final String PATH = "data/pedidos.json";

    public PedidoManager(UsuarioManager usuarioManager,
                         EmpresaManager empresaManager,
                         ProdutoManager produtoManager) {
        this.usuarioManager  = usuarioManager;
        this.empresaManager  = empresaManager;
        this.produtoManager  = produtoManager;
        this.pedidos         = carregar();
        this.proximoId       = pedidos.size() + 1;
    }

    // criarPedido

    public String criarPedido(String idCliente, String idEmpresa) {

        // Valida que o cliente existe
        Usuario cliente = usuarioManager.buscarPorId(idCliente);

        // Dono de empresa não pode fazer pedido
        if (empresaManager.isDonoDeEmpresa(idCliente)) {
            throw new IllegalArgumentException("Dono de empresa nao pode fazer um pedido");
        }

        // Empresa deve existir
        empresaManager.buscarPorId(idEmpresa);

        // Sem dois pedidos abertos para a mesma empresa
        for (Pedido p : pedidos) {
            if (p.getIdCliente().equals(idCliente)
                    && p.getIdEmpresa().equals(idEmpresa)
                    && p.getEstado().equals("aberto")) {
                throw new IllegalArgumentException("Nao e permitido ter dois pedidos em aberto para a mesma empresa");
            }
        }

        Pedido novo = new Pedido(String.valueOf(proximoId++), idCliente, idEmpresa);
        pedidos.add(novo);
        salvar();
        return novo.getId();
    }

    // adicionarProduto

    public void adicionarProduto(String numero, String idProduto) {

        Pedido p = buscarPorId(numero);

        if (p == null) {
            throw new IllegalArgumentException("Nao existe pedido em aberto");
        }

        if (!p.getEstado().equals("aberto")) {
            throw new IllegalArgumentException("Nao e possivel adcionar produtos a um pedido fechado");
        }

        Produto prod = produtoManager.buscarPorId(idProduto);

        if (!prod.getIdEmpresa().equals(p.getIdEmpresa())) {
            throw new IllegalArgumentException("O produto nao pertence a essa empresa");
        }

        p.addProduto(idProduto);
        salvar();
    }

    // getPedidos

    public String getPedidos(String pedido, String atributo) {

        if (atributo == null || atributo.trim().isEmpty()) {
            throw new IllegalArgumentException("Atributo invalido");
        }

        Pedido p = buscarPorId(pedido);
        if (p == null) {
            throw new IllegalArgumentException("Pedido nao encontrado");
        }

        switch (atributo) {

            case "cliente":
                return usuarioManager.buscarPorId(p.getIdCliente()).getNome();

            case "empresa":
                return empresaManager.buscarPorId(p.getIdEmpresa()).getNome();

            case "estado":
                return p.getEstado();

            case "produtos": {
                List<String> nomes = new ArrayList<>();
                for (String idProd : p.getProdutos()) {
                    nomes.add(produtoManager.buscarPorId(idProd).getNome());
                }
                return "{[" + String.join(", ", nomes) + "]}";
            }

            case "valor": {
                double total = 0;
                for (String idProd : p.getProdutos()) {
                    total += produtoManager.buscarPorId(idProd).getValor();
                }
                return String.format(Locale.US, "%.2f", total);
            }

            default:
                throw new IllegalArgumentException("Atributo nao existe");
        }
    }

    // fecharPedido

    public void fecharPedido(String numero) {
        Pedido p = buscarPorId(numero);
        if (p == null) {
            throw new IllegalArgumentException("Pedido nao encontrado");
        }
        p.fechar();
        salvar();
    }

    // getNumeroPedido

    public String getNumeroPedido(String idCliente, String idEmpresa, int indice) {

        List<String> ids = new ArrayList<>();

        for (Pedido p : pedidos) {
            if (p.getIdCliente().equals(idCliente) && p.getIdEmpresa().equals(idEmpresa)) {
                ids.add(p.getId());
            }
        }

        if (indice < 0 || indice >= ids.size()) {
            throw new IllegalArgumentException("Pedido nao encontrado");
        }

        // IDs são numéricos sequenciais — ordenar garante "mais antigo primeiro"
        ids.sort((a, b) -> Integer.parseInt(a) - Integer.parseInt(b));

        return ids.get(indice);
    }

    // removerProduto

    public void removerProduto(String pedido, String nomeProduto) {

        if (nomeProduto == null || nomeProduto.trim().isEmpty()) {
            throw new IllegalArgumentException("Produto invalido");
        }

        Pedido p = buscarPorId(pedido);
        if (p == null) {
            throw new IllegalArgumentException("Pedido nao encontrado");
        }

        if (!p.getEstado().equals("aberto")) {
            throw new IllegalArgumentException("Nao e possivel remover produtos de um pedido fechado");
        }

        List<String> lista = p.getProdutos();
        for (int i = 0; i < lista.size(); i++) {
            if (produtoManager.buscarPorId(lista.get(i)).getNome().equals(nomeProduto)) {
                lista.remove(i);
                salvar();
                return;
            }
        }

        throw new IllegalArgumentException("Produto nao encontrado");
    }

    // Auxiliares

    private Pedido buscarPorId(String id) {
        for (Pedido p : pedidos) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    private List<Pedido> carregar() {
        try {
            Type tipo = new TypeToken<List<Pedido>>() {}.getType();
            List<Pedido> lista = JsonUtil.fromJsonFile(PATH, tipo);
            return (lista == null) ? new ArrayList<>() : lista;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void salvar() {
        JsonUtil.toJsonFile(PATH, pedidos);
    }
}
