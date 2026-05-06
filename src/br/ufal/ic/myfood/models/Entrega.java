package br.ufal.ic.myfood.models;

import java.util.List;

public class Entrega {

    private String id;
    private String idPedido;
    private String idEntregador;
    private String destino;
    private boolean concluida;

    public Entrega(String id, String idPedido, String idEntregador, String destino) {
        this.id           = id;
        this.idPedido     = idPedido;
        this.idEntregador = idEntregador;
        this.destino      = destino;
        this.concluida    = false;
    }

    public String getId()           { return id; }
    public String getIdPedido()     { return idPedido; }
    public String getIdEntregador() { return idEntregador; }
    public String getDestino()      { return destino; }
    public boolean isConcluida()    { return concluida; }

    public void concluir() { this.concluida = true; }
}
