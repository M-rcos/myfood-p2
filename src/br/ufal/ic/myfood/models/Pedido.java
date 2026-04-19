package br.ufal.ic.myfood.models;

import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private String id;
    private String idCliente;
    private String idEmpresa;
    private String estado;
    private List<String> produtos;

    public Pedido(String id, String idCliente, String idEmpresa) {
        this.id = id;
        this.idCliente = idCliente;
        this.idEmpresa = idEmpresa;
        this.estado = "aberto";
        this.produtos = new ArrayList<>();
    }

    public String getId()        { return id; }
    public String getIdCliente() { return idCliente; }
    public String getIdEmpresa() { return idEmpresa; }
    public String getEstado()    { return estado; }

    public List<String> getProdutos() { return produtos; }

    public void fechar() { this.estado = "preparando"; }

    public void addProduto(String idProduto) {
        produtos.add(idProduto);
    }

    public void removerProduto(String idProduto) {
        produtos.remove(idProduto);
    }
}
