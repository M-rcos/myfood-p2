package br.ufal.ic.myfood.models;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;

public class EntregaManager {

    private static final String PATH = "data/entregas.json";

    private UsuarioManager    usuarioManager;
    private EmpresaManager    empresaManager;
    private ProdutoManager    produtoManager;
    private PedidoManager     pedidoManager;
    private EntregadorManager entregadorManager;

    public EntregaManager(UsuarioManager um, EmpresaManager em,
                          ProdutoManager pm, PedidoManager pdm,
                          EntregadorManager etm) {
        this.usuarioManager    = um;
        this.empresaManager    = em;
        this.produtoManager    = pm;
        this.pedidoManager     = pdm;
        this.entregadorManager = etm;
    }

    // -------------------------------------------------------------------------
    // obterPedido
    // -------------------------------------------------------------------------

    public String obterPedido(String idEntregador) {
        Usuario u = usuarioManager.buscarPorId(idEntregador);
        if (!u.isEntregador()) {
            throw new IllegalArgumentException("Usuario nao e um entregador");
        }

        Set<String> empresasDoEntregador = entregadorManager.getEmpresasIds(idEntregador);
        if (empresasDoEntregador.isEmpty()) {
            throw new IllegalArgumentException("Entregador nao estar em nenhuma empresa.");
        }

        List<Pedido> prontos = pedidoManager.getPedidosProntos();

        List<Pedido> disponiveis = new ArrayList<>();
        for (Pedido p : prontos) {
            if (empresasDoEntregador.contains(p.getIdEmpresa())) {
                disponiveis.add(p);
            }
        }

        if (disponiveis.isEmpty()) {
            throw new IllegalArgumentException("Nao existe pedido para entrega");
        }

        List<Pedido> farmacias = new ArrayList<>();
        List<Pedido> outros    = new ArrayList<>();

        for (Pedido p : disponiveis) {
            Empresa e = empresaManager.buscarPorId(p.getIdEmpresa());
            if ("farmacia".equals(e.getTipoEmpresa())) {
                farmacias.add(p);
            } else {
                outros.add(p);
            }
        }

        Comparator<Pedido> porId = (a, b) -> Integer.parseInt(a.getId()) - Integer.parseInt(b.getId());
        farmacias.sort(porId);
        outros.sort(porId);

        List<Pedido> ordenados = new ArrayList<>();
        ordenados.addAll(farmacias);
        ordenados.addAll(outros);

        return ordenados.get(0).getId();
    }

    // -------------------------------------------------------------------------
    // criarEntrega — pedido é validado ANTES do entregador
    // -------------------------------------------------------------------------

    public String criarEntrega(String idPedido, String idEntregador, String destino) {

        // 1. Pedido deve estar pronto (verificado primeiro)
        Pedido p = pedidoManager.buscarPedido(idPedido);
        if (p == null || !p.getEstado().equals("pronto")) {
            throw new IllegalArgumentException("Pedido nao esta pronto para entrega");
        }

        // 2. Valida entregador
        Usuario u;
        try {
            u = usuarioManager.buscarPorId(idEntregador);
        } catch (Exception e) {
            throw new IllegalArgumentException("Nao e um entregador valido");
        }
        if (!u.isEntregador()) {
            throw new IllegalArgumentException("Nao e um entregador valido");
        }

        // 3. Entregador não pode estar em outra entrega ativa
        List<Entrega> entregas = carregar();
        for (Entrega en : entregas) {
            if (en.getIdEntregador().equals(idEntregador) && !en.isConcluida()) {
                throw new IllegalArgumentException("Entregador ainda em entrega");
            }
        }

        // Destino padrão = endereço do cliente
        if (destino == null || destino.trim().isEmpty()) {
            destino = usuarioManager.buscarPorId(p.getIdCliente()).getEndereco();
        }

        Entrega nova = new Entrega(String.valueOf(entregas.size() + 1), idPedido, idEntregador, destino);
        entregas.add(nova);
        salvar(entregas);

        pedidoManager.setEstado(idPedido, "entregando");

        return nova.getId();
    }

    // -------------------------------------------------------------------------
    // getEntrega
    // -------------------------------------------------------------------------

    public String getEntrega(String id, String atributo) {
        if (atributo == null || atributo.trim().isEmpty()) {
            throw new IllegalArgumentException("Atributo invalido");
        }

        Entrega en = buscarPorId(id);
        if (en == null) throw new IllegalArgumentException("Entrega nao encontrada");

        Pedido p = pedidoManager.buscarPedido(en.getIdPedido());

        switch (atributo) {
            case "cliente":
                return usuarioManager.buscarPorId(p.getIdCliente()).getNome();
            case "empresa":
                return empresaManager.buscarPorId(p.getIdEmpresa()).getNome();
            case "pedido":
                return en.getIdPedido();
            case "entregador":
                return usuarioManager.buscarPorId(en.getIdEntregador()).getNome();
            case "destino":
                return en.getDestino();
            case "produtos": {
                List<String> nomes = new ArrayList<>();
                for (String pid : p.getProdutos()) {
                    nomes.add(produtoManager.buscarPorId(pid).getNome());
                }
                return "{[" + String.join(", ", nomes) + "]}";
            }
            default:
                throw new IllegalArgumentException("Atributo nao existe");
        }
    }

    // -------------------------------------------------------------------------
    // getIdEntrega
    // -------------------------------------------------------------------------

    public String getIdEntrega(String idPedido) {
        for (Entrega en : carregar()) {
            if (en.getIdPedido().equals(idPedido)) return en.getId();
        }
        throw new IllegalArgumentException("Nao existe entrega com esse id");
    }

    // -------------------------------------------------------------------------
    // entregar
    // -------------------------------------------------------------------------

    public void entregar(String idEntrega) {
        List<Entrega> entregas = carregar();
        Entrega en = null;
        for (Entrega e : entregas) {
            if (e.getId().equals(idEntrega)) { en = e; break; }
        }
        if (en == null) {
            throw new IllegalArgumentException("Nao existe nada para ser entregue com esse id");
        }

        en.concluir();
        salvar(entregas);

        pedidoManager.setEstado(en.getIdPedido(), "entregue");
    }

    // -------------------------------------------------------------------------
    // Auxiliares
    // -------------------------------------------------------------------------

    private Entrega buscarPorId(String id) {
        for (Entrega en : carregar()) {
            if (en.getId().equals(id)) return en;
        }
        return null;
    }

    private List<Entrega> carregar() {
        Type tipo = new TypeToken<List<Entrega>>() {}.getType();
        List<Entrega> lista = JsonUtil.fromJsonFile(PATH, tipo);
        return (lista == null) ? new ArrayList<>() : lista;
    }

    private void salvar(List<Entrega> entregas) {
        JsonUtil.toJsonFile(PATH, entregas);
    }
}
