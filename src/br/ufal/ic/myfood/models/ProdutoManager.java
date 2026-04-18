package br.ufal.ic.myfood.models;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProdutoManager {

    private List<Produto> produtos;
    private int proximoId;
    private EmpresaManager empresaManager;

    private static final String PATH = "data/produtos.json";

    public ProdutoManager(EmpresaManager empresaManager) {
        this.empresaManager = empresaManager;
        this.produtos = carregar();
        this.proximoId = produtos.size() + 1;
    }

    public String criarProduto(String idEmpresa, String nome, double valor, String categoria) {

        validar(nome, valor, categoria);

        Empresa empresa = empresaManager.buscarPorId(idEmpresa);

        for (Produto p : produtos) {
            if (p.getIdEmpresa().equals(idEmpresa) && p.getNome().equals(nome)) {
                throw new IllegalArgumentException("Ja existe um produto com esse nome para essa empresa");
            }
        }

        Produto novo = new Produto(
                String.valueOf(proximoId++),
                idEmpresa,
                nome,
                valor,
                categoria
        );

        produtos.add(novo);
        salvar();

        return novo.getId();
    }

    public void editarProduto(String idProduto, String nome, double valor, String categoria) {

        validar(nome, valor, categoria);

        Produto p = buscarPorId(idProduto);

        p.setNome(nome);
        p.setValor(valor);
        p.setCategoria(categoria);

        salvar();
    }

    public String getProduto(String nome, String idEmpresa, String atributo) {

        Produto p = buscarPorNomeEempresa(nome, idEmpresa);

        if (atributo == null || atributo.trim().isEmpty()) {
            throw new IllegalArgumentException("Atributo nao existe");
        }

        switch (atributo) {
            case "nome":
                return p.getNome();
            case "valor":
                return String.format(Locale.US, "%.2f", p.getValor());
            case "categoria":
                return p.getCategoria();
            case "empresa":
                return empresaManager.buscarPorId(idEmpresa).getNome();
            default:
                throw new IllegalArgumentException("Atributo nao existe");
        }
    }

    public String listarProdutos(String idEmpresa) {

        try {
            empresaManager.buscarPorId(idEmpresa);
        } catch (Exception e) {
            throw new IllegalArgumentException("Empresa nao encontrada");
        }

        List<String> nomes = new ArrayList<>();

        for (Produto p : produtos) {
            if (p.getIdEmpresa().equals(idEmpresa)) {
                nomes.add(p.getNome());
            }
        }

        return "{[" + String.join(", ", nomes) + "]}";
    }

    // AUXILIARES

    private Produto buscarPorId(String id) {
        for (Produto p : produtos) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Produto nao cadastrado");
    }

    private Produto buscarPorNomeEempresa(String nome, String idEmpresa) {

        for (Produto p : produtos) {
            if (p.getIdEmpresa().equals(idEmpresa) && p.getNome().equals(nome)) {
                return p;
            }
        }

        throw new IllegalArgumentException("Produto nao encontrado");
    }

    private void validar(String nome, double valor, String categoria) {

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome invalido");
        }

        if (valor < 0) {
            throw new IllegalArgumentException("Valor invalido");
        }

        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Categoria invalido");
        }
    }

    // JSON

    private List<Produto> carregar() {
        try {
            Type tipo = new TypeToken<List<Produto>>() {}.getType();
            return JsonUtil.fromJsonFile(PATH, tipo);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void salvar() {
        JsonUtil.toJsonFile(PATH, produtos);
    }
}
