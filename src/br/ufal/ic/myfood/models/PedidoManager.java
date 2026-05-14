package br.ufal.ic.myfood.models;

import java.util.*;

public class PedidoManager {

    private final String PATH = "data/pedidos.json";
    private UsuarioManager usuarioManager;
    private EmpresaManager empresaManager;
    private ProdutoManager produtoManager;

    public PedidoManager(UsuarioManager u, EmpresaManager e, ProdutoManager p) {
        usuarioManager = u; empresaManager = e; produtoManager = p;
    }

    private List<Pedido> carregar() {
        List<Pedido> lista = new ArrayList<>();
        for (Map<String, String> m : JsonUtil.carregarLista(PATH))
            lista.add(Pedido.fromMap(m));
        return lista;
    }

    private void salvar(List<Pedido> lista) {
        List<String> jsons = new ArrayList<>();
        for (Pedido p : lista) jsons.add(p.toJson());
        JsonUtil.salvarLista(PATH, jsons);
    }

    public String criarPedido(String idCliente, String idEmpresa) {
        usuarioManager.buscarPorId(idCliente);
        for (Empresa e : empresaManager.getTodas())
            if (e.getIdDono().equals(idCliente))
                throw new IllegalArgumentException("Dono de empresa nao pode fazer um pedido");
        empresaManager.buscarPorId(idEmpresa);
        List<Pedido> lista = carregar();
        for (Pedido p : lista)
            if (p.getIdCliente().equals(idCliente) && p.getIdEmpresa().equals(idEmpresa)
                    && p.getEstado().equals("aberto"))
                throw new IllegalArgumentException("Nao e permitido ter dois pedidos em aberto para a mesma empresa");
        Pedido novo = new Pedido(String.valueOf(lista.size() + 1), idCliente, idEmpresa);
        lista.add(novo);
        salvar(lista);
        return novo.getId();
    }

    public void adicionarProduto(String numero, String idProduto) {
        List<Pedido> lista = carregar();
        Pedido p = buscarPorIdNaLista(lista, numero);
        if (p == null) throw new IllegalArgumentException("Nao existe pedido em aberto");
        if (!p.getEstado().equals("aberto"))
            throw new IllegalArgumentException("Nao e possivel adcionar produtos a um pedido fechado");
        Produto prod = produtoManager.buscarPorId(idProduto);
        if (!prod.getIdEmpresa().equals(p.getIdEmpresa()))
            throw new IllegalArgumentException("O produto nao pertence a essa empresa");
        p.addProduto(idProduto);
        salvar(lista);
    }

    public String getPedidos(String id, String atributo) {
        if (atributo == null || atributo.trim().isEmpty())
            throw new IllegalArgumentException("Atributo invalido");
        List<Pedido> lista = carregar();
        Pedido p = buscarPorIdNaLista(lista, id);
        if (p == null) throw new IllegalArgumentException("Pedido nao encontrado");
        switch (atributo) {
            case "cliente": return usuarioManager.buscarPorId(p.getIdCliente()).getNome();
            case "empresa":  return empresaManager.buscarPorId(p.getIdEmpresa()).getNome();
            case "estado":   return p.getEstado();
            case "produtos": {
                List<String> nomes = new ArrayList<>();
                for (String pid : p.getProdutos()) nomes.add(produtoManager.buscarPorId(pid).getNome());
                return "{[" + String.join(", ", nomes) + "]}";
            }
            case "valor": {
                double total = 0;
                for (String pid : p.getProdutos()) total += produtoManager.buscarPorId(pid).getValor();
                return String.format(java.util.Locale.US, "%.2f", total);
            }
            default: throw new IllegalArgumentException("Atributo nao existe");
        }
    }

    public void fecharPedido(String numero) {
        List<Pedido> lista = carregar();
        Pedido p = buscarPorIdNaLista(lista, numero);
        if (p == null) throw new IllegalArgumentException("Pedido nao encontrado");
        p.fechar();
        salvar(lista);
    }

    public void liberarPedido(String numero) {
        List<Pedido> lista = carregar();
        Pedido p = buscarPorIdNaLista(lista, numero);
        if (p == null) throw new IllegalArgumentException("Pedido nao encontrado");
        if (p.getEstado().equals("pronto"))
            throw new IllegalArgumentException("Pedido ja liberado");
        if (!p.getEstado().equals("preparando"))
            throw new IllegalArgumentException("Nao e possivel liberar um produto que nao esta sendo preparado");
        p.liberar();
        salvar(lista);
    }

    public String getNumeroPedido(String idCliente, String idEmpresa, int indice) {
        List<String> ids = new ArrayList<>();
        for (Pedido p : carregar())
            if (p.getIdCliente().equals(idCliente) && p.getIdEmpresa().equals(idEmpresa))
                ids.add(p.getId());
        ids.sort((a, b) -> Integer.parseInt(a) - Integer.parseInt(b));
        if (indice < 0 || indice >= ids.size())
            throw new IllegalArgumentException("Pedido nao encontrado");
        return ids.get(indice);
    }

    public void removerProduto(String idPedido, String nome) {
        if (nome == null || nome.trim().isEmpty())
            throw new IllegalArgumentException("Produto invalido");
        List<Pedido> lista = carregar();
        Pedido p = buscarPorIdNaLista(lista, idPedido);
        if (p == null) throw new IllegalArgumentException("Pedido nao encontrado");
        if (!p.getEstado().equals("aberto"))
            throw new IllegalArgumentException("Nao e possivel remover produtos de um pedido fechado");
        List<String> prods = p.getProdutos();
        for (int i = 0; i < prods.size(); i++) {
            if (produtoManager.buscarPorId(prods.get(i)).getNome().equals(nome)) {
                prods.remove(i);
                salvar(lista);
                return;
            }
        }
        throw new IllegalArgumentException("Produto nao encontrado");
    }

    public List<Pedido> getPedidosProntos() {
        List<Pedido> result = new ArrayList<>();
        for (Pedido p : carregar())
            if (p.getEstado().equals("pronto")) result.add(p);
        return result;
    }

    public void setEstado(String idPedido, String novoEstado) {
        List<Pedido> lista = carregar();
        Pedido p = buscarPorIdNaLista(lista, idPedido);
        if (p == null) throw new IllegalArgumentException("Pedido nao encontrado");
        p.setEstado(novoEstado);
        salvar(lista);
    }

    public Pedido buscarPedido(String id) {
        return buscarPorIdNaLista(carregar(), id);
    }

    private Pedido buscarPorIdNaLista(List<Pedido> lista, String id) {
        for (Pedido p : lista)
            if (p.getId().equals(id)) return p;
        return null;
    }
}
