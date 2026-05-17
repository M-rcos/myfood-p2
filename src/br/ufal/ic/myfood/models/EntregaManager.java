package br.ufal.ic.myfood.models;

import br.ufal.ic.myfood.exceptions.MyFoodException;
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

    private List<Entrega> carregar() {
        List<Entrega> lista = new ArrayList<>();
        for (Map<String, String> m : JsonUtil.carregarLista(PATH))
            lista.add(Entrega.fromMap(m));
        return lista;
    }

    private void salvar(List<Entrega> lista) {
        List<String> jsons = new ArrayList<>();
        for (Entrega e : lista) jsons.add(e.toJson());
        JsonUtil.salvarLista(PATH, jsons);
    }

    public String obterPedido(String idEntregador) {
        Usuario u = usuarioManager.buscarPorId(idEntregador);
        if (!u.isEntregador())
            throw new MyFoodException("Usuario nao e um entregador");

        Set<String> empresasDoEntregador = entregadorManager.getEmpresasIds(idEntregador);
        if (empresasDoEntregador.isEmpty())
            throw new MyFoodException("Entregador nao estar em nenhuma empresa.");

        List<Pedido> disponiveis = new ArrayList<>();
        for (Pedido p : pedidoManager.getPedidosProntos())
            if (empresasDoEntregador.contains(p.getIdEmpresa()))
                disponiveis.add(p);

        if (disponiveis.isEmpty())
            throw new MyFoodException("Nao existe pedido para entrega");

        List<Pedido> farmacias = new ArrayList<>(), outros = new ArrayList<>();
        for (Pedido p : disponiveis) {
            Empresa e = empresaManager.buscarPorId(p.getIdEmpresa());
            if ("farmacia".equals(e.getTipoEmpresa())) farmacias.add(p);
            else outros.add(p);
        }

        Comparator<Pedido> porId = (a, b) -> Integer.parseInt(a.getId()) - Integer.parseInt(b.getId());
        farmacias.sort(porId);
        outros.sort(porId);

        List<Pedido> ordenados = new ArrayList<>();
        ordenados.addAll(farmacias);
        ordenados.addAll(outros);

        return ordenados.get(0).getId();
    }

    public String criarEntrega(String idPedido, String idEntregador, String destino) {
        Pedido p = pedidoManager.buscarPedido(idPedido);
        if (p == null || !p.getEstado().equals("pronto"))
            throw new MyFoodException("Pedido nao esta pronto para entrega");

        Usuario u;
        try { u = usuarioManager.buscarPorId(idEntregador); }
        catch (Exception e) { throw new MyFoodException("Nao e um entregador valido"); }
        if (!u.isEntregador())
            throw new MyFoodException("Nao e um entregador valido");

        List<Entrega> entregas = carregar();
        for (Entrega en : entregas)
            if (en.getIdEntregador().equals(idEntregador) && !en.isConcluida())
                throw new MyFoodException("Entregador ainda em entrega");

        if (destino == null || destino.trim().isEmpty())
            destino = usuarioManager.buscarPorId(p.getIdCliente()).getEndereco();

        Entrega nova = new Entrega(String.valueOf(entregas.size() + 1), idPedido, idEntregador, destino);
        entregas.add(nova);
        salvar(entregas);
        pedidoManager.setEstado(idPedido, "entregando");
        return nova.getId();
    }

    public String getEntrega(String id, String atributo) {
        if (atributo == null || atributo.trim().isEmpty())
            throw new MyFoodException("Atributo invalido");

        Entrega en = buscarPorId(id);
        if (en == null) throw new MyFoodException("Entrega nao encontrada");
        Pedido p = pedidoManager.buscarPedido(en.getIdPedido());

        switch (atributo) {
            case "cliente":    return usuarioManager.buscarPorId(p.getIdCliente()).getNome();
            case "empresa":    return empresaManager.buscarPorId(p.getIdEmpresa()).getNome();
            case "pedido":     return en.getIdPedido();
            case "entregador": return usuarioManager.buscarPorId(en.getIdEntregador()).getNome();
            case "destino":    return en.getDestino();
            case "produtos": {
                List<String> nomes = new ArrayList<>();
                for (String pid : p.getProdutos())
                    nomes.add(produtoManager.buscarPorId(pid).getNome());
                return "{[" + String.join(", ", nomes) + "]}";
            }
            default: throw new MyFoodException("Atributo nao existe");
        }
    }

    public String getIdEntrega(String idPedido) {
        for (Entrega en : carregar())
            if (en.getIdPedido().equals(idPedido)) return en.getId();
        throw new MyFoodException("Nao existe entrega com esse id");
    }

    public void entregar(String idEntrega) {
        List<Entrega> entregas = carregar();
        Entrega en = null;
        for (Entrega e : entregas)
            if (e.getId().equals(idEntrega)) { en = e; break; }
        if (en == null)
            throw new MyFoodException("Nao existe nada para ser entregue com esse id");
        en.concluir();
        salvar(entregas);
        pedidoManager.setEstado(en.getIdPedido(), "entregue");
    }

    private Entrega buscarPorId(String id) {
        for (Entrega en : carregar())
            if (en.getId().equals(id)) return en;
        return null;
    }
}
