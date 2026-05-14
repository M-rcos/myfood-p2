package br.ufal.ic.myfood.models;

import java.util.*;

public class EntregadorManager {

    private static final String PATH = "data/entregadores.json";
    private UsuarioManager usuarioManager;
    private EmpresaManager empresaManager;

    public EntregadorManager(UsuarioManager usuarioManager, EmpresaManager empresaManager) {
        this.usuarioManager = usuarioManager;
        this.empresaManager = empresaManager;
    }

    private List<String[]> carregar() {
        List<String[]> lista = new ArrayList<>();
        for (Map<String, String> m : JsonUtil.carregarLista(PATH))
            lista.add(new String[]{ JsonUtil.getString(m, "idEmpresa"),
                                    JsonUtil.getString(m, "idEntregador") });
        return lista;
    }

    private void salvar(List<String[]> lista) {
        List<String> jsons = new ArrayList<>();
        for (String[] v : lista)
            jsons.add("{\"idEmpresa\":" + JsonUtil.esc(v[0])
                    + ", \"idEntregador\":" + JsonUtil.esc(v[1]) + "}");
        JsonUtil.salvarLista(PATH, jsons);
    }

    public void cadastrarEntregador(String idEmpresa, String idEntregador) {
        empresaManager.buscarPorId(idEmpresa);
        Usuario u = usuarioManager.buscarPorId(idEntregador);
        if (!u.isEntregador())
            throw new IllegalArgumentException("Usuario nao e um entregador");
        List<String[]> lista = carregar();
        for (String[] v : lista)
            if (v[0].equals(idEmpresa) && v[1].equals(idEntregador)) return;
        lista.add(new String[]{ idEmpresa, idEntregador });
        salvar(lista);
    }

    public String getEntregadores(String idEmpresa) {
        empresaManager.buscarPorId(idEmpresa);
        List<String> emails = new ArrayList<>();
        for (String[] v : carregar())
            if (v[0].equals(idEmpresa))
                emails.add(usuarioManager.buscarPorId(v[1]).getEmail());
        return "{" + emails.toString() + "}";
    }

    public String getEmpresas(String idEntregador) {
        Usuario u = usuarioManager.buscarPorId(idEntregador);
        if (!u.isEntregador())
            throw new IllegalArgumentException("Usuario nao e um entregador");
        List<String> itens = new ArrayList<>();
        for (String[] v : carregar())
            if (v[1].equals(idEntregador)) {
                Empresa e = empresaManager.buscarPorId(v[0]);
                itens.add("[" + e.getNome() + ", " + e.getEndereco() + "]");
            }
        return "{" + itens.toString() + "}";
    }

    public Set<String> getEmpresasIds(String idEntregador) {
        Set<String> ids = new LinkedHashSet<>();
        for (String[] v : carregar())
            if (v[1].equals(idEntregador)) ids.add(v[0]);
        return ids;
    }
}
