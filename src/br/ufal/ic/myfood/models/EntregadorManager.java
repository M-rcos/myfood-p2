package br.ufal.ic.myfood.models;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;

public class EntregadorManager {

    private static final String PATH = "data/entregadores.json";

    private UsuarioManager usuarioManager;
    private EmpresaManager empresaManager;

    private static class Vinculo {
        String idEmpresa;
        String idEntregador;

        Vinculo(String idEmpresa, String idEntregador) {
            this.idEmpresa    = idEmpresa;
            this.idEntregador = idEntregador;
        }
    }

    public EntregadorManager(UsuarioManager usuarioManager, EmpresaManager empresaManager) {
        this.usuarioManager = usuarioManager;
        this.empresaManager = empresaManager;
    }

    // -------------------------------------------------------------------------
    // cadastrarEntregador
    // -------------------------------------------------------------------------

    public void cadastrarEntregador(String idEmpresa, String idEntregador) {
        empresaManager.buscarPorId(idEmpresa);

        Usuario u = usuarioManager.buscarPorId(idEntregador);
        if (!u.isEntregador()) {
            throw new IllegalArgumentException("Usuario nao e um entregador");
        }

        List<Vinculo> vinculos = carregar();
        for (Vinculo v : vinculos) {
            if (v.idEmpresa.equals(idEmpresa) && v.idEntregador.equals(idEntregador)) {
                return; // já vinculado
            }
        }

        vinculos.add(new Vinculo(idEmpresa, idEntregador));
        salvar(vinculos);
    }

    // -------------------------------------------------------------------------
    // getEntregadores — lista de emails
    // -------------------------------------------------------------------------

    public String getEntregadores(String idEmpresa) {
        empresaManager.buscarPorId(idEmpresa);

        List<String> emails = new ArrayList<>();
        for (Vinculo v : carregar()) {
            if (v.idEmpresa.equals(idEmpresa)) {
                emails.add(usuarioManager.buscarPorId(v.idEntregador).getEmail());
            }
        }
        return "{" + emails.toString() + "}";
    }

    // -------------------------------------------------------------------------
    // getEmpresas — lista de [nome, endereco]
    // -------------------------------------------------------------------------

    public String getEmpresas(String idEntregador) {
        Usuario u = usuarioManager.buscarPorId(idEntregador);
        if (!u.isEntregador()) {
            throw new IllegalArgumentException("Usuario nao e um entregador");
        }

        List<String> itens = new ArrayList<>();
        for (Vinculo v : carregar()) {
            if (v.idEntregador.equals(idEntregador)) {
                Empresa e = empresaManager.buscarPorId(v.idEmpresa);
                itens.add("[" + e.getNome() + ", " + e.getEndereco() + "]");
            }
        }
        return "{" + itens.toString() + "}";
    }

    // -------------------------------------------------------------------------
    // getEmpresasIds — usado pelo EntregaManager para saber quais empresas
    // um entregador cobre (retorna Set<String> de ids)
    // -------------------------------------------------------------------------

    public Set<String> getEmpresasIds(String idEntregador) {
        Set<String> ids = new LinkedHashSet<>();
        for (Vinculo v : carregar()) {
            if (v.idEntregador.equals(idEntregador)) {
                ids.add(v.idEmpresa);
            }
        }
        return ids;
    }

    // -------------------------------------------------------------------------
    // JSON
    // -------------------------------------------------------------------------

    private List<Vinculo> carregar() {
        Type tipo = new TypeToken<List<Vinculo>>() {}.getType();
        List<Vinculo> lista = JsonUtil.fromJsonFile(PATH, tipo);
        return (lista == null) ? new ArrayList<>() : lista;
    }

    private void salvar(List<Vinculo> vinculos) {
        JsonUtil.toJsonFile(PATH, vinculos);
    }
}
