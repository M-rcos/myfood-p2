package br.ufal.ic.myfood.models;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PedidoManager {

    private static final String PATH = "data/pedidos.json";

    private UsuarioManager  usuarioManager;
    private EmpresaManager  empresaManager;
    private ProdutoManager  produtoManager;

    public PedidoManager(UsuarioManager u, EmpresaManager e, ProdutoManager p) {
        usuarioManager = u;
        empresaManager = e;
        produtoManager = p;
    }

    // criarPedido

    public String criarPedido(String idCliente, String idEmpresa) {
        usuarioManager.buscarPorId(idCliente);

        for (Empresa e : empresaManager.getTodas()) {
            if (e.getIdDono().equals(idCliente)) {
                throw new IllegalArgumentException("Dono de empresa nao pode fazer um pedido");
            }
        }

        empresaManager.buscarPorId(idEmpresa);

        List<Pedido> pedidos = carregar();
        for (Pedido p : pedidos) {
            if (p.getIdCliente().equals(idCliente)
                    && p.getIdEmpresa().equals(idEmpresa)
                    && p.getEstado().equals("aberto")) {
                throw new IllegalArgumentException("Nao e permitido ter dois pedidos em aberto para a mesma empresa");
            }
        }

        List<Pedido> todos = carregar();
        Pedido novo = new Pedido(String.valueOf(todos.size() + 1), idCliente, idEmpresa);
        todos.add(novo);
        salvar(todos);
        return novo.getId();
    }

    // adicionarProduto

    public void adicionarProduto(String numero, String idProduto) {
        List<Pedido> pedidos = carregar();
        Pedido p = buscarPorId(pedidos, numero);

        if (p == null) throw new IllegalArgumentException("Nao existe pedido em aberto");
        if (!p.getEstado().equals("aberto"))
            throw new IllegalArgumentException("Nao e possivel adcionar produtos a um pedido fechado");

        Produto prod = produtoManager.buscarPorId(idProduto);
        if (!prod.getIdEmpresa().equals(p.getIdEmpresa()))
            throw new IllegalArgumentException("O produto nao pertence a essa empresa");

        p.addProduto(idProduto);
        salvar(pedidos);
    }

    // getPedidos

    public String getPedidos(String id, String atributo) {
        if (atributo == null || atributo.trim().isEmpty())
            throw new IllegalArgumentException("Atributo invalido");

        List<Pedido> pedidos = carregar();
        Pedido p = buscarPorId(pedidos, id);
        if (p == null) throw new IllegalArgumentException("Pedido nao encontrado");

        switch (atributo) {
            case "cliente":
                return usuarioManager.buscarPorId(p.getIdCliente()).getNome();
            case "empresa":
                return empresaManager.buscarPorId(p.getIdEmpresa()).getNome();
            case "estado":
                return p.getEstado();
            case "produtos": {
                List<String> nomes = new ArrayList<>();
                for (String pid : p.getProdutos())
                    nomes.add(produtoManager.buscarPorId(pid).getNome());
                return "{[" + String.join(", ", nomes) + "]}";
            }
            case "valor": {
                double total = 0;
                for (String pid : p.getProdutos())
                    total += produtoManager.buscarPorId(pid).getValor();
                return String.format(Locale.US, "%.2f", total);
            }
            default:
                throw new IllegalArgumentException("Atributo nao existe");
        }
    }

    // fecharPedido

    public void fecharPedido(String numero) {
        List<Pedido> pedidos = carregar();
        Pedido p = buscarPorId(pedidos, numero);
        if (p == null) throw new IllegalArgumentException("Pedido nao encontrado");
        p.fechar();
        salvar(pedidos);
    }

    // liberarPedido

    public void liberarPedido(String numero) {
        List<Pedido> pedidos = carregar();
        Pedido p = buscarPorId(pedidos, numero);
        if (p == null) throw new IllegalArgumentException("Pedido nao encontrado");

        if (p.getEstado().equals("pronto")) {
            throw new IllegalArgumentException("Pedido ja liberado");
        }
        if (!p.getEstado().equals("preparando")) {
            throw new IllegalArgumentException("Nao e possivel liberar um produto que nao esta sendo preparado");
        }

        p.liberar();
        salvar(pedidos);
    }

    // getNumeroPedido

    public String getNumeroPedido(String idCliente, String idEmpresa, int indice) {
        List<String> ids = new ArrayList<>();
        for (Pedido p : carregar()) {
            if (p.getIdCliente().equals(idCliente) && p.getIdEmpresa().equals(idEmpresa))
                ids.add(p.getId());
        }
        Collections.sort(ids, (a, b) -> Integer.parseInt(a) - Integer.parseInt(b));
        if (indice < 0 || indice >= ids.size())
            throw new IllegalArgumentException("Pedido nao encontrado");
        return ids.get(indice);
    }

    // removerProduto

    public void removerProduto(String idPedido, String nome) {
        if (nome == null || nome.trim().isEmpty())
            throw new IllegalArgumentException("Produto invalido");

        List<Pedido> pedidos = carregar();
        Pedido p = buscarPorId(pedidos, idPedido);
        if (p == null) throw new IllegalArgumentException("Pedido nao encontrado");
        if (!p.getEstado().equals("aberto"))
            throw new IllegalArgumentException("Nao e possivel remover produtos de um pedido fechado");

        List<String> lista = p.getProdutos();
        for (int i = 0; i < lista.size(); i++) {
            if (produtoManager.buscarPorId(lista.get(i)).getNome().equals(nome)) {
                lista.remove(i);
                salvar(pedidos);
                return;
            }
        }
        throw new IllegalArgumentException("Produto nao encontrado");
    }

    // Suporte para EntregaManager

    public List<Pedido> getPedidosProntos() {
        List<Pedido> result = new ArrayList<>();
        for (Pedido p : carregar()) {
            if (p.getEstado().equals("pronto")) result.add(p);
        }
        return result;
    }

    public void setEstado(String idPedido, String novoEstado) {
        List<Pedido> pedidos = carregar();
        Pedido p = buscarPorId(pedidos, idPedido);
        if (p == null) throw new IllegalArgumentException("Pedido nao encontrado");
        p.setEstado(novoEstado);
        salvar(pedidos);
    }

    public Pedido buscarPedido(String id) {
        return buscarPorId(carregar(), id);
    }

    // Auxiliares

    private Pedido buscarPorId(List<Pedido> pedidos, String id) {
        for (Pedido p : pedidos) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    private List<Pedido> carregar() {
        Type tipo = new com.google.gson.reflect.TypeToken<List<Pedido>>() {}.getType();
        List<Pedido> lista = JsonUtil.fromJsonFile(PATH, tipo);
        return (lista == null) ? new ArrayList<>() : lista;
    }

    private void salvar(List<Pedido> pedidos) {
        JsonUtil.toJsonFile(PATH, pedidos);
    }
}
