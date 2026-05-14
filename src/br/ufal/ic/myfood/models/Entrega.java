package br.ufal.ic.myfood.models;

import java.util.Map;

public class Entrega {
    private String id;
    private String idPedido;
    private String idEntregador;
    private String destino;
    private boolean concluida;

    public Entrega(String id, String idPedido, String idEntregador, String destino) {
        this.id = id; this.idPedido = idPedido;
        this.idEntregador = idEntregador; this.destino = destino;
        this.concluida = false;
    }

    public String getId()           { return id; }
    public String getIdPedido()     { return idPedido; }
    public String getIdEntregador() { return idEntregador; }
    public String getDestino()      { return destino; }
    public boolean isConcluida()    { return concluida; }
    public void concluir()          { this.concluida = true; }

    public String toJson() {
        return "{\"id\":" + JsonUtil.esc(id)
            + ", \"idPedido\":" + JsonUtil.esc(idPedido)
            + ", \"idEntregador\":" + JsonUtil.esc(idEntregador)
            + ", \"destino\":" + JsonUtil.esc(destino)
            + ", \"concluida\":" + concluida
            + "}";
    }

    public static Entrega fromMap(Map<String, String> m) {
        Entrega e = new Entrega(
            JsonUtil.getString(m, "id"),
            JsonUtil.getString(m, "idPedido"),
            JsonUtil.getString(m, "idEntregador"),
            JsonUtil.getString(m, "destino")
        );
        if (JsonUtil.getBoolean(m, "concluida")) e.concluir();
        return e;
    }
}
