package br.ufal.ic.myfood.models;

import java.util.*;

public class ProdutoManager {

    private final String PATH = "data/produtos.json";
    private EmpresaManager empresaManager;

    public ProdutoManager(EmpresaManager empresaManager) {
        this.empresaManager = empresaManager;
    }

    private List<Produto> carregar() {
        List<Produto> lista = new ArrayList<>();
        for (Map<String, String> m : JsonUtil.carregarLista(PATH))
            lista.add(Produto.fromMap(m));
        return lista;
    }

    private void salvar(List<Produto> lista) {
        List<String> jsons = new ArrayList<>();
        for (Produto p : lista) jsons.add(p.toJson());
        JsonUtil.salvarLista(PATH, jsons);
    }

    public String criarProduto(String idEmpresa, String nome, double valor, String categoria) {
        validar(nome, valor, categoria);
        empresaManager.buscarPorId(idEmpresa);
        List<Produto> lista = carregar();
        for (Produto p : lista)
            if (p.getIdEmpresa().equals(idEmpresa) && p.getNome().equals(nome))
                throw new IllegalArgumentException("Ja existe um produto com esse nome para essa empresa");
        Produto novo = new Produto(String.valueOf(lista.size() + 1), idEmpresa, nome, valor, categoria);
        lista.add(novo);
        salvar(lista);
        return novo.getId();
    }

    public void editarProduto(String idProduto, String nome, double valor, String categoria) {
        validar(nome, valor, categoria);
        List<Produto> lista = carregar();
        Produto p = buscarPorIdNaLista(lista, idProduto);
        p.setNome(nome); p.setValor(valor); p.setCategoria(categoria);
        salvar(lista);
    }

    public String getProduto(String nome, String idEmpresa, String atributo) {
        if (atributo == null || atributo.trim().isEmpty())
            throw new IllegalArgumentException("Atributo nao existe");
        Produto p = buscarPorNomeEmpresa(nome, idEmpresa);
        switch (atributo) {
            case "nome":      return p.getNome();
            case "valor":     return String.format(java.util.Locale.US, "%.2f", p.getValor());
            case "categoria": return p.getCategoria();
            case "empresa":   return empresaManager.buscarPorId(idEmpresa).getNome();
            default: throw new IllegalArgumentException("Atributo nao existe");
        }
    }

    public String listarProdutos(String idEmpresa) {
        try { empresaManager.buscarPorId(idEmpresa); }
        catch (Exception e) { throw new IllegalArgumentException("Empresa nao encontrada"); }
        List<String> nomes = new ArrayList<>();
        for (Produto p : carregar())
            if (p.getIdEmpresa().equals(idEmpresa)) nomes.add(p.getNome());
        return "{[" + String.join(", ", nomes) + "]}";
    }

    public Produto buscarPorId(String id) {
        for (Produto p : carregar())
            if (p.getId().equals(id)) return p;
        throw new IllegalArgumentException("Produto nao cadastrado");
    }

    private Produto buscarPorIdNaLista(List<Produto> lista, String id) {
        for (Produto p : lista)
            if (p.getId().equals(id)) return p;
        throw new IllegalArgumentException("Produto nao cadastrado");
    }

    private Produto buscarPorNomeEmpresa(String nome, String idEmpresa) {
        for (Produto p : carregar())
            if (p.getIdEmpresa().equals(idEmpresa) && p.getNome().equals(nome)) return p;
        throw new IllegalArgumentException("Produto nao encontrado");
    }

    private void validar(String nome, double valor, String categoria) {
        if (nome == null || nome.trim().isEmpty())
            throw new IllegalArgumentException("Nome invalido");
        if (valor < 0)
            throw new IllegalArgumentException("Valor invalido");
        if (categoria == null || categoria.trim().isEmpty())
            throw new IllegalArgumentException("Categoria invalido");
    }
}
