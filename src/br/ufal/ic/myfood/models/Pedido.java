package br.ufal.ic.myfood.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Pedido {
    private String id;
    private String idCliente;
    private String idEmpresa;
    private String estado;
    private List<String> produtos;

    public Pedido(String id, String idCliente, String idEmpresa) {
        this.id = id; this.idCliente = idCliente; this.idEmpresa = idEmpresa;
        this.estado = "aberto"; this.produtos = new ArrayList<>();
    }

    public String getId()             { return id; }
    public String getIdCliente()      { return idCliente; }
    public String getIdEmpresa()      { return idEmpresa; }
    public String getEstado()         { return estado; }
    public List<String> getProdutos() { return produtos; }

    public void fechar()             { this.estado = "preparando"; }
    public void liberar()            { this.estado = "pronto"; }
    public void setEstado(String s)  { this.estado = s; }
    public void addProduto(String p) { produtos.add(p); }

    public String toJson() {
        return "{\"id\":" + JsonUtil.esc(id)
            + ", \"idCliente\":" + JsonUtil.esc(idCliente)
            + ", \"idEmpresa\":" + JsonUtil.esc(idEmpresa)
            + ", \"estado\":" + JsonUtil.esc(estado)
            + ", \"produtos\":" + JsonUtil.escLista(produtos)
            + "}";
    }

    public static Pedido fromMap(Map<String, String> m) {
        Pedido p = new Pedido(
            JsonUtil.getString(m, "id"),
            JsonUtil.getString(m, "idCliente"),
            JsonUtil.getString(m, "idEmpresa")
        );
        p.setEstado(JsonUtil.getString(m, "estado"));
        p.produtos = JsonUtil.getStringList(m, "produtos");
        return p;
    }
}
